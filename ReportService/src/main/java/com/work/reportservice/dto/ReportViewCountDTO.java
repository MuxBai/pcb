package com.work.reportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportViewCountDTO {
    private String reportId;
    private Double viewCount;
}
