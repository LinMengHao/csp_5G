package com.xzkj.smsService.controller;


import com.alibaba.fastjson.JSONObject;
import com.xzkj.smsService.entity.TUser;
import com.xzkj.smsService.entity.User;
import com.xzkj.smsService.service.TUserService;
import com.xzkj.utils.MD5Utils;
import com.xzkj.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LinMengHao
 * @since 2022-10-13
 */

@Slf4j
@RefreshScope
@RestController
@RequestMapping("/baseservice/t-user")
public class TUserController {
    @Autowired
    TUserService service;
    @Value("${lmh.name}")
    private String name;
    @Value("${lmh.age}")
    private Integer age;

    @Value("${lmh.phone}")
    private List<Integer> phone=new ArrayList<>();


    @PostMapping(value = "findAll",consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE})
    public R test(){
        List<TUser> list= service.findAll();
        log.info(list.toString());
        log.info("name: "+name);
        log.info("age: "+age);
        log.info("phone"+phone.toString());
        return R.ok();
    }
    @Test
    public void test1(){
        long time = new Date().getTime();
        System.out.println("ts:"+time);
        String s = MD5Utils.MD5Encode("acc=xiuzhi&ts="+time+"|||pwd=xiuzhi66");
        System.out.println("token:"+s);
    }

    @Test
    public void test2(){
        JSONObject jsonObject=new JSONObject();
        JSONObject params=new JSONObject();
        String[] split = "hhh,lll,xxx".split(",");
        for (int i = 1; i <= split.length; i++) {
            String key="v"+i;
            params.put(key,split[i-1]);
        }
        jsonObject.put("vars",params.toString());
        System.out.println(params.toJSONString());
        System.out.println(jsonObject.toJSONString());
    }
    @Test
    public void test3(){
        String spid="1195";
        String login="admin";
        String pwd= MD5Utils.MD5Encode(login+"!yJIE7TU6").toUpperCase();
        JSONObject submitJson = new JSONObject();
        submitJson.put("phones","18756232770");
        submitJson.put("sn","20221116111300001");
        submitJson.put("spid",spid);
        submitJson.put("login",login);
        submitJson.put("pwd",pwd);
        submitJson.put("tid","221116102159200002");
        System.out.println(submitJson.toJSONString());
    }
    @Test
    public void test4() throws UnsupportedEncodingException {
        String decode = URLDecoder.decode("reports=221117141912105794%2C18908002166%2C4%2CDB%3A1037%2C20221117_1419127140019%2C20221117141912%3B","utf-8");
        System.out.println("reports=221117141912105794%2C18908002166%2C4%2CDB%3A1037%2C20221117_1419127140019%2C20221117141912%3B");
    }
}

