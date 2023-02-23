package com.xzkj.apiController.controller;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.apiController.redis.RedisUtils;
import com.xzkj.apiController.util.MD5Utils;
import com.xzkj.apiController.util.MmsUtils;
import com.xzkj.apiController.util.PublicUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping(value="mms")
public class DefineController {
    public static Logger logger = LoggerFactory.getLogger("DefineController");

    @RequestMapping("send")
    @ResponseBody
    public String mmsSend(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);

        String result = sendData(body);

        long duration = System.currentTimeMillis()-begin;
        logger.info("send,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }

    public String sendData(String body){
        JSONObject json = new JSONObject();
        json.put("bn", "");//{“code”:”T”,”msg”:”成功”,”bn”:” 202109011541207100001”}
        //请求内容
        if (StringUtils.isBlank(body)) {
            json.put("code", "-5005");
            json.put("msg", "body内容为空");
            return json.toJSONString();
        }
        try {
            body= URLDecoder.decode(body,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            json.put("code", "-5005");
            json.put("msg", "body内容异常");
            return json.toJSONString();
        }
        //用户账号
        String appName = bodyJson.containsKey("acc")?bodyJson.getString("acc"):"";
        //接入号，可以为空
        String src = bodyJson.containsKey("src")?bodyJson.getString("src"):"";
        //批次，可以为空
        String bn = bodyJson.containsKey("bn")?bodyJson.getString("bn"):"";
        //视频短信模板ID
        String mmsId = bodyJson.containsKey("mid")?bodyJson.getString("mid"):"";
        //手机号码,多个以”,”隔开，单次提交号码不能超过200个
        String phones = bodyJson.containsKey("mobs")?bodyJson.getString("mobs"):"";
        //变量参数组(组内成员变量以半角逗号分隔，组与组之间以|分隔，格式（变量1=a1,变量2=b1,变量3=c1|变量1=a2,变量2=b2,变量3=c2|变量1=a3,变量2=b3,变量3=c3) （urlencode编码 utf-8）
        String params = "";
        //mid=111&变量1=a1,变量2=b1,变量3=c1|变量1=a2,变量2=b2,变量3=c2,变量1=a3,变量2=b3,变量3=c3
        String content = bodyJson.containsKey("params")?bodyJson.getString("params"):"";

        String timestamp = bodyJson.containsKey("ts")?bodyJson.getString("ts"):"";
        String data = bodyJson.containsKey("data")?bodyJson.getString("data"):"";
        String url = bodyJson.containsKey("url")?bodyJson.getString("url"):"";
        String token = bodyJson.containsKey("token")?bodyJson.getString("token"):"";

        String[] contentArr = content.split("&");
        if(contentArr.length>1){
            mmsId = contentArr[0].split("=")[1];
            params = contentArr[1];
        }else{
            if (content.indexOf("=")!=-1){
                mmsId = content.split("=")[1];
            }else{
                params = content;
            }
        }
        bodyJson.put("mid",mmsId);
        bodyJson.put("params",params);
        //用户app_name由视频短信服务方提供
        if (StringUtils.isBlank(appName)) {
            json.put("code", "-5002");
            json.put("msg", "acc内容为空");
            return json.toJSONString();
        }
        //时间戳 访问接口时间  单位：毫秒,允许误差1分钟
        if (StringUtils.isBlank(timestamp)) {
            json.put("code", "-5003");
            json.put("msg", "timestamp内容为空");
            return json.toJSONString();
        }
        long time = 0;
        try{
            time = Long.valueOf(timestamp);
        }catch(Exception e){
            json.put("code", "-5003");
            json.put("msg", "timestamp无效");
            return json.toJSONString();
        }
        long dur = System.currentTimeMillis()-time;
        if(dur>600000){
            json.put("code", "-5003");
            json.put("msg", "timestamp过期时间超过10分钟");
            return json.toJSONString();
        }
        //数字签名 参考 token MD5校验和，token=md5(acc=xxx&ts=xxx|||pwd=xxxxxx)
        if (StringUtils.isBlank(token)) {
            json.put("code", "-5004");
            json.put("msg", "sign内容为空");
            return json.toJSONString();
        }

        if (StringUtils.isBlank(mmsId)){
            json.put("code", "-5006");
            json.put("msg", "模板ID为空");
            return json.toJSONString();
        }
        if (StringUtils.isBlank(phones)){
            json.put("code", "-5007");
            json.put("msg", "手机号为空");
            return json.toJSONString();
        }
        int sendType=1;//1-普通视频短信发送，2-变量视频短信发送
        String[] paramsArr = null;
        if(StringUtils.isNotEmpty(params) && !params.equals("null") && !params.equals("NULL")){
            paramsArr=params.split("\\|");
            if(paramsArr.length==0){
                sendType=1;
            }else{
                sendType=2;
            }
        }
        String[] phoneArr = phones.split(",");
        if(sendType==2){//变量模板短信
            if(paramsArr.length!= phoneArr.length){
                json.put("code", "-5009");
                json.put("msg", "手机号码和参数长度不一致");
                return json.toJSONString();
            }
        }
        String appInfo = RedisUtils.string_get(RedisUtils.STR_KEY_APP_INFO+appName);
        if(StringUtils.isBlank(appInfo)){
            json.put("code", "-5002");
            json.put("msg", "账号无效");
            return json.toJSONString();
        }
        JSONObject appJson = JSONObject.parseObject(appInfo);
        String password = appJson.getString("password");
        String companyId = appJson.getString("company_id");
        String appId = appJson.getString("app_id");
        String backMt = appJson.getString("rpt_sync_address");

        //acc=xxx&ts=xxx|||pwd=xxxxxx
        StringBuilder sb = new StringBuilder("acc=");
        sb.append(appName).append("&ts=").append(timestamp).append("|||pwd=").append(password);

        String tokenSign = MD5Utils.MD5Encode(sb.toString());
        if(!tokenSign.toLowerCase().equals(token.toLowerCase())){
            json.put("code", "-5004");
            json.put("msg", "数字签名无效");
            return json.toJSONString();
        }

        String start_time = appJson.getString("start_time");
        String end_time = appJson.getString("end_time");

        String now = new SimpleDateFormat("HHmm").format(new Date());
        if (now.compareTo(start_time) < 0 || now.compareTo(end_time) > 0) {
            json.put("code", "-5011");
            json.put("msg", "提交不在服务时间");
            return json.toJSONString();
        }

        String payment = appJson.getString("payment");
        long limitCount = appJson.getLong("limit_count");
        if("advance".equals(payment)) {
            //客户消费数
            String consume = RedisUtils.hash_get(RedisUtils.HASH_ACC_SEND, appName);
            long total = limitCount-Integer.parseInt(StringUtils.isNotBlank(consume)?consume:"0");
            /*if (StringUtils.isEmpty(balance)) {
                json.put("code", "-5010");
                json.put("msg", "余额不足");
                return json.toJSONString();
            }
            long chargeTotal = Long.valueOf(balance);*/
            if (total < phoneArr.length) {
                json.put("code", "-5010");
                json.put("msg", "余额不足");
                return json.toJSONString();
            }
        }
        /*String auth_ip = appJson.getString("auth_ip");
        //ipAddress = "0.0.0.0";//不限制ip
        if(auth_ip.indexOf(ipAddress)==-1&&auth_ip.indexOf("0.0.0.0")==-1){
            json.put("code", "-5025");
            json.put("msg", "IP地址受限");
            return json.toJSONString();
        }*/
        if(StringUtils.isBlank(bn)){
            bn= MmsUtils.getMmsLinkID();
        }

        bodyJson.put("sendType",sendType);//1-普通视频短信发送，2-变量视频短信发送
        bodyJson.put("appId",appId);//用户id
        bodyJson.put("appName",appName);//用户账号
        bodyJson.put("password",password);//用户密码
        bodyJson.put("companyId",companyId);//公司id
        bodyJson.put("batchId",bn);//
        bodyJson.put("timestamp",timestamp);//提交的时间戳
        if(StringUtils.isBlank(url)&&StringUtils.isNotBlank(backMt)){
            bodyJson.put("url",backMt);//客户回调地址
        }

        RedisUtils.fifo_push(RedisUtils.FIFO_APP_REQ_LIST+companyId+":"+appId,bodyJson.toJSONString());
        RedisUtils.hash_incrBy(RedisUtils.HASH_APP_REQ_TOTAL, companyId+"_"+appId, 1);

        //写入发件箱
        String submitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String updateSql = String.format(" INSERT INTO t_outbox(msg_id,app_id,send_num,subject,date_time,status,company_id) "
                + "VALUES('%s',%s,%s,'%s','%s','%s',%s);",bn,appId,phoneArr.length,mmsId, submitTime,"complete",companyId);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        json.put("code", "000");
        json.put("msg", "成功");
        json.put("bn", bn);
        return json.toJSONString();
    }
}
