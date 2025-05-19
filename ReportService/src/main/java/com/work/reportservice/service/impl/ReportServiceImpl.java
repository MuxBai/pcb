package com.work.reportservice.service.impl;

import com.work.reportservice.dao.ReportDao;
import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.entity.Reports;
import com.work.reportservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDao reportDao;

    @Override
    public Reports findByReportId(Integer reportId) {
        return reportDao.findByReportId(reportId);
    }

    @Override
    public List<String> findReportIdBySerialNumber(String serialNumber) {
        return reportDao.findReportIdBySerialNumber(serialNumber);
    }

    @Override
    public Boolean changeReport(ChangeReport changeReport) {
        return reportDao.changeReport(changeReport);
    }

    @Override
    public Boolean insertReport(Reports reports) {
        return reportDao.insertReport(reports);
    }
}
