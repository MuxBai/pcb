package com.work.reportservice.dao;

import com.work.reportservice.entity.ExportRecords;

import java.util.List;

public interface ExportRecordDao {
    Boolean insertExportRecord(ExportRecords exportRecords);
//    List<ExportRecords> getExportRecordsByUserId(String userId);
//    List<ExportRecords> getExportRecordsByReportId(Integer reportId);
    ExportRecords getExportRecordByExportId(Integer exportId);
}
