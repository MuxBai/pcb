package com.work.reportservice.dao;

import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.entity.Report;

import java.util.List;

public interface ReportDao {
    public Report findByReportId(Integer reportId);
    public List<String> findReportIdBySerialNumber(String serialNumber);
    public Boolean changeReport(ChangeReport changeReport);
    public Boolean insertReport(Report report);
}
