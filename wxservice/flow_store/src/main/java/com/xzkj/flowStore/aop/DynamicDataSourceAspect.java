package com.xzkj.flowStore.aop;



import com.xzkj.flowStore.annotation.DS;
import com.xzkj.flowStore.constants.DataSourceConstants;
import com.xzkj.flowStore.context.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 动态数据源切面
 */
@Aspect
@Component
public class DynamicDataSourceAspect {
    //切入点,@DS标记的类，和方法做切入
    @Pointcut("@annotation(com.xzkj.flowStore.annotation.DS)"
            + "|| @within(com.xzkj.flowStore.annotation.DS)")
    public void dataSourcePointCut(){}

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> aClass = Class.forName(signature.getDeclaringType().getName());
        // 方法优先，如果方法上存在注解，则优先使用方法上的注解
        if (signature.getMethod().isAnnotationPresent(DS.class)) {
            DynamicDataSourceContextHolder.setContextKey(signature.getMethod().getAnnotation(DS.class).value());
        }
        // 其次类优先，如果类上存在注解，则使用类上的注解
        else  if (aClass.isAnnotationPresent(DS.class)) {
            DynamicDataSourceContextHolder.setContextKey(aClass.getAnnotation(DS.class).value());
        }
        // 如果都不存在，则使用默认
        else {
            DynamicDataSourceContextHolder.setContextKey(DataSourceConstants.DS_KEY_MASTER);
        }
        try {
            return joinPoint.proceed();
        } finally {
            //删除本次数据源副本，避免内存泄漏
            DynamicDataSourceContextHolder.removeContextKey();
        }
    }
}
