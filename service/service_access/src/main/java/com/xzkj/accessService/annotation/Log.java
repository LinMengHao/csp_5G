package com.xzkj.accessService.annotation;


import com.xzkj.accessService.constants.OperatorType;

import java.lang.annotation.*;
/**
 * 日志切面
 */
@Target({ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    //操作者
    public String operatorName() default "";
    //操作类型
    public String operatorType() default OperatorType.SEND;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;
}
