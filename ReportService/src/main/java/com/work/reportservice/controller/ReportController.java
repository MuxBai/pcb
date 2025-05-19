package com.work.reportservice.controller;

import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.entity.Reports;
import com.work.reportservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/findByReportId/{reportId}")
    public Reports findByReportId(@PathVariable Integer reportId){
        return reportService.findByReportId(reportId);
    }

    @GetMapping("/findReportIdBySerialNumber/{serialNumber}")
    public List<String> findReportIdBySerialNumber(@PathVariable String serialNumber){
        return reportService.findReportIdBySerialNumber(serialNumber);
    }

    @PostMapping("/changeReport")
    public Boolean changeReport(@RequestBody ChangeReport changeReport){
        return reportService.changeReport(changeReport);
    }

    @PostMapping("/insertReport")
    public Boolean insertReport(@RequestBody Reports reports){
        return reportService.insertReport(reports);
    }
}
