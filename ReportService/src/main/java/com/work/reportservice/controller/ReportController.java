package com.work.reportservice.controller;

import com.work.commonconfig.exception.MyException;
import com.work.reportservice.dto.ChangeReport;
import com.work.reportservice.entity.Reports;
import com.work.reportservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    // 根据报告ID查询报告详情
    @GetMapping("/{reportId}")
    public ResponseEntity<?> findByReportId(@PathVariable Integer reportId,
                                            @RequestHeader("Authorization") String token) {
        try {
            Reports report = reportService.findByReportId(reportId, token);
            return ResponseEntity.ok(report);
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 查询某一产品的全部报告根据serial_number
    @GetMapping("/by-serial")
    public ResponseEntity<?> findReportIdBySerialNumberPaged(@RequestParam String serialNumber,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int pageSize,
                                                             @RequestHeader("Authorization") String token) {
        try {
            List<Reports> reports = reportService.findReportBySerialNumberPaged(serialNumber, page, pageSize, token);
            int total = reportService.countReportsBySerialNumber(serialNumber);

            Map<String, Object> result = new HashMap<>();
            result.put("data", reports);
            result.put("total", total);
            result.put("page", page);
            result.put("size", pageSize);

            return ResponseEntity.ok(result);
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 修改报告
    @PutMapping("/change")
    public ResponseEntity<?> changeReport(@RequestBody ChangeReport changeReport,
                                          @RequestHeader("Authorization") String token) {
        try {
            Boolean success = reportService.changeReport(changeReport, token);
            return ResponseEntity.ok(success);
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 生成报告
    @PostMapping("/add")
    public ResponseEntity<?> insertReport(@RequestBody Reports reports) {
        try {
            Boolean success = reportService.insertReport(reports);
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 分页获取全部报告
    @GetMapping("/page")
    public ResponseEntity<?> getReportsByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader("Authorization") String token) {
        try {
            List<Reports> reports = reportService.getReportsByPage(page, pageSize, token);
            int total = reportService.getReportCount();

            Map<String, Object> result = new HashMap<>();
            result.put("data", reports);
            result.put("total", total);
            result.put("page", page);
            result.put("size", pageSize);

            return ResponseEntity.ok(result);
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 导出报告为CSV（二进制流返回）
    @PostMapping("/export")
    public ResponseEntity<?> exportReportsAsCsv(@RequestBody List<Integer> reportIds,
                                                @RequestHeader("Authorization") String token) {
        try {
            byte[] csvData = reportService.exportReportsAsCsv(reportIds, token);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=reports.csv")
                    .header("Content-Type", "text/csv;charset=UTF-8")
                    .body(csvData);
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 根据报告id删除
    @DeleteMapping("/by-reportId")
    public ResponseEntity<String> deleteByIds(@RequestBody List<Integer> reportIds,
                                              @RequestHeader("Authorization") String token) {
        try {
            boolean deleted = reportService.deleteReportsByIds(reportIds, token);
            return deleted ? ResponseEntity.ok("删除成功")
                    : ResponseEntity.status(404).body("未找到对应报告或删除失败");
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }


    // 根据产品序列号删除
    @DeleteMapping("/by-serial")
    public ResponseEntity<String> deleteBySerialNumbers(@RequestBody List<String> serialNumbers,
                                                        @RequestHeader("Authorization") String token) {
        try {
            boolean deleted = reportService.deleteReportsBySerialNumbers(serialNumbers, token);
            return deleted ? ResponseEntity.ok("删除成功")
                    : ResponseEntity.status(404).body("未找到对应报告或删除失败");
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }
}
