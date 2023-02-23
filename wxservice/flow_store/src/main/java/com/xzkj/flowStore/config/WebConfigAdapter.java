package com.xzkj.flowStore.config;

import com.google.common.collect.Lists;

import com.xzkj.flowStore.interceptor.AdminInterceptor;
import com.xzkj.flowStore.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfigAdapter extends WebMvcConfigurationSupport {

    @Bean
    TokenInterceptor initTokenInterceptor() {
        return new TokenInterceptor();
    }

    @Bean
    AdminInterceptor initAdminInterceptor() {
        return new AdminInterceptor();
    }


    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(initTokenInterceptor())
                .addPathPatterns("/web/**")
                .excludePathPatterns(Lists.newArrayList("/admin/**"));

        registry.addInterceptor(initAdminInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns(Lists.newArrayList("/web/**"));
    }

    /**
     * 防止@EnableMvc把默认的静态资源路径覆盖了，手动设置的方式
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/").setCachePeriod(0);
    }
}
