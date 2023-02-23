package com.example.demo.web.controller;

import com.example.demo.web.service.HelloServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web")
public class HelloController {

    @Autowired
    private HelloServiceClient helloServiceClient;

    @RequestMapping("/hello")
    public String hello(@RequestParam String name){
        System.out.println("------------------"+name);
       return helloServiceClient.hello(name);
    }


}
