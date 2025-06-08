package com.work.reportservice.service;

import com.work.reportservice.dto.ReportViewCountDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface RedisService {
    // 增加计数
    void increaseCountForUser(String userId);

    // 获取某日统计数据
    Map<Object, Object> getStatsByDate(String date);

    // 导出报告表计数
    void increaseExportCountForUser(String userId);

    // 查询某天导出统计
    Map<Object, Object> getExportStatsByDate(String date);

    // 增加报告访问量
    void incrementReportViewCount(String reportId);

    // 获取前N个访问量最高报告
    List<ReportViewCountDTO> getTopViewedReportsWithCount(String date, int topN);
}
