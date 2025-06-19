package com.work.reportservice.mapper;

import com.work.reportservice.entity.ExportRecords;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExportRecordMapper {
    Boolean insertExportRecord(ExportRecords exportRecords);
    List<ExportRecords> getExportRecordsByUserId(String userId, int pageSize, int offset);
    int countExportRecordsByUserId(String userId);
    List<ExportRecords> getExportRecordsByReportId(Integer reportId, int pageSize, int offset);
    int countExportRecordsByReportId(Integer reportId);
    ExportRecords getExportRecordByExportId(Integer exportId);
    List<ExportRecords> findAllExportReport(int limit, int offset);
    Integer countExportReport();
    int deleteByExportId(List<Integer> exportIds);
    int deleteByReportId(List<Integer> reportIds);
    List<String> findUserIdsByExportIds(List<Integer> exportIds);
    List<Integer> findReportIdsByExportIds(List<Integer> exportIds);
    List<String> findUserIdsByReportIds(List<Integer> reportIds);
    List<Integer> findExportIdsByReportIds(List<Integer> reportIds);

}
