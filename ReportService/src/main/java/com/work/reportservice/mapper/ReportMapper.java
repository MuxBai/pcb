package com.work.reportservice.mapper;

import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.dto.OutReport;
import com.work.reportservice.entity.Reports;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportMapper {
    public Reports findByReportId(Integer reportId);
    public List<Reports> findReportBySerialNumber(String serialNumber, int pageSize, int offset);
    public Boolean changeReport(ChangeReport changeReport);
    public Boolean insertReport(Reports reports);
    public List<Reports> findAllReport(int limit, int offset);
    public Integer countReports();
    public List<OutReport> findExportReportsByIds(List<Integer> reportIds);
    public OutReport findExportReportById(Integer id);
    public int countReportsBySerialNumber(String serialNumber);
    public int deleteByReportId(List<Integer> reportIds);
    public int deleteBySerialNumber(List<String> serialNumbers);
}
