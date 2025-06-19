package com.work.reportservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.work.commonconfig.config.filter.JwtFilter;
import com.work.commonconfig.exception.MyException;
import com.work.reportservice.entity.ExportRecords;
import com.work.reportservice.mapper.ExportRecordMapper;
import com.work.reportservice.service.ExportRecordService;
import com.work.reportservice.utils.ExportCacheUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class ExportRecordServiceImpl implements ExportRecordService {
    @Autowired
    private ExportRecordMapper exportRecordMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ExportCacheUtils exportCacheUtils;

    // 查询某一管理员获取的所有报告记录
    @Override
    public List<ExportRecords> getExportRecordsByUserIdPaged(String userId, String token, int page, int pageSize) {
        // 校验 token
        if (!JwtFilter.isValidToken(token)) {
            MyException.throwError("token过期或无效", 401);
        }

        // 校验权限（仅限管理员）
        Integer role = JwtFilter.getUserRoleFromToken(token);
        if (role != 2) {
            MyException.throwError("权限不足", 403);
        }

        // Redis 缓存 key
        String key = String.format("export:userId:%s:page:%d:size:%d", userId, page, pageSize);

        // 查询缓存
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            String json = cached.toString();
            return JSON.parseArray(json, ExportRecords.class);
        }

        // 缓存未命中，查询数据库
        int offset = (page - 1) * pageSize;
        List<ExportRecords> list = exportRecordMapper.getExportRecordsByUserId(userId, pageSize, offset);

        // 写入缓存，设置过期时间60秒
        redisTemplate.opsForValue().set(key, JSON.toJSONString(list), 60, TimeUnit.SECONDS);

        return list;
    }

    @Override
    public int countExportRecordsByUserId(String userId) {
        return exportRecordMapper.countExportRecordsByUserId(userId);
    }


    // 查询某一报告被查询的记录
    @Override
    public List<ExportRecords> getExportRecordsByReportIdPaged(Integer reportId, String token, int page, int pageSize) {
        // 校验 token
        if (!JwtFilter.isValidToken(token)) {
            MyException.throwError("token过期或无效", 401);
        }

        // 校验权限（仅限管理员）
        Integer role = JwtFilter.getUserRoleFromToken(token);
        if (role != 2) {
            MyException.throwError("权限不足", 403);
        }

        // Redis 缓存 key
        String key = String.format("export:reportId:%d:page:%d:size:%d", reportId, page, pageSize);

        // 读取缓存
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            String json = cached.toString();
            return JSON.parseArray(json, ExportRecords.class);
        }

        // 缓存未命中，查询数据库
        int offset = (page - 1) * pageSize;
        List<ExportRecords> list = exportRecordMapper.getExportRecordsByReportId(reportId, pageSize, offset);

        // 写入 Redis，设置60秒过期
        redisTemplate.opsForValue().set(key, JSON.toJSONString(list), 60, TimeUnit.SECONDS);

        return list;
    }

    @Override
    public int countExportRecordsByReportId(Integer reportId) {
        return exportRecordMapper.countExportRecordsByReportId(reportId);
    }

    // 获取某一导出报告表的信息
    @Override
    public ExportRecords getExportRecordByExportId(Integer exportId, String token) {
        if(!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效",401);
        Integer role = JwtFilter.getUserRoleFromToken(token);
        if(role != 2)
            MyException.throwError("权限不足",403);
        return exportRecordMapper.getExportRecordByExportId(exportId);
    }

    // 分页获取报告查询记录
    @Override
    public List<ExportRecords> getxportReportsByPage(int page, int pageSize, String token) {
        // 校验 token
        if (!JwtFilter.isValidToken(token)) {
            MyException.throwError("token过期或无效", 401);
        }

        // 校验权限（仅限管理员）
        Integer role = JwtFilter.getUserRoleFromToken(token);
        if (role != 2) {
            MyException.throwError("权限不足", 403);
        }

        // Redis 缓存 key
        String key = String.format("export:all:page:%d:size:%d", page, pageSize);

        // 查询 Redis 缓存
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            String json = cached.toString();
            return JSON.parseArray(json, ExportRecords.class);
        }

        // 缓存未命中，查询数据库
        int offset = (page - 1) * pageSize;
        List<ExportRecords> list = exportRecordMapper.findAllExportReport(pageSize, offset);

        // 写入 Redis 缓存，设置60秒过期
        redisTemplate.opsForValue().set(key, JSON.toJSONString(list), 60, TimeUnit.SECONDS);

        return list;
    }


    // 获取全部记录数
    @Override
    public Integer countExportReport(){
        return exportRecordMapper.countExportReport();
    }

    // 根据export_id删除导出记录表
    @Override
    public boolean deleteByExportIds(List<Integer> exportIds, String token) {
        if (!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效", 401);

        if (exportIds == null || exportIds.isEmpty()) {
            MyException.throwError("删除失败：导出记录ID列表不能为空", 400);
        }

        String lockKey = "export:delete:lock:" + exportIds.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                // 先查出对应 userId 和 reportId
                Set<String> userIds = new HashSet<>(exportRecordMapper.findUserIdsByExportIds(exportIds));
                Set<Integer> reportIds = new HashSet<>(exportRecordMapper.findReportIdsByExportIds(exportIds));

                int result = exportRecordMapper.deleteByExportId(exportIds);

                if (result > 0) {
                    exportCacheUtils.clearCachesAfterExportDelete(userIds, reportIds, new HashSet<>(exportIds));
                    return true;
                } else {
                    return false;
                }
            } else {
                MyException.throwError("导出记录正在被其他用户操作，请稍后重试", 429);
                return false;
            }
        } catch (Exception e) {
            MyException.throwError("删除导出记录失败：" + e.getMessage(), 500);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 根据report_id删除导出记录表
    @Override
    public boolean deleteByReportIds(List<Integer> reportIds, String token) {
        if (!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效", 401);

        if (reportIds == null || reportIds.isEmpty()) {
            MyException.throwError("删除失败：报告ID列表不能为空", 400);
        }

        String lockKey = "report:delete:lock:" + reportIds.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                Set<String> userIds = new HashSet<>(exportRecordMapper.findUserIdsByReportIds(reportIds));
                Set<Integer> exportIds = new HashSet<>(exportRecordMapper.findExportIdsByReportIds(reportIds));

                int result = exportRecordMapper.deleteByReportId(reportIds);

                if (result > 0) {
                    exportCacheUtils.clearCachesAfterExportDelete(userIds, new HashSet<>(reportIds), exportIds);
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
