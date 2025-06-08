/*
package com.work.reportservice.aspect;

import com.work.commonconfig.config.filter.JwtFilter;
import com.work.reportservice.service.RedisService;
import com.work.reportservice.service.impl.RedisServiceImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExportReportAspect {

    @Autowired
    private RedisServiceImpl redisServiceImpl;

    @Pointcut("execution(* com.work.reportservice.service.ReportService.exportReportsAsCsv(..))")
    public void exportReportPointcut() {}

    @AfterReturning(pointcut = "exportReportPointcut()", returning = "result")
    public void afterExportReport(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length < 2) return;

        String token = (String) args[1]; // 根据方法签名，获取第二个参数token
        String userId = JwtFilter.getUserIdFromToken(token);

        if(userId != null) {
            redisServiceImpl.increaseExportCountForUser(userId);
        }
    }
}

*/
