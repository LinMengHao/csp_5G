package com.xzkj.apiService.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.apiService.redis.RedisUtils;
import com.xzkj.apiService.util.ConstantsReport;
import com.xzkj.apiService.util.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("backService")
public class BackServiceController {
    public static Logger logger = LoggerFactory.getLogger("BackServiceImpl");

    @RequestMapping("/mt01")
    public String mt01(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress) {
        //请求内容{"mobile":"15811047193","messageId":"20220816_1659346550002","type":0,"notifyUrl":"http://103.29.16.3:9130/back/mt01","status":"0","msg":"DELIVRD","bizCode":"2384075185690528863"}
        if (StringUtils.isBlank(body)) {
            return ConstantsReport.report01Resp("9999","内容为空");
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            return ConstantsReport.report01Resp("9998","参数异常，非json格式");
        }
        String mobile = bodyJson.containsKey("mobile")?bodyJson.getString("mobile"):"";
        String messageId = bodyJson.containsKey("messageId")?bodyJson.getString("messageId"):"";
        //下发状态, 0：短信  1：彩信
        int type = bodyJson.containsKey("type")?bodyJson.getInteger("type"):1;
        //下发状态：0-成功，其他-失败(对应msg描述)
        String status = bodyJson.containsKey("status")?bodyJson.getString("status"):"-1";
        String msg = bodyJson.containsKey("msg")?bodyJson.getString("msg"):"-1";
        //平台唯一ID
        String bizcode = bodyJson.containsKey("bizcode")?bodyJson.getString("bizcode"):"";

        String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+channelId+":"+messageId,mobile);
        if(StringUtils.isBlank(jsonStr)){
            return ConstantsReport.report01Resp("9997","重复推送或无对应数据");
        }
        JSONObject json = JSONObject.parseObject(jsonStr);
        ConstantsReport.updateMmsReport(json,channelId,messageId,mobile,status,msg,bizcode);

