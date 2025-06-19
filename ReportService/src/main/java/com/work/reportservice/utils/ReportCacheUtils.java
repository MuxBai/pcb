package com.work.reportservice.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ReportCacheUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void deleteKeysByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void deleteCacheBySerialNumbers(Set<String> serialNumbers) {
        for (String serial : serialNumbers) {
            deleteKeysByPattern("report:" + serial + ":*");
        }
    }

    public void deleteCacheByReportIds(Set<Integer> reportIds) {
        for (Integer id : reportIds) {
            redisTemplate.delete("report:" + id);
        }
    }

    public void deleteAllPagedCache() {
        deleteKeysByPattern("report:all:*");
    }

    public void clearCachesAfterReportChange(Set<Integer> reportIds, Set<String> serialNumbers) {
        deleteCacheByReportIds(reportIds);
        deleteCacheBySerialNumbers(serialNumbers);
        deleteAllPagedCache();
    }
}