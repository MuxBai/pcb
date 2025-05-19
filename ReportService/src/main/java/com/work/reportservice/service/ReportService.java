package com.work.reportservice.service;

import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.entity.Reports;

import java.util.List;

public interface ReportService {
    //  查询报告
    Reports findByReportId(Integer reportId);

    // 查询某一产品的所有报告
    List<String> findReportIdBySerialNumber(String serialNumber);

    // 修改报告
    Boolean changeReport(ChangeReport changeReport);

    // 生成报告
    Boolean insertReport(Reports reports);
}
