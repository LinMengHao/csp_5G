package com.xzkj.flowacl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FlowAclApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowAclApplication.class,args);
    }
}
