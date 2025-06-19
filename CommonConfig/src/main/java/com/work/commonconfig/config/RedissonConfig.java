package com.work.commonconfig.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 单机模式，地址换成你的 Redis 地址
        config.useSingleServer()
                .setAddress("redis://81.71.96.88:6379")
                .setDatabase(0);
        return Redisson.create(config);
    }
}
