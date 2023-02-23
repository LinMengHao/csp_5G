package com.xzkj.flowStore.config;


import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.xzkj.flowStore.common.Constant;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class PayConfig {


    @Bean
    public AlipayClient initAlipayClient() {
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", Constant.ALI_PAY_APP_ID, Constant.ALI_PAY_PRIVATE_KEY, "json", Constant.ALI_PAY_CHARSET, Constant.ALI_PAY_PUBLIC_KEY, Constant.ALI_PAY_SIGN);
        return alipayClient;
    }




}
