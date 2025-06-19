package com.work.detectionservice.utils;

import com.work.detectionservice.entity.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductCacheUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void deleteKeysByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void deleteRelatedProductCaches(List<Products> products) {
        if (products == null || products.isEmpty()) return;

        Set<String> userIds = products.stream()
                .map(Products::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Integer> defectLevels = products.stream()
                .map(Products::getDefectLevel)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 清除 userId 分页缓存
        for (String userId : userIds) {
            deleteKeysByPattern("product:userId:" + userId + ":*");
        }

        // 清除 defectLevel 分页缓存
        for (Integer level : defectLevels) {
            deleteKeysByPattern("product:defectLevel:" + level + ":*");
        }

        // 清除所有产品分页缓存
        deleteKeysByPattern("product:all:*");
    }
}