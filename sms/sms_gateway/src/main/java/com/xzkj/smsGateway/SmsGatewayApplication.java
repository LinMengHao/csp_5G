package com.xzkj.smsGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SmsGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsGatewayApplication.class, args);
    }

}
