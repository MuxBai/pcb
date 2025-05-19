package com.work.reportservice.mapper;

import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.entity.Report;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportMapper {
    public Report findByReportId(Integer reportId);
    public List<String> findReportIdBySerialNumber(String serialNumber);
    public Boolean changeReport(ChangeReport changeReport);
    public Boolean insertReport(Report report);
}
