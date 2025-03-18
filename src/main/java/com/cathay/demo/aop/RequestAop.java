package com.cathay.demo.aop;

import com.cathay.demo.model.dto.BaseRs;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.exception.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 包 Rest Controller：
 * 1. 若有新增 log 表可以記錄 api 被呼叫時間
 * 2. 統一 Response 長相，前端可依據 code 判斷請求狀況
 */
@Slf4j
@Aspect
@Component
public class RequestAop {

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object handleRequestException(ProceedingJoinPoint joinPoint) {
        try {
            ResponseEntity<?> result = (ResponseEntity<?>) joinPoint.proceed();
            log.debug("result: {}", result);
            if (result.getBody() instanceof BaseRs<?>) return result;

            return ResponseEntity.ok(new BaseRs<>(result.getBody()));
        } catch (Throwable e) {
            log.error(": {}", e.getMessage(), e);

            BaseRs<?> rs;
            if (e instanceof GenericException) {
                GenericException ge = (GenericException) e;
                rs = new BaseRs<>(RequestStatus.getByCode(ge.getCode()), ge.getMessage(), null);
            } else {
                RequestStatus status = RequestStatus.SYSTEM_ERROR;
                rs = new BaseRs<>(status, status.name(), null);
            }

            return ResponseEntity.ok(rs);
        }
    }

}
