package com.work.reportservice.service;

import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.entity.Reports;

import java.util.List;

public interface ReportService {

    // 查询报告
    Reports findByReportId(Integer reportId, String token);

    // 查询某一产品的所有报告
    List<Reports> findReportBySerialNumberPaged(String serialNumber, int page, int pageSize, String token);
    int countReportsBySerialNumber(String serialNumber);

    // 修改报告
    Boolean changeReport(ChangeReport changeReport, String token);

    // 生成报告
    Boolean insertReport(Reports reports);

    // 分页获取报告
    List<Reports> getReportsByPage(int page, int pageSize, String token);

    //  获取报告总数
    Integer getReportCount();

    // 导出报告（转为csv类型）
    byte[] exportReportsAsCsv(List<Integer> reportIds, String token);

    // 根据report_id删除报告
    boolean deleteReportsByIds(List<Integer> reportIds, String token);

    // 根据serial_number删除报告
    boolean deleteReportsBySerialNumbers(List<String> serialNumbers, String token);
}
