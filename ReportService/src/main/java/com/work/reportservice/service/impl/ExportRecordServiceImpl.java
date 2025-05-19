package com.work.reportservice.service.impl;

import com.work.reportservice.dao.ExportRecordDao;
import com.work.reportservice.entity.ExportRecords;
import com.work.reportservice.service.ExportRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExportRecordServiceImpl implements ExportRecordService {
    @Autowired
    private ExportRecordDao exportRecordDao;

    @Override
    public Boolean insertExportRecord(ExportRecords exportRecords) {
        return exportRecordDao.insertExportRecord(exportRecords);
    }

    @Override
    public List<ExportRecords> getExportRecordsByUserId(String userId) {
        return exportRecordDao.getExportRecordsByUserId(userId);
    }

    @Override
    public List<ExportRecords> getExportRecordsByReportId(Integer reportId) {
        return exportRecordDao.getExportRecordsByReportId(reportId);
    }

    @Override
    public ExportRecords getExportRecordByExportId(Integer exportId) {
        return exportRecordDao.getExportRecordByExportId(exportId);
    }
}
