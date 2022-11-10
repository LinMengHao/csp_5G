package com.xzkj.ossService.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xzkj.ossService.entity.TStudent;
import com.xzkj.utils.R;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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

    @Test
    public void test(){
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
}
