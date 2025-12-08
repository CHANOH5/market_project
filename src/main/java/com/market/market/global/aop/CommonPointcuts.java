package com.market.market.global.aop;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CommonPointcuts {

    // 서비스 & 컨트롤러의 public 메서드만 로그
    @Pointcut("execution(public * com.market..service..*(..)) || execution(public * com.market..controller..*(..))")
    public void appEntry(){}

    // 민감정보 로그 제외
//    @Pointcut("@annotation(com.market.market.global.aop.NoLog)")
//    public void noLog(){}

} // end class
