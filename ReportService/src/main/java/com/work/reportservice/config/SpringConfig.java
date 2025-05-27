package com.work.reportservice.config;

import com.work.commonconfig.config.DatabaseConfig;
import com.work.commonconfig.config.ListenerConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@MapperScan("com.work.reportservice.mapper")
@EnableDiscoveryClient
@Import({DatabaseConfig.class, ListenerConfig.class})
public class SpringConfig {
}
