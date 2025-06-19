package com.work.reportservice.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ExportCacheUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void deleteKeysByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void deleteCacheByUserId(String userId) {
        deleteKeysByPattern("export:userId:" + userId + ":*");
    }

    public void deleteCacheByReportIds(Set<Integer> reportIds) {
        for (Integer id : reportIds) {
            deleteKeysByPattern("export:reportId:" + id + ":*");
        }
    }

    public void deleteCacheByExportIds(Set<Integer> exportIds) {
        for (Integer id : exportIds) {
            redisTemplate.delete("export:exportId:" + id); // 如果你有这种结构的 key
        }
    }

    public void deleteAllPagedCache() {
        deleteKeysByPattern("export:all:*");
    }

    public void clearCachesAfterExportDelete(Set<String> userIds, Set<Integer> reportIds, Set<Integer> exportIds) {
        if (userIds != null) {
            for (String uid : userIds) {
                deleteCacheByUserId(uid);
            }
        }
        if (reportIds != null) {
            deleteCacheByReportIds(reportIds);
        }
        if (exportIds != null) {
            deleteCacheByExportIds(exportIds);
        }
        deleteAllPagedCache();
    }
}