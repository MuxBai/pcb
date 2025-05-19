package com.work.reportservice.service;

import com.work.reportservice.entity.ExportRecords;

import java.util.List;

public interface ExportRecordService {
    // 生成查询报告记录
    Boolean insertExportRecord(ExportRecords exportRecords);

    // 查询某一管理员获取的所有报告记录
    List<ExportRecords> getExportRecordsByUserId(String userId);

    // 查询某一报告被查询的记录
    List<ExportRecords> getExportRecordsByReportId(Integer reportId);

    // 获取某一报告查询记录
    ExportRecords getExportRecordByExportId(Integer exportId);
}
