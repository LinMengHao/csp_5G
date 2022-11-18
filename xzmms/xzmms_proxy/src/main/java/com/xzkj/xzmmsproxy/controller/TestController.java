package com.xzkj.xzmmsproxy.controller;

import com.xzkj.xzmmsproxy.client.TestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class TestController {

    @Autowired
    TestClient client;

    @RequestMapping("test")
    public void test(){
        log.info("access.............");
        String test = client.test6();
        log.info("数据："+test);
    }
}