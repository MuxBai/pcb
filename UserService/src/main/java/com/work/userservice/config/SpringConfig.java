package com.work.userservice.config;

import com.work.commonconfig.config.DatabaseConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@MapperScan("com.work.userservice.mapper")
@EnableDiscoveryClient
@Import(DatabaseConfig.class)
public class SpringConfig {
}
