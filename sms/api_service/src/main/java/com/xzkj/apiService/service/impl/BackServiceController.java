package com.xzkj.apiService.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.apiService.mapper.ServiceMapper;
import com.xzkj.apiService.redis.RedisUtils;
import com.xzkj.apiService.util.ConstantsReport;
import com.xzkj.apiService.util.MD5Utils;
import com.xzkj.apiService.util.MmsUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("backService")
public class BackServiceController {
    public static Logger logger = LoggerFactory.getLogger("BackServiceImpl");
    @Autowired
    private ServiceMapper serviceMapper;

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
    @RequestMapping("/mo09")
    public String mo09(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress) {
        JSONObject json=new JSONObject();
        if (StringUtils.isBlank(body)) {
            return ConstantsReport.report01Resp("9999","内容为空");
        }

        JSONObject bodyJson=null;
        try {
//            //ChnlNo=C0190&IPass=123456&IUser=test12&Mobile=18756232770&MsgId=20230210497027431001&RptTime=20230210121135165&Stat=DELIVRD
//            String decode = URLDecoder.decode(body, "utf-8");
//            String[] split = decode.split("&");
//            bodyJson=new JSONObject();
//            for (int i = 0; i < split.length; i++) {
//                String[] split1 = split[i].split("=");
//                bodyJson.put(split1[0],split1[1]);
//            }
            bodyJson=JSONObject.parseObject(body);
        }catch (Exception e){
            return ConstantsReport.report01Resp("9998","参数异常");
        }
        String appName=bodyJson.getString("IUser");
        String appInfo = RedisUtils.string_get(RedisUtils.STR_KEY_APP_INFO+appName);
        if(StringUtils.isBlank(appInfo)){
            json.put("code", "-5002");
            json.put("msg", "账号无效");
            return json.toJSONString();
        }
        JSONObject appJson = JSONObject.parseObject(appInfo);
        String companyId = appJson.getString("company_id");
        String appId = appJson.getString("app_id");
        String transId = bodyJson.containsKey("ServiceNo")?bodyJson.getString("ServiceNo"):"";


        String phone = bodyJson.containsKey("Mobile")?bodyJson.getString("Mobile"):"";
        String msg = bodyJson.containsKey("MsgCont")?bodyJson.getString("MsgCont"):"";
        String pushTime = bodyJson.containsKey("MoTime")?bodyJson.getString("MoTime"):"";
        String MsgId = bodyJson.containsKey("MsgId")?bodyJson.getString("MsgId"):"";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMMddHHmmss");
        String receiveTime="";
        try {
            Date parse = sdf2.parse(pushTime);
            receiveTime = sdf.format(parse);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        JSONObject submitJson=new JSONObject();
        submitJson.put("acc",appName);
        submitJson.put("serviceNo",transId);
        submitJson.put("mob",phone);
        submitJson.put("msg",msg);
        submitJson.put("moTime",receiveTime);

        SimpleDateFormat sdf3=new SimpleDateFormat("yyyyMM");
        String logDate = sdf3.format(new Date());
        String tableName="mms_mo_"+logDate;
        String createTime = sdf.format(new Date());
        submitJson.put("tableName",tableName);
        String moId = MmsUtils.getMmsLinkID();
        submitJson.put("moId",moId);
        //保存到上行队列
//        RedisUtils.fifo_push(RedisUtils.FIFO_APP_MO_LIST+companyId,bodyJson.toJSONString());
//        RedisUtils.hash_incrBy(RedisUtils.HASH_APP_MO_TOTAL, companyId, 1);

        String updateSql = String.format(" INSERT INTO %s"
                        + "(mo_id,mobile,service_code,content,channel_id,receive_time,app_id,company_id,isp_code,status,create_time,update_time) "
                        + "VALUES('%s','%s','%s','%s',%s,'%s',%s,%s,%s,%s,'%s','%s');",tableName,moId,phone,transId,msg,9,receiveTime,appId
                ,companyId,4,2,createTime,createTime);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
        logger.info("流水号：{}, 手机号：{}, 内容：{}, 时间：{}, 通信号：{}",transId,phone,msg,pushTime,MsgId);
        return "Y";
    }

    @RequestMapping("/mo01")
    public String mo01(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress) {
        JSONObject json=new JSONObject();
        if (StringUtils.isBlank(body)) {
            return ConstantsReport.report01Resp("9999","内容为空");
        }

        JSONObject bodyJson=null;
        try {
            bodyJson=JSONObject.parseObject(body);
        }catch (Exception e){
            return ConstantsReport.report01Resp("9998","参数异常");
        }

        String serviceCode=bodyJson.getString("serviceCode");
        String appName="";
        String companyId="";
        String appId="";
        //旧版
        String appExt = serviceCode.substring(8, 12);
        Map<String,String> map = new HashMap<String,String>();
        map.put("appExt",appExt);
        List<Map<String, String>> appInfos = serviceMapper.selectAppByCode(map);
        if ("106908324400021".equals(serviceCode)){
            appName="gzyyV";
            companyId="107";
            appId="16";

        }else {
            if(appInfos == null||appInfos.size()==0){


            }else {
                Map<String, String> appInfo = appInfos.get(0);
                companyId = String.valueOf(appInfo.get("company_id"));
                appName = String.valueOf(appInfo.get("app_name"));
                appId=String.valueOf(appInfo.get("id"));
            }
        }
        //TODO 新版本 兼容旧版本，等服务码号管理运行稳定，可弃用旧版本
        Map<String,String> csmap = new HashMap<String,String>();
        csmap.put("serviceCode",serviceCode);
        List<Map<String, String>> codeList= serviceMapper.selectCodeRelated(csmap);
        if(codeList !=null && codeList.size()>0){
            //容错，若出现服务码号关系错乱，默认取第一个，不影响系统运行
            Map<String, String> codeMap = codeList.get(0);
//            String status = String.valueOf(codeMap.get("status"));//状态：1-审核成功2-审核拒绝3-待审核
            appName=String.valueOf(codeMap.get("app_name"));
            companyId=String.valueOf(codeMap.get("company_id"));
            appId=String.valueOf(codeMap.get("app_id"));
        }

        if(StringUtils.isBlank(companyId)&&StringUtils.isBlank(appId)&&StringUtils.isBlank(appName)){
            json.put("code", "-5002");
            json.put("msg", "服务码号找不到对应关系");
            return json.toJSONString();
        }
        bodyJson.put("IUser",appName);

        String phone = bodyJson.containsKey("mobile")?bodyJson.getString("mobile"):"";
        String msg = bodyJson.containsKey("content")?bodyJson.getString("content"):"";
        Long pushTime = bodyJson.containsKey("receiveTime")?bodyJson.getLong("receiveTime"):new Date().getTime();
        String ispCode = bodyJson.containsKey("ispCode")?bodyJson.getString("ispCode"):"4";

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String receiveTime = sdf.format(new Date(pushTime));
        JSONObject submitJson=new JSONObject();
        submitJson.put("acc",appName);
        submitJson.put("serviceNo",serviceCode);
        submitJson.put("mob",phone);
        submitJson.put("msg",msg);
        submitJson.put("moTime",receiveTime);

        SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMM");
        String logDate = sdf2.format(new Date());
        String tableName="mms_mo_"+logDate;
        String createTime = sdf.format(new Date());
        submitJson.put("tableName",tableName);
        String moId = MmsUtils.getMmsLinkID();
        submitJson.put("moId",moId);


        //保存到上行队列
//        RedisUtils.fifo_push(RedisUtils.FIFO_APP_MO_LIST+companyId,submitJson.toJSONString());
//        RedisUtils.hash_incrBy(RedisUtils.HASH_APP_MO_TOTAL, companyId, 1);
        //保存到数据库
        String updateSql = String.format(" INSERT INTO %s"
                        + "(mo_id,mobile,service_code,content,channel_id,receive_time,app_id,company_id,isp_code,status,create_time,update_time) "
                        + "VALUES('%s','%s','%s','%s',%s,'%s',%s,%s,%s,%s,'%s','%s');",tableName,moId,phone,serviceCode,msg,1,receiveTime,appId
                ,companyId,ispCode,2,createTime,createTime);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        logger.info("流水号：{}, 手机号：{}, 内容：{}, 时间：{}, 通信号：{}",serviceCode,phone,msg,pushTime,moId);
        return ConstantsReport.report01Resp("0","success");
    }

    @RequestMapping("/mo02ts")
    public String mo02ts(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress) {
        JSONObject json=new JSONObject();
        if (StringUtils.isBlank(body)) {
            return ConstantsReport.report01Resp("9999","内容为空");
        }

        JSONObject bodyJson=null;
        try {
            bodyJson=JSONObject.parseObject(body);
        }catch (Exception e){
            return ConstantsReport.report01Resp("9998","参数异常");
        }

        String serviceCode=bodyJson.getString("serviceCode");
        String appName="";
        String companyId="";
        String appId="";
        //旧版
        String appExt = serviceCode.substring(8, 12);
        Map<String,String> map = new HashMap<String,String>();
        map.put("appExt",appExt);
        List<Map<String, String>> appInfos = serviceMapper.selectAppByCode(map);
        if ("106908324400021".equals(serviceCode)){
            appName="gzyyV";
            companyId="107";
            appId="16";

        }else {
            if(appInfos == null||appInfos.size()==0){

            }else {
                Map<String, String> appInfo = appInfos.get(0);
                companyId = String.valueOf(appInfo.get("company_id"));
                appName = String.valueOf(appInfo.get("app_name"));
                appId=String.valueOf(appInfo.get("id"));
            }
        }

        //TODO 新版本 兼容旧版本，等服务码号管理运行稳定，可弃用旧版本
        Map<String,String> csmap = new HashMap<String,String>();
        csmap.put("serviceCode",serviceCode);
        List<Map<String, String>> codeList= serviceMapper.selectCodeRelated(csmap);
        if(codeList !=null && codeList.size()>0){
            //容错，若出现服务码号关系错乱，默认取第一个，不影响系统运行
            Map<String, String> codeMap = codeList.get(0);
//            String status = String.valueOf(codeMap.get("status"));//状态：1-审核成功2-审核拒绝3-待审核
            appName=String.valueOf(codeMap.get("app_name"));
            companyId=String.valueOf(codeMap.get("company_id"));
            appId=String.valueOf(codeMap.get("app_id"));
        }

        if(StringUtils.isBlank(companyId)&&StringUtils.isBlank(appId)&&StringUtils.isBlank(appName)){
            json.put("code", "-5002");
            json.put("msg", "服务码号找不到对应关系");
            return json.toJSONString();
        }

        String phone = bodyJson.containsKey("mobile")?bodyJson.getString("mobile"):"";
        String msg = bodyJson.containsKey("content")?bodyJson.getString("content"):"";
        Long pushTime = bodyJson.containsKey("receiveTime")?bodyJson.getLong("receiveTime"):new Date().getTime();
        String ispCode = bodyJson.containsKey("ispCode")?bodyJson.getString("ispCode"):"4";

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String receiveTime = sdf.format(new Date(pushTime));
        JSONObject submitJson=new JSONObject();
        submitJson.put("acc",appName);
        submitJson.put("serviceNo",serviceCode);
        submitJson.put("mob",phone);
        submitJson.put("msg",msg);
        submitJson.put("moTime",receiveTime);

        SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMM");
        String logDate = sdf2.format(new Date());
        String tableName="mms_mo_"+logDate;
        String createTime = sdf.format(new Date());
        submitJson.put("tableName",tableName);
        String moId = MmsUtils.getMmsLinkID();
        submitJson.put("moId",moId);


        //保存到上行队列
//        RedisUtils.fifo_push(RedisUtils.FIFO_APP_MO_LIST+companyId,submitJson.toJSONString());
//        RedisUtils.hash_incrBy(RedisUtils.HASH_APP_MO_TOTAL, companyId, 1);
        //保存到数据库
        String updateSql = String.format(" INSERT INTO %s"
                        + "(mo_id,mobile,service_code,content,channel_id,receive_time,app_id,company_id,isp_code,status,create_time,update_time) "
                        + "VALUES('%s','%s','%s','%s',%s,'%s',%s,%s,%s,%s,'%s','%s');",tableName,moId,phone,serviceCode,msg,2,receiveTime,appId
                ,companyId,ispCode,2,createTime,createTime);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        logger.info("流水号：{}, 手机号：{}, 内容：{}, 时间：{}, 通信号：{}",serviceCode,phone,msg,pushTime,moId);
        return ConstantsReport.report01Resp("0","success");
    }
    //106816114400013
    @RequestMapping("/mo06")
    public String mo06(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress) {
        JSONObject json=new JSONObject();
        if (StringUtils.isBlank(body)) {
            return ConstantsReport.report01Resp("9999","内容为空");
        }

        JSONObject bodyJson=null;
        try {
            bodyJson=JSONObject.parseObject(body);
        }catch (Exception e){
            return ConstantsReport.report01Resp("9998","参数异常");
        }

        String serviceCode=bodyJson.getString("serviceCode");
        String appName="";
        String companyId="";
        String appId="";
        //旧版
        String appExt = serviceCode.substring(8, 12);
        Map<String,String> map = new HashMap<String,String>();
        map.put("appExt",appExt);
        List<Map<String, String>> appInfos = serviceMapper.selectAppByCode(map);
        if ("106816114400013".equals(serviceCode)){
            appName="gzyyV";
            companyId="107";
            appId="16";

        }else {
            if(appInfos == null||appInfos.size()==0){
                //农业银行=106908324400021

            }else {
                Map<String, String> appInfo = appInfos.get(0);
                companyId = String.valueOf(appInfo.get("company_id"));
                appName = String.valueOf(appInfo.get("app_name"));
                appId=String.valueOf(appInfo.get("id"));
            }
        }
        //TODO 新版本 兼容旧版本，等服务码号管理运行稳定，可弃用旧版本
        Map<String,String> csmap = new HashMap<String,String>();
        csmap.put("serviceCode",serviceCode);
        List<Map<String, String>> codeList= serviceMapper.selectCodeRelated(csmap);
        if(codeList !=null && codeList.size()>0){
            //容错，若出现服务码号关系错乱，默认取第一个，不影响系统运行
            Map<String, String> codeMap = codeList.get(0);
//            String status = String.valueOf(codeMap.get("status"));//状态：1-审核成功2-审核拒绝3-待审核
            appName=String.valueOf(codeMap.get("app_name"));
            companyId=String.valueOf(codeMap.get("company_id"));
            appId=String.valueOf(codeMap.get("app_id"));
        }

        if(StringUtils.isBlank(companyId)&&StringUtils.isBlank(appId)&&StringUtils.isBlank(appName)){
            json.put("code", "-5002");
            json.put("msg", "服务码号找不到对应关系");
            return json.toJSONString();
        }

        String phone = bodyJson.containsKey("mobile")?bodyJson.getString("mobile"):"";
        String msg = bodyJson.containsKey("content")?bodyJson.getString("content"):"";
        Long pushTime = bodyJson.containsKey("receiveTime")?bodyJson.getLong("receiveTime"):new Date().getTime();
        String ispCode = bodyJson.containsKey("ispCode")?bodyJson.getString("ispCode"):"4";

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String receiveTime = sdf.format(new Date(pushTime));
        JSONObject submitJson=new JSONObject();
        submitJson.put("acc",appName);
        submitJson.put("serviceNo",serviceCode);
        submitJson.put("mob",phone);
        submitJson.put("msg",msg);
        submitJson.put("moTime",receiveTime);

        SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMM");
        String logDate = sdf2.format(new Date());
        String tableName="mms_mo_"+logDate;
        String createTime = sdf.format(new Date());
        submitJson.put("tableName",tableName);
        String moId = MmsUtils.getMmsLinkID();
        submitJson.put("moId",moId);


        //保存到上行队列
//        RedisUtils.fifo_push(RedisUtils.FIFO_APP_MO_LIST+companyId,submitJson.toJSONString());
//        RedisUtils.hash_incrBy(RedisUtils.HASH_APP_MO_TOTAL, companyId, 1);
        //保存到数据库
        String updateSql = String.format(" INSERT INTO %s"
                        + "(mo_id,mobile,service_code,content,channel_id,receive_time,app_id,company_id,isp_code,status,create_time,update_time) "
                        + "VALUES('%s','%s','%s','%s',%s,'%s',%s,%s,%s,%s,'%s','%s');",tableName,moId,phone,serviceCode,msg,6,receiveTime,appId
                ,companyId,ispCode,2,createTime,createTime);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        logger.info("流水号：{}, 手机号：{}, 内容：{}, 时间：{}, 通信号：{}",serviceCode,phone,msg,pushTime,moId);
        return ConstantsReport.report01Resp("0","success");
    }
}
