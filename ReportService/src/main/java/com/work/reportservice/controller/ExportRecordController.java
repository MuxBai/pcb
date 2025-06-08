package com.work.reportservice.controller;

import com.work.commonconfig.exception.MyException;
import com.work.reportservice.entity.ExportRecords;
import com.work.reportservice.service.ExportRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exportReport")
public class ExportRecordController {
    @Autowired
    private ExportRecordService exportRecordService;

    // 查询某一管理员获取的所有报告记录
    @GetMapping("/by-userId")
    public ResponseEntity<?> getExportRecordsByUserIdPaged(@RequestParam String userId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int pageSize,
                                                           @RequestHeader("Authorization") String token) {
        try {
            List<ExportRecords> records = exportRecordService.getExportRecordsByUserIdPaged(userId, token, page, pageSize);
            int total = exportRecordService.countExportRecordsByUserId(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("data", records);
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

    // 查询某一报告被查询的记录
    @GetMapping("/by-reportId")
    public ResponseEntity<?> getExportRecordsByReportIdPaged(@RequestParam Integer reportId,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int pageSize,
                                                             @RequestHeader("Authorization") String token) {
        try {
            List<ExportRecords> records = exportRecordService.getExportRecordsByReportIdPaged(reportId, token, page, pageSize);
            int total = exportRecordService.countExportRecordsByReportId(reportId);

            Map<String, Object> result = new HashMap<>();
            result.put("data", records);
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

    // 获取某一导出报告表的信息
    @GetMapping("/by-exportId")
    public ResponseEntity<?> getExportRecordByExportId(@RequestParam Integer exportId, @RequestHeader("Authorization") String token) {
        try {
            ExportRecords record = exportRecordService.getExportRecordByExportId(exportId, token);
            return ResponseEntity.ok(record);
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 分页查询导出记录
    @GetMapping("/page")
    public ResponseEntity<?> getExportReportsByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader("Authorization") String token) {
        try {
            List<ExportRecords> records = exportRecordService.getxportReportsByPage(page, pageSize, token);
            Integer total = exportRecordService.countExportReport();

            Map<String, Object> result = new HashMap<>();
            result.put("data", records);
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


    // 根据exportIds删除导出记录
    @DeleteMapping("/by-exportId")
    public ResponseEntity<String> deleteByExportIds(@RequestBody List<Integer> exportIds,
                                                    @RequestHeader("Authorization") String token) {
        try {
            boolean deleted = exportRecordService.deleteByExportIds(exportIds, token);
            return deleted
                    ? ResponseEntity.ok("删除成功")
                    : ResponseEntity.status(404).body("未找到对应导出记录或删除失败");
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }

    // 根据reportIds删除导出记录
    @DeleteMapping("/by-reportId")
    public ResponseEntity<String> deleteByReportIds(@RequestBody List<Integer> reportIds,
                                                    @RequestHeader("Authorization") String token) {
        try {
            boolean deleted = exportRecordService.deleteByReportIds(reportIds, token);
            return deleted
                    ? ResponseEntity.ok("删除成功")
                    : ResponseEntity.status(404).body("未找到对应导出记录或删除失败");
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }
}
