package com.example.demo.web.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "demo-service",contextId = "helloService")
public interface HelloServiceClient {

    @RequestMapping(value = "/service/hello", method = RequestMethod.GET)
    String hello(@RequestParam String name);

}
