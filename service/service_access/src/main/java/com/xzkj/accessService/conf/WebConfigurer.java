package com.xzkj.accessService.conf;



import com.xzkj.accessService.conf.interceptor.InboundMessageInterceptor;
import com.xzkj.accessService.conf.interceptor.NotificationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {
    @Autowired
    InboundMessageInterceptor inboundMessageInterceptor;
    @Autowired
    NotificationInterceptor notificationInterceptor;
    // 注册拦截器，写好的拦截器需要通过这里添加注册才能生效
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(inboundMessageInterceptor).addPathPatterns("/xiuzhi/bj_mobile/ChatBot/MsgMO/**");
        registry.addInterceptor(notificationInterceptor).addPathPatterns("/xiuzhi/bj_mobile/Media/MsgMO/InboundMessageNotification/**");
    }
}
