/*
package com.work.reportservice.aspect;

import com.work.reportservice.service.impl.RedisServiceImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReportAspect {

    @Autowired
    private RedisServiceImpl redisServiceImpl;

    // 切点
    @Pointcut("execution(* com.work.reportservice.service.ReportService.findByReportId(..))")
    public void reportViewPointcut() {}

    // 根据report_id查找报告(redis统计报告访问次数排行)
    @AfterReturning(pointcut = "reportViewPointcut()", returning = "result")
    public void afterReportView(JoinPoint joinPoint, Object result) {
        if (result == null) return; // 如果没有返回结果，不计数

        Object[] args = joinPoint.getArgs();
        if (args.length >= 1 && args[0] != null) {
            String reportId = args[0].toString();
            redisServiceImpl.incrementReportViewCount(reportId);
        }
    }
}*/
