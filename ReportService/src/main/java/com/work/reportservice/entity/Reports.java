package com.work.reportservice.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reports {
    private Integer reportId;
    private String content;
    private LocalDateTime createdAt;
    private String frontDefectImg;
    private String backDefectImg;
    private String serialNumber;
}
