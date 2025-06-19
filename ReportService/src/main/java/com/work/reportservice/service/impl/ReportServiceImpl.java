package com.work.reportservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.commonconfig.config.filter.JwtFilter;
import com.work.commonconfig.exception.MyException;
import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.dto.OutReport;
import com.work.reportservice.entity.ExportRecords;
import com.work.reportservice.entity.Reports;
import com.work.reportservice.mapper.ExportRecordMapper;
import com.work.reportservice.mapper.ReportMapper;
import com.work.reportservice.service.ReportService;
import com.work.reportservice.utils.ExportCacheUtils;
import com.work.reportservice.utils.ReportCacheUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private ExportRecordMapper exportRecordMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportCacheUtils reportCacheUtils;

    @Autowired
    private ExportCacheUtils exportCacheUtils;

    // 根据id查询报告
    @Override
    public Reports findByReportId(Integer reportId, String token) {
        if(!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效",401);
        Integer role = JwtFilter.getUserRoleFromToken(token);
        if(role !=2)
            MyException.throwError("权限不足",403);
        return reportMapper.findByReportId(reportId);
    }

    // 根据产品序列号查询报告
    @Override
    public List<Reports> findReportBySerialNumberPaged(String serialNumber, int page, int pageSize, String token) {
        if (!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效", 401);
        Integer role = JwtFilter.getUserRoleFromToken(token);
        if (role != 2) {
            MyException.throwError("权限不足", 403);
        }

        String key = String.format("report:%s:page:%d:size:%d", serialNumber, page, pageSize);

        // 从 Redis 取缓存
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            String json = cached.toString();
            return JSON.parseArray(json, Reports.class);
        }

        // 计算分页偏移量
        int offset = (page - 1) * pageSize;
        List<Reports> list = reportMapper.findReportBySerialNumber(serialNumber, pageSize, offset);

        // 缓存到 Redis，设置过期时间60秒
        redisTemplate.opsForValue().set(key, JSON.toJSONString(list), 60, TimeUnit.SECONDS);

        return list;
    }
    @Override
    public int countReportsBySerialNumber(String serialNumber) {
        return reportMapper.countReportsBySerialNumber(serialNumber);
    }

    // 修改报告
    @Override
    public Boolean changeReport(ChangeReport changeReport, String token) {
        if (!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效", 401);

        Integer role = JwtFilter.getUserRoleFromToken(token);
        if (role != 2)
            MyException.throwError("权限不足", 403);

        Integer reportId = changeReport.getReportId();
        if (reportId == null) {
            MyException.throwError("修改失败：报告ID不能为空", 400);
        }

        String lockKey = "report:change:lock:" + reportId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                // 获取该报告的序列号
                Reports report = reportMapper.findByReportId(reportId);
                String serial = report.getSerialNumber();

                Boolean success = reportMapper.changeReport(changeReport);
                if (success) {
                    reportCacheUtils.clearCachesAfterReportChange(
                            Set.of(reportId), Set.of(serial)
                    );

                }
                return success;
            } else {
                MyException.throwError("报告正在被其他用户修改，请稍后重试", 429);
                return false;
            }
        } catch (Exception e) {
            MyException.throwError("修改报告失败：" + e.getMessage(), 500);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 生成报告
    @Override
    public Boolean insertReport(Reports reports) {
        boolean result = reportMapper.insertReport(reports);
        if (result) {
            // 新增报告后清除相关缓存
            Set<Integer> reportIds = Set.of(reports.getReportId());
            Set<String> serialNumbers = Set.of(reports.getSerialNumber());
            reportCacheUtils.clearCachesAfterReportChange(reportIds, serialNumbers);
        }
        return result;
    }

    // 分页获取全部报告
    @Override
    public List<Reports> getReportsByPage(int page, int pageSize, String token) {
        // 校验 token
        if (!JwtFilter.isValidToken(token)) {
            MyException.throwError("token过期或无效", 401);
        }

        // 校验权限（仅限管理员）
        Integer role = JwtFilter.getUserRoleFromToken(token);
        if (role != 2) {
            MyException.throwError("权限不足", 403);
        }

        // 构造 Redis 缓存 key
        String key = String.format("report:all:page:%d:size:%d", page, pageSize);

        // 查询缓存
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            String json = cached.toString();
            return JSON.parseArray(json, Reports.class);
        }

        // 缓存未命中则查询数据库
        int offset = (page - 1) * pageSize;
        List<Reports> list = reportMapper.findAllReport(pageSize, offset);

        // 写入缓存，有效期60秒
        redisTemplate.opsForValue().set(key, JSON.toJSONString(list), 60, TimeUnit.SECONDS);

        return list;
    }


    // 获取报告总数
    @Override
    public Integer getReportCount(){
        return reportMapper.countReports();
    }

    // 导出报告（转换为csv类型）
    @Override
    public byte[] exportReportsAsCsv(List<Integer> reportIds, String token) {
        if (!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效", 401);
        Integer role = JwtFilter.getUserRoleFromToken(token);
        String userId = JwtFilter.getUserIdFromToken(token);
        if (role != 2)
            MyException.throwError("权限不足", 403);

        // 记录导出
        List<Integer> exportIds = new ArrayList<>();
        for (Integer reportId : reportIds) {
            ExportRecords exportReport = new ExportRecords();
            exportReport.setUserId(userId);
            exportReport.setReportId(reportId);
            exportReport.setExportedAt(LocalDateTime.now());
            exportRecordMapper.insertExportRecord(exportReport);
            exportIds.add(exportReport.getExportId()); // 前提是 insert 时回填主键
        }

        // 清理导出记录相关缓存
        exportCacheUtils.clearCachesAfterExportDelete(
                Set.of(userId),
                new HashSet<>(reportIds),
                new HashSet<>(exportIds)
        );

        List<OutReport> reports = new ArrayList<>();

        for (Integer reportId : reportIds) {
            String redisKey = "report:" + reportId;

            // 直接取Object，RedisTemplate用的json序列化，自动转成LinkedHashMap，需要转成OutReport
            Object cachedObj = redisTemplate.opsForValue().get(redisKey);

            if (cachedObj != null) {
                try {
                    // cachedObj 实际上是 LinkedHashMap，不能直接强转，需要用 ObjectMapper 转换
                    OutReport report = objectMapper.convertValue(cachedObj, OutReport.class);
                    reports.add(report);
                } catch (Exception e) {
                    redisTemplate.delete(redisKey);
                }
            } else {
                OutReport report = reportMapper.findExportReportById(reportId);
                if (report != null) {
                    reports.add(report);
                    try {
                        // 缓存，过期30分钟
                        redisTemplate.opsForValue().set(redisKey, report, 30, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        // 忽略缓存失败异常
                    }
                }
            }
        }

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("content,createdAt,frontDefectImg,backDefectImg,serialNumber\n");

        for (OutReport report : reports) {
            csvBuilder.append("\"").append(report.getContent()).append("\",")
                    .append(report.getCreatedAt()).append(",")
                    .append(report.getFrontDefectImg()).append(",")
                    .append(report.getBackDefectImg()).append(",")
                    .append(report.getSerialNumber()).append("\n");
        }

        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    // 根据report_id删除报告
    @Override
    public boolean deleteReportsByIds(List<Integer> reportIds, String token) {
        if (!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效", 401);

        if (reportIds == null || reportIds.isEmpty()) {
            MyException.throwError("删除失败：报告ID列表不能为空", 400);
        }

        String lockKey = "report:delete:lock:" + reportIds.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                // 查出删除前的序列号
                Set<String> serials = reportMapper.findSerialsByReportIds(reportIds);

                int result = reportMapper.deleteByReportId(reportIds);

                if (result > 0) {
                    reportCacheUtils.clearCachesAfterReportChange(
                            new HashSet<>(reportIds), serials
                    );
                }

                return result > 0;
            } else {
                MyException.throwError("报告正在被其他用户操作，请稍后重试", 429);
                return false;
            }
        } catch (Exception e) {
            MyException.throwError("删除报告失败：" + e.getMessage(), 500);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }



    // 根据serial_number删除报告
    @Override
    public boolean deleteReportsBySerialNumbers(List<String> serialNumbers, String token) {
        if (!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效", 401);

    /*Integer role = JwtFilter.getUserRoleFromToken(token);
    if (role != 2)
        MyException.throwError("权限不足", 403);*/

        if (serialNumbers == null || serialNumbers.isEmpty()) {
            MyException.throwError("删除失败：序列号列表不能为空", 400);
        }

        String lockKey = "report:delete:serial:lock:" + serialNumbers.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                Set<Integer> reportIds = new HashSet<>(reportMapper.findDistinctReportIdsBySerialNumbers(serialNumbers));
                int result = reportMapper.deleteBySerialNumber(serialNumbers);

                if (result > 0) {
                    // 删除 Redis 缓存
                    reportCacheUtils.clearCachesAfterReportChange(
                            reportIds,
                            new HashSet<>(serialNumbers)
                    );
                    return true;
                } else {
                    return false;
                }
            } else {
                MyException.throwError("报告正在被其他用户操作，请稍后重试", 429);
                return false;
            }
        } catch (Exception e) {
            MyException.throwError("删除报告失败：" + e.getMessage(), 500);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
