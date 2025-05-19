package com.work.reportservice.mapper;

import com.work.reportservice.entity.ExportRecords;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExportRecordMapper {
    public Boolean insertExportRecord(ExportRecords exportRecords);
    public List<ExportRecords> getExportRecordsByUserId(String userId);
    public List<ExportRecords> getExportRecordsByReportId(Integer reportId);
    public ExportRecords getExportRecordByExportId(Integer exportId);
}
