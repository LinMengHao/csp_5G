package com.xzkj.flowStore.annotation;





import com.xzkj.flowStore.constants.DataSourceConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多数据源切换注解
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DS {
    //默认使用主数据源
    String value() default DataSourceConstants.DS_KEY_MASTER;
}
