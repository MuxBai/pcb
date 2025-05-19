package com.work.reportservice.service;

import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.entity.Report;

import java.util.List;

public interface ReportService {
    Report findByReportId(Integer reportId);
    List<String> findReportIdBySerialNumber(String serialNumber);
    Boolean changeReport(ChangeReport changeReport);
    Boolean insertReport(Report report);
}
