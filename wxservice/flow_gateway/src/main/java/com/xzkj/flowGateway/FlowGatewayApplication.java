package com.xzkj.flowGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@EnableEurekaClient
public class FlowGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowGatewayApplication.class,args);
        System.out.println("服务网关启动成功,时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
