package com.xzkj.accessService.aspect;


import com.xzkj.accessService.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 日志切面
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    //日志切入点
    @Pointcut("@annotation(com.xzkj.accessService.annotation.Log)")
    public void logPointcut(){
    }

    @AfterReturning(pointcut = "@annotation(controllerLog)",returning = "jsonResult")
    public void doAfter(JoinPoint joinPoint, Log controllerLog, Object jsonResult){

    }
    protected void handleLog(JoinPoint joinPoint, Log controllerLog,Exception e,Object jsonResult){

    }

}
