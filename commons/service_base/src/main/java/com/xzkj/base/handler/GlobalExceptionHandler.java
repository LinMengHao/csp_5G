package com.xzkj.base.handler;


import com.xzkj.utils.ExceptionUtils;
import com.xzkj.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理类
 *
 * @Slf4j作用：
 * log.error(e.getMessage ());
 *         e.printStackTrace();
 *         return R.error().message("执行全局异常处理...");
 *         则可以把运行中的异常写入日志文件
 */
@Slf4j
@ControllerAdvice//切面增强Controller
public class GlobalExceptionHandler {
    //指定出现什么异常执行这个方法
    @ExceptionHandler(Exception.class)
    @ResponseBody//为了返回数据
    public R error(Exception e){
        log.error(ExceptionUtils.getMessage(e));
        e.printStackTrace();
        return R.error().message("执行全局异常处理...");
    }

    //特定异常
    @ExceptionHandler(MyBatisSystemException.class)
    @ResponseBody//为了返回数据
    public R error(MyBatisSystemException e){
        log.error(ExceptionUtils.getMessage(e));
        e.printStackTrace();
        return R.error().message("执行MyBatisSystemException异常处理...");
    }

    //自定义异常处理
    @ExceptionHandler(LmhException.class)
    @ResponseBody//为了返回数据
    public R error(LmhException e){
        log.error(ExceptionUtils.getMessage(e));
        e.printStackTrace();
        return R.error().code(e.getCode()).message(e.getMsg());
    }
}
