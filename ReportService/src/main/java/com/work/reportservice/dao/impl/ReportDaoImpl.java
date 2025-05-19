package com.work.reportservice.dao.impl;

import com.work.reportservice.dao.ReportDao;
import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.entity.Report;
import com.work.reportservice.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportDaoImpl implements ReportDao {
    @Autowired
    private ReportMapper reportMapper;

    @Override
    public Report findByReportId(Integer reportId) {
        return reportMapper.findByReportId(reportId);
    }

    @Override
    public List<String> findReportIdBySerialNumber(String serialNumber) {
        return reportMapper.findReportIdBySerialNumber(serialNumber);
    }

    @Override
    public Boolean changeReport(ChangeReport changeReport) {
        return reportMapper.changeReport(changeReport);
    }

    @Override
    public Boolean insertReport(Report report) {
        return reportMapper.insertReport(report);
    }
}
