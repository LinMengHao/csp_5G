package com.xzkj.flowacl.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {


    // 注册拦截器，写好的拦截器需要通过这里添加注册才能生效
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(orderFlowInterceptor).addPathPatterns("/action/flow/flowRecharge");
//        registry.addInterceptor(orderFlowCallbackInterceptor).addPathPatterns("/action/flow/callback");
//        registry.addInterceptor(orderFlowQueryInterceptor).addPathPatterns("/action/flow/flowQuery");
//        registry.addInterceptor(productFlowQueryInterceptor).addPathPatterns("/action/flow/productQuery");
    }
}
