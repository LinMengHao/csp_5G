package com.xzkj.flowStore.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("store")
public class TestController {
    @RequestMapping("test/{name}")
    public String test(@PathVariable("name") String name){
        return name+"远程调用store模块";
    }
}
