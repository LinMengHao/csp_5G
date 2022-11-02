package com.xzkj.smsService.controller;


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
}

