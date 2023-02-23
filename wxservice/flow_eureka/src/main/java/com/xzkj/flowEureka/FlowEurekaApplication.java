package com.xzkj.flowEureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@EnableEurekaServer
public class FlowEurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowEurekaApplication.class,args);
        System.out.println("注册中心启动成功,时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
