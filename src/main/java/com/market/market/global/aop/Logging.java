package com.market.market.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect // aop 단위가 횡단 관심사임을 의미
@Component // component로 등록
@Order(10)
public class Logging {

    private static final String USER_HEADER = "X-USER-ID";

    @Around("com.market.market.global.aop.CommonPointcuts.appEntry()")
    public Object logCall(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            return pjp.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            String signature = pjp.getSignature().toShortString();
            String userId = resolveUserId();

            log.info("[CALL] {} | user={} | {}ms", signature, userId, elapsed);
        }
    }

    private String resolveUserId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String userId = request.getHeader(USER_HEADER);
            if (userId != null && !userId.isBlank()) {
                return userId;
            }
        }

        return "anonymous";
    }

}
