/*
package com.work.reportservice.controller;

import com.work.reportservice.dto.ReportViewCountDTO;
import com.work.reportservice.service.impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    private RedisServiceImpl redisServiceImpl;

    // 统计某个用户某天检测pcb板的数量
    @GetMapping("/pcb-stat/{date}")
    public Map<Object, Object> getPcbStats(@PathVariable String date) {
        return redisServiceImpl.getStatsByDate(date);
    }

    // 统计某个用户某天导出报告的数量
    @GetMapping("/export-stat/{date}")
    public Map<Object, Object> getExportStats(@PathVariable String date) {
        return redisServiceImpl.getExportStatsByDate(date);
    }

    // 统计报告访问量最高排名
    @GetMapping("/top-reports/{date}")
    public List<ReportViewCountDTO> getTopReportsByDate(
            @PathVariable String date,
            @RequestParam(defaultValue = "10") int topN) {
        return redisServiceImpl.getTopViewedReportsWithCount(date, topN);
    }
}
*/
