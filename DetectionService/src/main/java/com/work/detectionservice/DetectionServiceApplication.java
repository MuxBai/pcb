package com.work.detectionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class DetectionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DetectionServiceApplication.class, args);
    }

}
