//package com.market.market.global.aop;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Aspect // aop 단위가 횡단 관심사임을 의미
//@Component // component로 등록
//@Order(10)
//public class Logging {
//
//    public Object logCall(ProceedingJoinPoint pjp) throws Throwable{
//
//        String sig = pjp.getSignature().toShortString();
//        String user = getUser();
//
//        return null;
//    }
//
//    private String getUser() {
//        Authentication a = SecurityContextHolder.getContext().getAuthentication();
//    }
//
//}
