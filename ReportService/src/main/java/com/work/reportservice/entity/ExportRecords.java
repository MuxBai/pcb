package com.work.reportservice.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExportRecords {
    private Integer exportId;
    private Integer reportId;
    private String userId;
    private LocalDateTime  exportedAt;
}
