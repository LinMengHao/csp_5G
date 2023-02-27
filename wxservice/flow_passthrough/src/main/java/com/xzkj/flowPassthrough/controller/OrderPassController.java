package com.xzkj.flowPassthrough.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LinMengHao
 * @since 2023-02-24
 */
@Slf4j
@RestController
@RequestMapping("/flowPassthrough/order-pass")
public class OrderPassController {
    @RequestMapping("test")
    public String test(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("respMsg","提交验证通过");
        String  orderNo ="O"+ new Date().getTime()+"-"+new Random().nextInt(100);
        jsonObject.put("orderNo",orderNo);
        jsonObject.put("respCode", "100000");
        return jsonObject.toJSONString();
    }

    @RequestMapping("callback")
    public String callback(@RequestBody String body){
        JSONArray jsonArray= JSON.parseArray(body);
        log.info("流量订购消息回调：{}",jsonArray);
        return "success";
    }

    @RequestMapping("query")
    public String query(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("errorMsg","正常");
        jsonObject.put("errorCode","100000");

        JSONObject data1=new JSONObject();
        data1.put("orderno", "O1677483773758-6");
        data1.put("sale_orderno", "1677483772392");
        data1.put("charge_state", "2");
        data1.put("state_msg", "充值中");
        data1.put("receive_time", "2017-01-12 13:31:35.000");

        JSONObject data2=new JSONObject();
        data2.put("orderno", "O1677483831212-30");
        data2.put("sale_orderno", "1677483831152");
        data2.put("charge_state", "1");
        data2.put("state_msg", "充值成功");
        data2.put("receive_time", "2017-01-12 13:31:35.000");

        JSONArray jsonArray =new JSONArray();

        jsonArray.add(data1);
        jsonArray.add(data2);
        jsonObject.put("listData",jsonArray);
        log.info("查询结果：{}",jsonObject);

        return jsonObject.toJSONString();
    }
}

