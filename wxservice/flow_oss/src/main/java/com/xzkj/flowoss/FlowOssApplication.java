package com.xzkj.flowoss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FlowOssApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowOssApplication.class,args);
    }
}
