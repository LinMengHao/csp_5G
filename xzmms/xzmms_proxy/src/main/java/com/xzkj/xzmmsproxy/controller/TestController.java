package com.xzkj.xzmmsproxy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xzkj.xzmmsproxy.client.TestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.util.JAXBSource;
import java.io.IOException;
import java.util.List;

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

    @RequestMapping("test1")
    public void test1(HttpServletRequest request){
        String data = request.getParameter("Data");
        JSONObject jsonObject = JSONObject.parseObject(data);
        log.info("data: {}",jsonObject);
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
        try {
            byte[] bytes = file.getBytes();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }



    }

    @RequestMapping("test7")
    public String test7(String name){
        log.info("access....");
        String test=client.test7(name);
        log.info("数据：{}",test);
        return test;
    }

    @RequestMapping("test8")
    public String test8(@RequestBody String name){
        log.info("数据：{}",name);
        return name;
    }
}