        return ConstantsReport.report01Resp("0","接收成功");
    }

    @RequestMapping("/mt03")
    public String mt03(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress) {
        if (StringUtils.isBlank(body)) {
            return ConstantsReport.report01Resp("9999","内容为空");
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            return ConstantsReport.report01Resp("9998","参数异常，非json格式");
        }
        String mobile = bodyJson.containsKey("phone")?bodyJson.getString("phone"):"";
        String messageId = bodyJson.containsKey("transId")?bodyJson.getString("transId"):"";

        //下发状态：0-成功，其他-失败(对应msg描述)
        String status = bodyJson.containsKey("state")?bodyJson.getString("state"):"-1";

        String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+channelId+":"+messageId,mobile);
        if(StringUtils.isBlank(jsonStr)){
            return ConstantsReport.report01Resp("9997","重复推送或无对应数据");
        }
        JSONObject json = JSONObject.parseObject(jsonStr);
        ConstantsReport.updateMmsReport(json,channelId,messageId,mobile,status,"","");

        return ConstantsReport.report01Resp("0000","成功");
    }

    @RequestMapping("/mt04")
    public String mt04(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress) {
        /*
        reports=180828100924138386,13912345678,0,DELIVRD,00000020140805135416
        ,20181125112640;180828100924138386,13912345678,0,DELIVRD,
        00000020140805135416,20181125112640
        状态报告是批量返回,直接获取 out=后面的字符串即可，多条状态报告用英文
        分号分隔
         */
        if (StringUtils.isBlank(body)) {
            return ConstantsReport.report01Resp("9999","内容为空");
        }
        try {
            body = URLDecoder.decode(body, "utf-8");
        }catch (Exception e){
            logger.info("转码错误");
        }
        JSONObject bodyJson=null;
//        try {
//            bodyJson = JSONObject.parseObject(body);
//        }catch (Exception e){
//            return ConstantsReport.report01Resp("9998","参数异常，非json格式");
//        }
        logger.info("body: {}",body);
        String reports=body.split("=")[1];
        logger.info("reports: {}",reports);
//        reports.replace("%3B",";");
//        reports.replace("%2C",",");
//        reports.replace("%3A",":");
        String[] split1 = reports.split(";");
//        String[] split1 = reports.split("%3B");
        logger.info("split: {}",split1.length);
        for (int i = 0; i < split1.length; i++) {
            String[] split2 = split1[i].split(",");
//            String[] split2 = split1[i].split("%2C");
            logger.info("split"+(i+1)+": "+split1.length);
            logger.info("split[0]:{}",split2[0]);
            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+channelId+":"+split2[0],split2[1]);
            if(StringUtils.isBlank(jsonStr)){
                return ConstantsReport.report01Resp("9997","重复推送或无对应数据");
            }
            JSONObject json = JSONObject.parseObject(jsonStr);
            ConstantsReport.updateMmsReport(json,channelId,split2[0],split2[1],split2[2],split2[3],split2[4]);
        }

        return ConstantsReport.report01Resp("0","接收成功");
    }

    @RequestMapping("/mt05")
    public String mt05(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress) {
        String key="";
        if (StringUtils.isBlank(body)) {
            return ConstantsReport.report01Resp("9999","内容为空");
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            return ConstantsReport.report01Resp("9998","参数异常，非json格式");
        }
        String siId = bodyJson.containsKey("SiID")?bodyJson.getString("SiID"):"";
        String authenticator = bodyJson.containsKey("Authenticator")?bodyJson.getString("Authenticator"):"";
        String date = bodyJson.containsKey("Date")?bodyJson.getString("Date"):"";
        String method = bodyJson.containsKey("Method")?bodyJson.getString("Method"):"";
        String authenticator1 = MD5Utils.MD5Encode(siId+date+key).toUpperCase();
        if(!authenticator1.equals(authenticator)){
            return ConstantsReport.report01Resp("9998","参数异常，鉴权失败");
        }
        List<String> reportList = bodyJson.containsKey("ReportList")?bodyJson.getObject("ReportList", List.class):new ArrayList<>();
        JSONObject states=null;
        try {
            states = JSONObject.parseObject(reportList.get(0));
        }catch (Exception e){
            return ConstantsReport.report01Resp("9998","参数异常，非json格式");
        }
        String msgID = states.containsKey("MsgID")?bodyJson.getString("MsgID"):"";
        String phone = states.containsKey("Phone")?bodyJson.getString("Phone"):"";
        String state = states.containsKey("State")?bodyJson.getString("State"):"-1";
        String messageId = states.containsKey("TransID")?bodyJson.getString("TransID"):"";


        String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+channelId+":"+messageId,phone);
        if(StringUtils.isBlank(jsonStr)){
            return ConstantsReport.report01Resp("9997","重复推送或无对应数据");
        }
        JSONObject json = JSONObject.parseObject(jsonStr);
        ConstantsReport.updateMmsReport(json,channelId,messageId,phone,state,"","");

        return ConstantsReport.report02Resp("1000","成功");
    }

    @RequestMapping("/mo03")
    public String mo03(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress) {
        if (StringUtils.isBlank(body)) {
            return ConstantsReport.report01Resp("9999","内容为空");
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            return ConstantsReport.report01Resp("9998","参数异常，非json格式");
        }
        String transId = bodyJson.containsKey("transId")?bodyJson.getString("transId"):"";


        String phone = bodyJson.containsKey("phone")?bodyJson.getString("phone"):"";
        String msg = bodyJson.containsKey("msg")?bodyJson.getString("msg"):"";
        String pushTime = bodyJson.containsKey("pushTime")?bodyJson.getString("pushTime"):"";
        String number = bodyJson.containsKey("number")?bodyJson.getString("number"):"";
        //TODO 上行逻辑...
        logger.info("流水号：{}, 手机号：{}, 内容：{}, 时间：{}, 通信号：{}",transId,phone,msg,pushTime,number);
        return ConstantsReport.report01Resp("0000","成功");
    }
}
