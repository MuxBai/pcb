package com.work.reportservice.controller;

import com.work.reportservice.entity.ExportRecords;
import com.work.reportservice.service.ExportRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ExportRecordController {
    @Autowired
    private ExportRecordService exportRecordService;

    @PostMapping("/insert")
    public Boolean insertExportRecord(ExportRecords exportRecords)
    {
        return exportRecordService.insertExportRecord(exportRecords);
    }

    @GetMapping("/getExportRecordsByUserId/{userId}")
    public List<ExportRecords> getExportRecordsByUserId(@PathVariable String userId)
    {
        return exportRecordService.getExportRecordsByUserId(userId);
    }

    @GetMapping("/getExportRecordsByReportId/{reportId}")
    public List<ExportRecords> getExportRecordsByReportId(@PathVariable Integer reportId){
        return exportRecordService.getExportRecordsByReportId(reportId);
    }

    @GetMapping("/getExportRecordByExportId/{exportId}")
    public ExportRecords getExportRecordByExportId(@PathVariable Integer exportId){
        return exportRecordService.getExportRecordByExportId(exportId);
    }
}
