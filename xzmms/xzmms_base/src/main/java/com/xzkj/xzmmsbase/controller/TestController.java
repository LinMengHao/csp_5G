package com.xzkj.xzmmsbase.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.Duration;
import com.google.protobuf.InvalidProtocolBufferException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.xzkj.base.handler.LmhException;
import com.xzkj.utils.MD5Utils;
import com.xzkj.utils.R;
import com.xzkj.xzmmsbase.entity.TStudent;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

@RefreshScope
@RestController
public class TestController {



    @PostMapping("/test1")
    public R test1(HttpServletRequest request){

        try {
            TStudent.Student student = TStudent.Student.parseFrom((InputStream) request.getInputStream());
            System.out.println("字符解析后："+student.getName());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.ok();
    }

    @PostMapping("/test2")
    public R test2(){
        RestTemplate restTemplate=new RestTemplate();
        TStudent.Student.Builder builder = TStudent.Student.newBuilder();
        builder.setHobby("打篮球");
        builder.setName("阿三");
        builder.setSex(1);
        builder.setSkill("java");
        builder.setNumber(123);
        TStudent.Student student = builder.build();
        System.out.println("数据大小："+student.toByteString().size());
        System.out.println("数据字节字符串内容："+student.toByteString().toString());
        System.out.println("数据实体内容："+student.toString());
        System.out.println(student.getName());
        System.out.println(student.getHobby());

        byte[] bytes = student.toByteArray();
        System.out.println("压缩后："+ Arrays.toString(bytes));
        HttpHeaders headers=new HttpHeaders();
        HttpEntity<byte[]> entity=new HttpEntity<>(bytes,headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8085/test1", entity, String.class);
        System.out.println("客户端接受响应："+response.getBody().toString());
        return R.ok();
    }
    @PostMapping("/test3")
    public R test3(MultipartFile data) throws IOException {
        byte[] bytes = data.getBytes();
        TStudent.Student student = TStudent.Student.parseFrom(bytes);
        System.out.println("来自python的包体，解析："+student.toString());
        System.out.println(student.getName());
        System.out.println(student.getHobby());
        return R.ok();
    }

    @Autowired
    RabbitTemplate rabbitTemplate;
    @PostMapping("/test4")
    public R test4(){
        TStudent.Student.Builder builder = TStudent.Student.newBuilder();
        builder.setNumber(18);
        builder.setDuration(Duration.newBuilder().build());
        builder.setName("阿三");
        builder.setSex(1);
        builder.setHobby("打篮球");
        builder.setSkill("java");
        TStudent.Student student = builder.build();
        rabbitTemplate.convertAndSend("student","student",student.toByteArray());
        return R.ok();
    }

    @Test
    public void test5(){
        TStudent.Student.Builder builder = TStudent.Student.newBuilder();
        builder.setHobby("打篮球");
        builder.setName("小林");
        builder.setSex(1);
        builder.setSkill("java");
        builder.setNumber(123);
        TStudent.Student student = builder.build();
        System.out.println("数据大小："+student.toByteString().size());
        System.out.println("数据字节字符串内容："+student.toByteString().toString());
        System.out.println("数据实体内容："+student.toString());
        System.out.println(student.getName());
        System.out.println(student.getHobby());

        byte[] bytes = student.toByteArray();
        System.out.println("压缩后："+ Arrays.toString(bytes));
        try {
            TStudent.Student student1 = TStudent.Student.parseFrom(bytes);
            System.out.println("字节数组解析后："+student.getName());

            TStudent.Student student2 = TStudent.Student.parseFrom(student.toByteString());
            System.out.println("字符解析后："+student.getName());

        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    @Value("${xz.name}")
    public String name;

    @Value("${xz.age}")
    public Integer age;

    @RequestMapping("test6")
    public String test6(){
        System.out.println("9001执行啦");
        return name+" "+age;
    }

    @RequestMapping("test7")
    @HystrixCommand(fallbackMethod = "getNameFallback")
    public String test7(String name){
        System.out.println("参数："+name);
        if("lmh".equals(name)){
            return "OK";
        }else {
            throw new LmhException("500","熔断一手");
        }
    }
    /**
     * 出错后会调用该降级方法，返回指定的信息
     * @param name
     * @return
     */
    public String getNameFallback(String name){
        return " this username is not exist ";
    }



}
