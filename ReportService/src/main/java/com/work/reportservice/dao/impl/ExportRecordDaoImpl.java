package com.work.reportservice.dao.impl;

import com.work.reportservice.dao.ExportRecordDao;
import com.work.reportservice.entity.ExportRecords;
import com.work.reportservice.mapper.ExportRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExportRecordDaoImpl implements ExportRecordDao {
    @Autowired
    private ExportRecordMapper exportRecordMapper;

    @Override
    public Boolean insertExportRecord(ExportRecords exportRecords) {
        return exportRecordMapper.insertExportRecord(exportRecords);
    }

    /*@Override
    public List<ExportRecords> getExportRecordsByUserId(String userId) {
        return exportRecordMapper.getExportRecordsByUserId(userId);
    }

    @Override
    public List<ExportRecords> getExportRecordsByReportId(Integer reportId) {
        return exportRecordMapper.getExportRecordsByReportId(reportId);
    }*/

    @Override
    public ExportRecords getExportRecordByExportId(Integer exportId) {
        return exportRecordMapper.getExportRecordByExportId(exportId);
    }
}
