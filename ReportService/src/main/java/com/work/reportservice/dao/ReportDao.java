package com.work.reportservice.dao;

import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.entity.Reports;

import java.util.List;

public interface ReportDao {
//    public Reports findByReportId(Integer reportId);
//    public List<String> findReportIdBySerialNumber(String serialNumber);
    public Boolean changeReport(ChangeReport changeReport);
    public Boolean insertReport(Reports reports);
}
