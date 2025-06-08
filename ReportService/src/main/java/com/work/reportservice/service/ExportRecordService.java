package com.work.reportservice.service;

import com.work.reportservice.entity.ExportRecords;

import java.util.List;

public interface ExportRecordService {

    // 查询某一管理员获取的所有报告记录
    List<ExportRecords> getExportRecordsByUserIdPaged(String userId, String token, int page, int pageSize);
    int countExportRecordsByUserId(String userId);

    // 查询某一报告被查询的记录
    List<ExportRecords> getExportRecordsByReportIdPaged(Integer reportId, String token, int page, int pageSize);
    int countExportRecordsByReportId(Integer reportId);

    // 获取某一报告查询记录
    ExportRecords getExportRecordByExportId(Integer exportId, String token);

    // 分页获取报告查询记录
    List<ExportRecords> getxportReportsByPage(int page, int pageSize, String token);

    // 获取全部记录数
    Integer countExportReport();

    // 根据export_id删除导出记录表
    boolean deleteByExportIds(List<Integer> exportIds, String token);

    // 根据report_id删除导出记录表
    boolean deleteByReportIds(List<Integer> reportIds, String token);
}
