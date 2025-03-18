package com.cathay.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 紀錄 annotation 標記執行時間
 */
@Slf4j
@Aspect
@Component
public class DurationAop {

    @Around("@annotation(com.cathay.demo.model.annotation.LogExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("方法: {} 開始執行...", methodName);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        log.info("方法 {} 執行時間: {} ms.",methodName , endTime - startTime);

        return result;
    }
}
