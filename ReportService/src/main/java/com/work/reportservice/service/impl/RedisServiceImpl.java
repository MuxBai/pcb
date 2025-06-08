package com.work.reportservice.service.impl;

import com.work.reportservice.dto.ReportViewCountDTO;
import com.work.reportservice.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REPORT_VIEW_COUNT_KEY = "report:view:count";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Pcb检测计数
    @Override
    public void increaseCountForUser(String userId) {
        String dateKey = "pcb:count:" + LocalDate.now().format(FORMATTER);
        Long expire = redisTemplate.getExpire(dateKey);

        redisTemplate.opsForHash().increment(dateKey, userId, 1);

        // 设置为 30天过期
        if (expire == null || expire == -1) {
            redisTemplate.expire(dateKey, Duration.ofDays(30));
        }
    }

    // 获取某日Pcb检测统计数据
    @Override
    public Map<Object, Object> getStatsByDate(String date) {
        String key = "pcb:count:" + date;
        return redisTemplate.opsForHash().entries(key);
    }

    // 导出报告表计数
    @Override
    public void increaseExportCountForUser(String userId) {
        String dateKey = "export:count:" + LocalDate.now().format(FORMATTER);
        Long expire = redisTemplate.getExpire(dateKey);

        redisTemplate.opsForHash().increment(dateKey, userId, 1);

        // 设置为 30天过期
        if (expire == null || expire == -1) {
            redisTemplate.expire(dateKey, Duration.ofDays(30));
        }
    }

    // 查询某天导出统计
    @Override
    public Map<Object, Object> getExportStatsByDate(String date) {
        String key = "export:count:" + date;
        return redisTemplate.opsForHash().entries(key);
    }

    // 增加报告访问量
    @Override
    public void incrementReportViewCount(String reportId) {
        String dateKey = "report:view:count:" + LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        redisTemplate.opsForZSet().incrementScore(dateKey, reportId, 1);

        Long expire = redisTemplate.getExpire(dateKey);

        // 设置为 30天过期
        if (expire == null || expire == -1) {
            redisTemplate.expire(dateKey, Duration.ofDays(30));
        }
    }

    // 获取前N个访问量最高报告
    public List<ReportViewCountDTO> getTopViewedReportsWithCount(String date,int topN) {
        String dateKey = "report:view:count:" + date;

        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(dateKey, 0, topN - 1);

        if (typedTuples == null || typedTuples.isEmpty()) {
            return Collections.emptyList();
        }

        List<ReportViewCountDTO> result = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : typedTuples) {
            String reportId = (String) tuple.getValue();
            Double score = tuple.getScore();
            result.add(new ReportViewCountDTO(reportId, score));
        }
        return result;
    }
}

