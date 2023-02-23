package com.xzkj.flowStore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class FlowStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowStoreApplication.class,args);
        System.out.println("商城模块启动成功,时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
