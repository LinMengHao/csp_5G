package com.xzkj.flowPayment.controller;

import com.xzkj.flowPayment.client.IStoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pay")
public class TestController {

    @Autowired
    private IStoreClient storeClient;

    @RequestMapping("test")
    public String test(){
        String name="pay模块 ";
        String test = storeClient.test(name);
        return test;
    }
}
