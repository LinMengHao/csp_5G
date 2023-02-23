package com.xzkj.apiService.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.apiService.mapper.ServiceMapper;
import com.xzkj.apiService.redis.RedisUtils;
import com.xzkj.apiService.util.MD5Utils;
import com.xzkj.apiService.util.MmsUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("apiService")
public class ApiServiceController{
    //public static Logger logger = LoggerFactory.getLogger("ApiServiceImpl");

    @Autowired
    private ServiceMapper serviceMapper;

    @Value("${model.filePath}")
    private String modelFilePath;
    @Value("${model.filePaths}")
    private String modelFilePaths;

    @Value("${sign.filePath}")
    private String signFilePath;
    @Value("${sign.profile}")
    private String profile;
    @Value("${sign.filePaths}")
    private String signFilePaths;

    @RequestMapping("/mmsSend")
    public String mmsSend(@RequestParam String body,@RequestParam String ipAddress) {
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
        String params = bodyJson.containsKey("params")?bodyJson.getString("params"):"";
        String timestamp = bodyJson.containsKey("ts")?bodyJson.getString("ts"):"";
        String data = bodyJson.containsKey("data")?bodyJson.getString("data"):"";
        String url = bodyJson.containsKey("url")?bodyJson.getString("url"):"";
        String token = bodyJson.containsKey("token")?bodyJson.getString("token"):"";

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
        //是否变量模板
        boolean isVariable = true;
        String[] paramsArr = null;
        if(StringUtils.isEmpty(params) || params.equals("null")){
            isVariable=false;
        }else{
            paramsArr=params.split("\\|");
            if(paramsArr.length==0){
                isVariable=false;
            }
        }
        String[] phoneArr = phones.split(",");
        if(isVariable){//变量模板短信
            sendType=2;
            if(phoneArr.length>200){
                json.put("code", "-5009");
                json.put("msg", "手机号码长度超过限制");
                return json.toJSONString();
            }
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
        String group_id = appJson.getString("group_id");//黑名单组id
        JSONObject groupJson = appJson.getJSONObject("groupJson");//黑名单组
        JSONArray blackGroup = appJson.getJSONArray("blackGroup");//黑名单组关系

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
        if(isVariable) {//变量模板短信
        }else{
        }
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
        bodyJson.put("groupId",group_id);//黑名单组ID
        bodyJson.put("groupJson",groupJson);//黑名单组
        bodyJson.put("blackGroup",blackGroup);//黑名单组关系
        if(StringUtils.isBlank(url)&&StringUtils.isNotBlank(backMt)){
            bodyJson.put("url",backMt);//客户回调地址
        }

        RedisUtils.fifo_push(RedisUtils.FIFO_APP_REQ_LIST+companyId+":"+appId,bodyJson.toJSONString());
        RedisUtils.hash_incrBy(RedisUtils.HASH_APP_REQ_TOTAL, companyId+"_"+appId, 1);

        /*//写入发件箱
        String submitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String updateSql = String.format(" INSERT INTO t_outbox(msg_id,app_id,send_num,subject,date_time,status,company_id) "
                        + "VALUES('%s',%s,%s,'%s','%s','%s',%s);",bn,appId,phoneArr.length,mmsId, submitTime,"complete",companyId);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);*/

        json.put("code", "000");
        json.put("msg", "成功");
        json.put("bn", bn);
        return json.toJSONString();
    }

    @RequestMapping("/mmsList")
    public String mmsList(@RequestParam String body,@RequestParam String ipAddress) {
        JSONObject json = new JSONObject();
        json.put("data", "");//{“code”:”000”,”msg”:”成功”,”data”:””}
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
        String timestamp = bodyJson.containsKey("ts")?bodyJson.getString("ts"):"";
        //手机号码
        String mobile = bodyJson.containsKey("mob")?bodyJson.getString("mob"):"";
        //批次，可以为空
        String bn = bodyJson.containsKey("bn")?bodyJson.getString("bn"):"";
        String date = bodyJson.containsKey("date")?bodyJson.getString("date"):"";

        String token = bodyJson.containsKey("token")?bodyJson.getString("token"):"";

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
        if (StringUtils.isBlank(mobile)){
            json.put("code", "-5007");
            json.put("msg", "手机号为空");
            return json.toJSONString();
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

        //acc=xxx&ts=xxx|||pwd=xxxxxx
        StringBuilder sb = new StringBuilder("acc=");
        sb.append(appName).append("&ts=").append(timestamp).append("|||pwd=").append(password);

        String tokenSign = MD5Utils.MD5Encode(sb.toString());
        if(!tokenSign.toLowerCase().equals(token.toLowerCase())){
            json.put("code", "-5004");
            json.put("msg", "数字签名无效");
            return json.toJSONString();
        }

        /*String auth_ip = appJson.getString("auth_ip");
        //ipAddress = "0.0.0.0";//不限制ip
        if(auth_ip.indexOf(ipAddress)==-1&&auth_ip.indexOf("0.0.0.0")==-1){
            json.put("code", "-5025");
            json.put("msg", "IP地址受限");
            return json.toJSONString();
        }*/
        if(StringUtils.isBlank(date)){
            date=new SimpleDateFormat("yyyyMMdd").format(new Date());
        }
        String tableName = "mms_sender_"+date;

        Map<String,String> map = new HashMap<String,String>();
        map.put("companyId",companyId);
        map.put("appId",appId);
        map.put("batchId",bn);
        map.put("destNumber",mobile);
        map.put("tableName",tableName);

        List<Map<String,String>> mapList = serviceMapper.selectOrderList(map);
        if(mapList == null||mapList.size()==0){
            json.put("code", "-5013");
            json.put("msg", "无此数据");
            return json.toJSONString();
        }
        Map<String,String> mapData = mapList.get(0);
        String batchId = mapData.get("batch_id");
        String status = mapData.get("status");
        String info = mapData.get("info");
        String reportTime = mapData.get("report_time");

        JSONObject dataJson = new JSONObject();
        dataJson.put("acc", appName);
        dataJson.put("bn", bn);
        dataJson.put("mob", mobile);

        dataJson.put("mid", batchId);
        dataJson.put("src", "");
        dataJson.put("code", status);//DELIVRD为成功，其他请参考错误码说明。
        dataJson.put("msg", info);
        dataJson.put("time", reportTime);
        dataJson.put("down", "");//下载时间
        dataJson.put("data", "");//用户保留数据

        json.put("code", "000");
        json.put("msg", "成功");
        json.put("data", dataJson.toJSONString());
        return json.toJSONString();
    }

    @RequestMapping("/modelSubmit")
    public String modelSubmit(@RequestBody String body) {
        JSONObject json = new JSONObject();
        json.put("mid", "");//{“code”:”000”,”msg”:”成功”,”mid”:” 202109011541207100001”}
        //请求内容
        if (StringUtils.isBlank(body)) {
            json.put("code", "-5005");
            json.put("msg", "body内容为空");
            return json.toJSONString();
        }
//        try {
//            body= URLDecoder.decode(body,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
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
        //短信签名id
        String sign_id = bodyJson.containsKey("sign")?bodyJson.getString("sign"):"";
        //模板标题(按 urlencode编码 utf-8)
        String title = bodyJson.containsKey("title")?bodyJson.getString("title"):"";
        try {
         title=URLDecoder.decode(title,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //变量标志（1表示变量模板，2表示非变量模板）
        String variable = bodyJson.containsKey("var")?bodyJson.getString("var"):"2";

        String timestamp = bodyJson.containsKey("ts")?bodyJson.getString("ts"):"";
        String url = bodyJson.containsKey("url")?bodyJson.getString("url"):"";
        String token = bodyJson.containsKey("token")?bodyJson.getString("token"):"";
        //模板媒体的文件内容(json 数组格式字符串)
        JSONArray contentArr = null;
        try{
            contentArr = bodyJson.containsKey("content")?bodyJson.getJSONArray("content"):null;
        }catch (Exception e){

        }
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

        if (StringUtils.isBlank(sign_id)){
            json.put("code", "-5012");
            json.put("msg", "短信签名ID为空");
            return json.toJSONString();
        }
        if (contentArr==null){
            json.put("code", "-5013");
            json.put("msg", "模板内容格式不正确");
            return json.toJSONString();
        }
        if (StringUtils.isBlank(title)){
            json.put("code", "-5014");
            json.put("msg", "模板标题为空");
            return json.toJSONString();
        }
        try {
            title= URLDecoder.decode(title,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
        String backModel = appJson.getString("rpt_model_address");

        //acc=xxx&ts=xxx|||pwd=xxxxxx
        StringBuilder sb = new StringBuilder("acc=");
        sb.append(appName).append("&ts=").append(timestamp).append("&sign=").append(sign_id).append("|||pwd=").append(password);

        String tokenSign = MD5Utils.MD5Encode(sb.toString());
        if(!tokenSign.toLowerCase().equals(token.toLowerCase())){
            json.put("code", "-5004");
            json.put("msg", "数字签名无效");
            return json.toJSONString();
        }

        String payment = appJson.getString("payment");
        long limitCount = appJson.getLong("limit_count");
        if("advance".equals(payment)) {
            //客户消费数
            String consume = RedisUtils.hash_get(RedisUtils.HASH_ACC_SEND, appName);
            long total = limitCount-Integer.parseInt(StringUtils.isNotBlank(consume)?consume:"0");

            if (total <=0) {
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

        long maxId = getMaxId("e_model_info","id");
        //模板id
//        String modelId="M"+variable+"1"+appId+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        String modelId="M"+new SimpleDateFormat("yyyyMMdd").format(new Date())+variable+"1"+appId+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        String submitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        bodyJson.put("variable",variable);//1-变量视频短信模板，2-普通视频短信模板
        bodyJson.put("appId",appId);//用户id
        bodyJson.put("submitTime",submitTime);//提交时间
        bodyJson.put("modelId",modelId);//模板id
        bodyJson.put("companyId",companyId);//公司id
        bodyJson.put("appName",appName);//用户账号
        bodyJson.put("modelFilePath",modelFilePath);//模板文件实际路径
        bodyJson.put("modelFilePaths",modelFilePaths);//模板文件相对路径

        RedisUtils.fifo_push(RedisUtils.FIFO_APP_MODEL_LIST+companyId+":"+appId,bodyJson.toJSONString());
        RedisUtils.hash_incrBy(RedisUtils.HASH_APP_MODEL_TOTAL, companyId+"_"+appId, 1);

        String backUrl=url;
        if(StringUtils.isBlank(url)&&StringUtils.isNotBlank(backModel)){
            backUrl = backModel;//客户回调地址
        }

        //写入模板表
        String updateSql = String.format(" INSERT INTO e_model_info(company_id,app_id,sign_id,model_id,title,variate,status,info,source,back_url,user_id,update_time,create_time) "
                + "VALUES(%s,%s,'%s','%s','%s',%s,%s,'%s',%s,'%s',%s,'%s','%s');",companyId,appId,sign_id,modelId,title,variable,"3","待审核","1",backUrl,"0",submitTime,submitTime);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        json.put("code", "000");
        json.put("msg", "成功");
        json.put("mid", modelId);
        return json.toJSONString();
    }

    @RequestMapping("/modelOne")
    public String modelOne(@RequestParam String body,@RequestParam String ipAddress) {
        JSONObject json = new JSONObject();
        //请求内容
        if (StringUtils.isBlank(body)) {
            json.put("code", "-5005");
            json.put("msg", "body内容为空");
            return json.toJSONString();
        }
//        try {
//            body= URLDecoder.decode(body,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
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
        //模板id
        String modelId = bodyJson.containsKey("mid")?bodyJson.getString("mid"):"";
        String timestamp = bodyJson.containsKey("ts")?bodyJson.getString("ts"):"";
        String token = bodyJson.containsKey("token")?bodyJson.getString("token"):"";

        //用户app_name由视频短信服务方提供
        if (StringUtils.isBlank(appName)) {
            json.put("code", "-5002");
            json.put("msg", "acc内容为空");
            return json.toJSONString();
        }
        //时间戳 访问接口时间  单位：毫秒,允许误差10分钟
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

        if (StringUtils.isBlank(modelId)){
            json.put("code", "-5012");
            json.put("msg", "模板ID为空");
            return json.toJSONString();
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

        //acc=xxx&ts=xxx|||pwd=xxxxxx
        StringBuilder sb = new StringBuilder("acc=");
        sb.append(appName).append("&ts=").append(timestamp).append("|||pwd=").append(password);

        String tokenSign = MD5Utils.MD5Encode(sb.toString());
        if(!tokenSign.toLowerCase().equals(token.toLowerCase())){
            json.put("code", "-5004");
            json.put("msg", "数字签名无效");
            return json.toJSONString();
        }

        /*String auth_ip = appJson.getString("auth_ip");
        //ipAddress = "0.0.0.0";//不限制ip
        if(auth_ip.indexOf(ipAddress)==-1&&auth_ip.indexOf("0.0.0.0")==-1){
            json.put("code", "-5025");
            json.put("msg", "IP地址受限");
            return json.toJSONString();
        }*/
        Map<String,String> map = new HashMap<String,String>();
        map.put("companyId",companyId);
        map.put("appId",appId);
        map.put("modelId",modelId);

        List<Map<String,String>> mapList = serviceMapper.selectModelList(map);
        if(mapList == null||mapList.size()==0){
            json.put("code", "-5013");
            json.put("msg", "无此数据");
            return json.toJSONString();
        }
        Map<String,String> mapData = mapList.get(0);
        String signId = mapData.get("sign_id");
        String title = mapData.get("title");
        String variate = String.valueOf(mapData.get("variate"));//变量标志：1-变量模板 2-普通模板
        String status = String.valueOf(mapData.get("status"));//状态：1-审核成功2-审核拒绝3-待审核
        String info = mapData.get("info");
        String updateTime = String.valueOf(mapData.get("update_time"));
        JSONObject data = new JSONObject();
        data.put("acc",appName);
        data.put("mid",modelId);
        data.put("sign",signId);
        data.put("title",title);
        data.put("var",variate);
        if(status.equals("1")||status.equals("3")||status.equals("4")){
            status="3";
            info="待审核";
        }else if(status.equals("2")||status.equals("6")){
            status="2";
        }else{
            status="1";
            info="审核成功";
        }
        data.put("status",status);
        data.put("info",info);
        data.put("cmcc",1);
        data.put("unicom",1);
        data.put("telecom",1);
        data.put("time",updateTime);

        json.put("code", "000");
        json.put("msg", "查询成功");
        json.put("data", data.toJSONString());
        return json.toJSONString();
    }


    @RequestMapping("/signSubmit")
    public String signSubmit(@RequestBody String body) {
        JSONObject json = new JSONObject();
        json.put("signId", "");//{“code”:”000”,”msg”:”成功”,”signId”:” 202109011541207100001”}
        //请求内容
        if (StringUtils.isBlank(body)) {
            json.put("code", "-5005");
            json.put("msg", "body内容为空");
            return json.toJSONString();
        }
//        try {
//            body= URLDecoder.decode(body,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
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
        //短信签名id
        String ecProvince = bodyJson.containsKey("ecProvince")?bodyJson.getString("ecProvince"):"";
        //模板标题(按 urlencode编码 utf-8)
        String ecCity = bodyJson.containsKey("ecCity")?bodyJson.getString("ecCity"):"";
        String reportSignContent = bodyJson.containsKey("reportSignContent")?bodyJson.getString("reportSignContent"):"";
        String ecName = bodyJson.containsKey("ecName")?bodyJson.getString("ecName"):"";
        String rcsIndustry = bodyJson.containsKey("rcsIndustry")?bodyJson.getString("rcsIndustry"):"";
        String industry = bodyJson.containsKey("industry")?bodyJson.getString("industry"):"";


        String timestamp = bodyJson.containsKey("ts")?bodyJson.getString("ts"):"";
        String url = bodyJson.containsKey("url")?bodyJson.getString("url"):"";
        String token = bodyJson.containsKey("token")?bodyJson.getString("token"):"";
        //模板媒体的文件内容(json 数组格式字符串)
        JSONArray contentArr = null;
        contentArr = bodyJson.containsKey("content")?bodyJson.getJSONArray("content"):null;
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


        if (contentArr==null){
            json.put("code", "-5013");
            json.put("msg", "模板内容格式不正确");
            return json.toJSONString();
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
        String backSign = appJson.getString("rpt_sign_address");

        //acc=xxx&ts=xxx|||pwd=xxxxxx
        StringBuilder sb = new StringBuilder("acc=");
        sb.append(appName).append("&ts=").append(timestamp).append("&reportSignContent=").append(reportSignContent).append("|||pwd=").append(password);

        String tokenSign = MD5Utils.MD5Encode(sb.toString());
        if(!tokenSign.toLowerCase().equals(token.toLowerCase())){
            json.put("code", "-5004");
            json.put("msg", "数字签名无效");
            return json.toJSONString();
        }

        String payment = appJson.getString("payment");
        long limitCount = appJson.getLong("limit_count");
        if("advance".equals(payment)) {
            //客户消费数
            String consume = RedisUtils.hash_get(RedisUtils.HASH_ACC_SEND, appName);
            long total = limitCount-Integer.parseInt(StringUtils.isNotBlank(consume)?consume:"0");

            if (total <=0) {
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

        long maxId = getSignMaxId("e_model_sign","id");
        //模板id
//        String modelId="M"+variable+"1"+appId+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        String signId="S"+new SimpleDateFormat("MMdd").format(new Date())+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        String submitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        bodyJson.put("appId",appId);//用户id
        bodyJson.put("submitTime",submitTime);//提交时间
        bodyJson.put("signId",signId);//模板id
        bodyJson.put("companyId",companyId);//公司id
        bodyJson.put("appName",appName);//用户账号
        bodyJson.put("signFilePath",signFilePath);//签名文件相对路径下级目录 ${sign.filePath} signFile
        bodyJson.put("signFilePaths",signFilePaths);//签名文件ip地址前缀，ip+port+相对路径 http://103.29.16.3:9100/profile/
        bodyJson.put("signProfile",profile);//签名文件绝对路径 /home/platform/mmsUploadPath/



        String backUrl=url;
        if(StringUtils.isBlank(url)&&StringUtils.isNotBlank(backSign)){
            backUrl = backSign;//客户回调地址
        }
        //TODO 素材保存，插入数据库，图片访问地址 例如：http://103.29.16.3:9100/profile/signFile/20230210/1sucai_20230210163547A007.jpg

        RedisUtils.fifo_push(RedisUtils.FIFO_APP_SIGN_LIST+companyId+":"+appId,bodyJson.toJSONString());
        RedisUtils.hash_incrBy(RedisUtils.HASH_APP_SIGN_TOTAL, companyId+"_"+appId, 1);

        //写入签名表
        String updateSql = String.format(" INSERT INTO e_model_sign(company_id,app_id,sign_id,app_name,ec_province,ec_city,report_sign_content,ec_name,rcsIndustry,industry,customer_type,operator_type,info,status,source,back_url,user_id,create_time,update_time) "
                + "VALUES(%s,%s,'%s','%s','%s','%s','%s','%s','%s','%s',%s,%s,'%s',%s,%s,'%s',%s,'%s','%s');",companyId,appId,signId,appName,ecProvince,ecCity,reportSignContent,ecName,rcsIndustry,industry,1,1,"待审核",3,1,backUrl,0,submitTime,submitTime);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        json.put("code", "000");
        json.put("msg", "成功");
        json.put("signId",signId);
        return json.toJSONString();
    }


    @RequestMapping("/signOne")
    public String signOne(@RequestParam String body,@RequestParam String ipAddress) {
        JSONObject json = new JSONObject();
        //请求内容
        if (StringUtils.isBlank(body)) {
            json.put("code", "-5005");
            json.put("msg", "body内容为空");
            return json.toJSONString();
        }
//        try {
//            body= URLDecoder.decode(body,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
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
        //模板id
        String signId = bodyJson.containsKey("signId")?bodyJson.getString("signId"):"";
        String timestamp = bodyJson.containsKey("ts")?bodyJson.getString("ts"):"";
        String token = bodyJson.containsKey("token")?bodyJson.getString("token"):"";

        //用户app_name由视频短信服务方提供
        if (StringUtils.isBlank(appName)) {
            json.put("code", "-5002");
            json.put("msg", "acc内容为空");
            return json.toJSONString();
        }
        //时间戳 访问接口时间  单位：毫秒,允许误差10分钟
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

        if (StringUtils.isBlank(signId)){
            json.put("code", "-5012");
            json.put("msg", "签名ID为空");
            return json.toJSONString();
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

        //acc=xxx&ts=xxx|||pwd=xxxxxx
        StringBuilder sb = new StringBuilder("acc=");
        sb.append(appName).append("&ts=").append(timestamp).append("|||pwd=").append(password);

        String tokenSign = MD5Utils.MD5Encode(sb.toString());
        if(!tokenSign.toLowerCase().equals(token.toLowerCase())){
            json.put("code", "-5004");
            json.put("msg", "数字签名无效");
            return json.toJSONString();
        }

        /*String auth_ip = appJson.getString("auth_ip");
        //ipAddress = "0.0.0.0";//不限制ip
        if(auth_ip.indexOf(ipAddress)==-1&&auth_ip.indexOf("0.0.0.0")==-1){
            json.put("code", "-5025");
            json.put("msg", "IP地址受限");
            return json.toJSONString();
        }*/
        Map<String,String> map = new HashMap<String,String>();
        map.put("companyId",companyId);
        map.put("appId",appId);
        map.put("signId",signId);

        List<Map<String,String>> mapList = serviceMapper.selectSignList(map);
        if(mapList == null||mapList.size()==0){
            json.put("code", "-5013");
            json.put("msg", "无此数据");
            return json.toJSONString();
        }
        Map<String,String> mapData = mapList.get(0);
        String cmcc = String.valueOf(mapData.containsKey("to_cmcc")?mapData.get("to_cmcc"):"2");
        String unicom = String.valueOf(mapData.containsKey("to_unicom")?mapData.get("to_unicom"):"2");
        String telecom = String.valueOf(mapData.containsKey("to_telecom")?mapData.get("to_telecom"):"2");
        if(StringUtils.isBlank(cmcc)){
            cmcc="2";
        }
        if(StringUtils.isBlank(unicom)){
            unicom="2";
        }
        if(StringUtils.isBlank(telecom)){
            telecom="2";
        }
        String reportSignContent = mapData.get("report_sign_content");
        String status = String.valueOf(mapData.get("status"));//状态：1-审核成功2-审核拒绝3-待审核
        String info = mapData.get("info");
        String updateTime = String.valueOf(mapData.get("update_time"));
        JSONObject data = new JSONObject();
        data.put("acc",appName);
        data.put("signId",signId);

        if(status.equals("1")||status.equals("5")){
            status="1";
            info="审核成功";

        }else if(status.equals("2")||status.equals("6")){
            status="2";
            info="审核失败";
        }else{
            status="3";
            info="待审核";
        }
        data.put("status",status);
        data.put("info",info);
        data.put("cmcc",cmcc);
        data.put("unicom",unicom);
        data.put("telecom",telecom);
        data.put("time",updateTime);
        data.put("reportSignContent",reportSignContent);

        json.put("code", "000");
        json.put("msg", "查询成功");
        json.put("data", data.toJSONString());
        return json.toJSONString();
    }


    @RequestMapping("/modelSubmitToChannel")
    public String modelSubmitToChannel(@RequestBody String body) {
        /*
        模版数据：{"data":{"1":[{"size":"1604","sort":"1","type":"2","content":"/profile/modelFile/20230215/F16764486851192.jpg"},
        {"size":"21","sort":"2","type":"1","content":"测试时间：$v1$ \r\n 测试人：$v2$"}]},
        "modelInfo":"{\"appExt\":106102,\"appId\":3,\"appName\":\"xiuzhi\",\"channelId\":1,\"companyId\":100,
        \"companyName\":\"修治科技\",\"extNum\":\"1061029\",\"id\":9,\"modelExt\":9,\"modelId\":\"M02151191\",
        \"params\":{},\"signId\":\"45306\",\"source\":2,\"title\":\"模版提交接口测试\",\"toCmcc\":1,\"toInternational\":2,
        \"toTelecom\":2,\"toUnicom\":2,\"variate\":1}"}

         */
        System.out.println("模版数据："+body);
        JSONObject json = new JSONObject();
        json.put("mid", "");//{“code”:”000”,”msg”:”成功”,”mid”:” 202109011541207100001”}
        //请求内容
        if (StringUtils.isBlank(body)) {
            json.put("code", "-5005");
            json.put("msg", "body内容为空");
            return json.toJSONString();
        }

        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            json.put("code", "-5005");
            json.put("msg", "body内容异常");
            return json.toJSONString();
        }
        /*
        s: {"data":{"1":[{"size":"244","sort":"1","type":"2","content":"/profile/modelFile/20221209/截屏2022-12-08 15.01.08_20221209123337A002.png"},{"size":"6","sort":"2","type":"1","content":"测试6666"}],
                    "2":[{"size":"54","sort":"1","type":"2","content":"/profile/modelFile/20221209/截屏2022-12-08 12.50.23_20221209123351A003.png"},{"size":"2","sort":"2","type":"1","content":"测试"}]},
            "modelInfo":{"searchValue":null,"createBy":null,"createTime":"2022-12-09 04:34:24","updateBy":null,"companyId":100,"updateTime":"2022-12-09 04:34:24","remark":null,"params":{},"channelId":null,"id":1,"appId":3,"signId":"1","modelId":"M1233424","title":"测试1","variate":1,"status":3,"info":"待审核","source":2,"backUrl":"","userId":1,"companyName":"修治科技","appName":"xiuzhi","userName":"修治"}}

         */
        //模版基本信息
        JSONObject modelInfo = JSONObject.parseObject(bodyJson.getString("modelInfo"));

        //用户账号
        String appName = modelInfo.containsKey("appName")?modelInfo.getString("appName"):"";
        //短信签名id
        String sign_id = modelInfo.containsKey("signId")?modelInfo.getString("signId"):"";
        //模板标题(按 urlencode编码 utf-8)
        String title = modelInfo.containsKey("title")?modelInfo.getString("title"):"";
        //变量标志（1表示变量模板，2表示非变量模板）
        String variable = modelInfo.containsKey("variate")?modelInfo.getString("variate"):"2";
        //提交渠道id
        String channelId = modelInfo.containsKey("channelId")?modelInfo.getString("channelId"):"";
        //模版id
        String modelId = modelInfo.containsKey("modelId")?modelInfo.getString("modelId"):"";

//        String timestamp = bodyJson.containsKey("ts")?bodyJson.getString("ts"):"";
        String url = modelInfo.containsKey("backUrl")?modelInfo.getString("backUrl"):"";
//        String token = bodyJson.containsKey("token")?bodyJson.getString("token"):"";
        //模板媒体的文件内容(json 数组格式字符串)
        Map<Integer,JSONArray> map=(Map<Integer,JSONArray>)bodyJson.get("data");
//        JSONArray contentArr = null;
//        try{
//            contentArr = bodyJson.containsKey("data")?bodyJson.getJSONArray("content"):null;
//        }catch (Exception e){
//
//        }
        //用户app_name由视频短信服务方提供
        if (StringUtils.isBlank(appName)) {
            json.put("code", "-5002");
            json.put("msg", "acc内容为空");
            return json.toJSONString();
        }
        if (StringUtils.isBlank(channelId)) {
            json.put("code", "-5002");
            json.put("msg", "channelId内容为空");
            return json.toJSONString();
        }
        //时间戳 访问接口时间  单位：毫秒,允许误差1分钟
//        if (StringUtils.isBlank(timestamp)) {
//            json.put("code", "-5003");
//            json.put("msg", "timestamp内容为空");
//            return json.toJSONString();
//        }
//        long time = 0;
//        try{
//            time = Long.valueOf(timestamp);
//        }catch(Exception e){
//            json.put("code", "-5003");
//            json.put("msg", "timestamp无效");
//            return json.toJSONString();
//        }
//        long dur = System.currentTimeMillis()-time;
//        if(dur>600000){
//            json.put("code", "-5003");
//            json.put("msg", "timestamp过期时间超过10分钟");
//            return json.toJSONString();
//        }
        //数字签名 参考 token MD5校验和，token=md5(acc=xxx&ts=xxx|||pwd=xxxxxx)
//        if (StringUtils.isBlank(token)) {
//            json.put("code", "-5004");
//            json.put("msg", "sign内容为空");
//            return json.toJSONString();
//        }

        if (StringUtils.isBlank(sign_id)){
            json.put("code", "-5012");
            json.put("msg", "短信签名ID为空");
            return json.toJSONString();
        }
        if (map==null){
            json.put("code", "-5013");
            json.put("msg", "模板内容格式不正确");
            return json.toJSONString();
        }
        if (StringUtils.isBlank(title)){
            json.put("code", "-5014");
            json.put("msg", "模板标题为空");
            return json.toJSONString();
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
        String backModel = appJson.getString("rpt_model_address");

        //acc=xxx&ts=xxx|||pwd=xxxxxx
//        StringBuilder sb = new StringBuilder("acc=");
//        sb.append(appName).append("&ts=").append(timestamp).append("&sign=").append(sign_id).append("|||pwd=").append(password);
//
//        String tokenSign = MD5Utils.MD5Encode(sb.toString());
//        if(!tokenSign.toLowerCase().equals(token.toLowerCase())){
//            json.put("code", "-5004");
//            json.put("msg", "数字签名无效");
//            return json.toJSONString();
//        }

        String payment = appJson.getString("payment");
        long limitCount = appJson.getLong("limit_count");
        if("advance".equals(payment)) {
            //客户消费数
            String consume = RedisUtils.hash_get(RedisUtils.HASH_ACC_SEND, appName);
            long total = limitCount-Integer.parseInt(StringUtils.isNotBlank(consume)?consume:"0");

            if (total <=0) {
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

//        long maxId = getMaxId("e_model_info","id");
//        //模板id
//        String modelId="M"+variable+"1"+appId+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
//        String submitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//
//        bodyJson.put("variable",variable);//1-变量视频短信模板，2-普通视频短信模板
//        bodyJson.put("appId",appId);//用户id
//        bodyJson.put("submitTime",submitTime);//提交时间
//        bodyJson.put("modelId",modelId);//模板id
//        bodyJson.put("companyId",companyId);//公司id
//        bodyJson.put("appName",appName);//用户账号
        bodyJson.put("modelFilePath",modelFilePath);//模板文件实际路径
        bodyJson.put("modelFilePaths",modelFilePaths);//模板文件相对路径
        bodyJson.put("password",password);
        String backUrl=url;
        if(StringUtils.isBlank(url)&&StringUtils.isNotBlank(backModel)){
            backUrl = backModel;//客户回调地址
        }
        bodyJson.put("backUrl",backUrl);
        RedisUtils.fifo_push(RedisUtils.FIFO_CHANNEL_MODEL_LIST+companyId+":"+appId+":"+channelId,bodyJson.toJSONString());
        RedisUtils.hash_incrBy(RedisUtils.HASH_CHANNEL_MODEL_TOTAL, companyId+"_"+appId+"_"+channelId, 1);



        //写入模板表
//        String updateSql = String.format(" INSERT INTO e_model_info(company_id,app_id,sign_id,model_id,title,variate,status,info,source,back_url,user_id,update_time,create_time) "
//                + "VALUES(%s,%s,'%s','%s','%s',%s,%s,'%s',%s,'%s',%s,'%s','%s');",companyId,appId,sign_id,modelId,title,variable,"3","待审核","1",backUrl,"0",submitTime,submitTime);
//        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        json.put("code", "000");
        json.put("msg", "成功");
        json.put("mid", modelId);
        return json.toJSONString();
    }

    @RequestMapping("/signSubmitToChannel")
    public String signSubmitToChannel(@RequestBody String body) {
        System.out.println("签名数据："+body);
        JSONObject json = new JSONObject();
        json.put("sid", "");//{“code”:”000”,”msg”:”成功”,”sid”:” 202109011541207100001”}
        //请求内容
        if (StringUtils.isBlank(body)) {
            json.put("code", "-5005");
            json.put("msg", "body内容为空");
            return json.toJSONString();
        }

        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            json.put("code", "-5005");
            json.put("msg", "body内容异常");
            return json.toJSONString();
        }

        //签名基本信息
        //用户账号
        String appName = bodyJson.containsKey("appName")?bodyJson.getString("appName"):"";
        //短信签名id
        String sign_id = bodyJson.containsKey("signId")?bodyJson.getString("signId"):"";
        //省份
        String ecProvince = bodyJson.containsKey("ecProvince")?bodyJson.getString("ecProvince"):"";
        //地市
        String ecCity = bodyJson.containsKey("ecCity")?bodyJson.getString("ecCity"):"";
        //提交渠道id
        String channelId = bodyJson.containsKey("channelId")?bodyJson.getString("channelId"):"";
        //服务代码
        String serviceCode = bodyJson.containsKey("serviceCode")?bodyJson.getString("serviceCode"):"";
        //签名内容
        String reportSignContent = bodyJson.containsKey("reportSignContent")?bodyJson.getString("reportSignContent"):"";
        //实际发送企业
        String ecName = bodyJson.containsKey("ecName")?bodyJson.getString("ecName"):"";
        //行业属性
        String rcsIndustry = bodyJson.containsKey("rcsIndustry")?bodyJson.getString("rcsIndustry"):"";
        //行业分类
        String industry = bodyJson.containsKey("industry")?bodyJson.getString("industry"):"";
        //客户分类
        Long customerType = bodyJson.containsKey("customerType")?bodyJson.getLong("customerType"):1l;
        //操作类型
        Long operatorType = bodyJson.containsKey("operatorType")?bodyJson.getLong("operatorType"):1l;
        //申请文件地址
        String filepath = bodyJson.containsKey("filepath")?bodyJson.getString("filepath"):"";

        Long id = bodyJson.containsKey("id")?bodyJson.getLong("id"):0l;

//        if (StringUtils.isBlank(filepath)) {
//            json.put("code", "-5002");
//            json.put("msg", "申请文件为空");
//            return json.toJSONString();
//        }

        if (StringUtils.isBlank(reportSignContent)) {
            json.put("code", "-5002");
            json.put("msg", "签名内容为空");
            return json.toJSONString();
        }
        //用户app_name由视频短信服务方提供
        if (StringUtils.isBlank(appName)) {
            json.put("code", "-5002");
            json.put("msg", "acc内容为空");
            return json.toJSONString();
        }
        if (StringUtils.isBlank(channelId)) {
            json.put("code", "-5002");
            json.put("msg", "channelId内容为空");
            return json.toJSONString();
        }

        if (StringUtils.isBlank(sign_id)){
            json.put("code", "-5012");
            json.put("msg", "短信签名ID为空");
            return json.toJSONString();
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
//        String backModel = appJson.getString("rpt_sign_address");


        String payment = appJson.getString("payment");
        long limitCount = appJson.getLong("limit_count");
        if("advance".equals(payment)) {
            //客户消费数
            String consume = RedisUtils.hash_get(RedisUtils.HASH_ACC_SEND, appName);
            long total = limitCount-Integer.parseInt(StringUtils.isNotBlank(consume)?consume:"0");

            if (total <=0) {
                json.put("code", "-5010");
                json.put("msg", "余额不足");
                return json.toJSONString();
            }
        }
        bodyJson.put("password",password);


        RedisUtils.fifo_push(RedisUtils.FIFO_CHANNEL_SIGN_LIST+companyId+":"+appId+":"+channelId,bodyJson.toJSONString());
        RedisUtils.hash_incrBy(RedisUtils.HASH_CHANNEL_SIGN_TOTAL, companyId+"_"+appId+"_"+channelId, 1);



        //写入模板表
//        String updateSql = String.format(" INSERT INTO e_model_info(company_id,app_id,sign_id,model_id,title,variate,status,info,source,back_url,user_id,update_time,create_time) "
//                + "VALUES(%s,%s,'%s','%s','%s',%s,%s,'%s',%s,'%s',%s,'%s','%s');",companyId,appId,sign_id,modelId,title,variable,"3","待审核","1",backUrl,"0",submitTime,submitTime);
//        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        json.put("code", "000");
        json.put("msg", "成功");
        json.put("sid", id);
        return json.toJSONString();
    }

    @RequestMapping("/mmsReport")
    public String mmsReport(@RequestParam String table,@RequestParam String ids,@RequestParam String ipAddress) {
        JSONObject json = new JSONObject();
        json.put("data", "");//{“code”:”000”,”msg”:”成功”,”data”:””}
        //请求内容
        if (StringUtils.isBlank(ids)) {
            json.put("code", "-5005");
            json.put("msg", "ids内容为空");
            return json.toJSONString();
        }
        if(StringUtils.isBlank(table)){
            table=new SimpleDateFormat("yyyyMMdd").format(new Date());
        }

        table = "mms_sender_"+table;
        JSONObject data = new JSONObject();
        data.put("table",table);
        data.put("ids",ids);

        //重推状态报告
        RedisUtils.fifo_push(RedisUtils.FIFO_REPORT_LIST,data.toJSONString());

        json.put("code", "000");
        json.put("msg", "成功");
        json.put("data", table+"|ids="+ids);
        return json.toJSONString();
    }
    public long getMaxId(String tableName, String field) {
        long max = 0;
        try {
            String key = RedisUtils.HASH_TABLE_MAX_INDEX;
            long object=RedisUtils.hash_incrBy(key,"model",1);
            if(object==1){
                int maxSize = serviceMapper.selectMaxId(tableName,field);
                if(maxSize!=0){
                    RedisUtils.hash_incrBy(key,"model",maxSize);
                    object+=maxSize;
                }
            }
            max = object + 1000;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取e_model_info主键最大id失败");
        }
        return max;
    }
    public long getSignMaxId(String tableName, String field) {
        long max = 0;
        try {
            String key = RedisUtils.HASH_TABLE_MAX_INDEX;
            long object=RedisUtils.hash_incrBy(key,"sign",1);
            if(object==1){
                int maxSize = serviceMapper.selectMaxId(tableName,field);
                if(maxSize!=0){
                    RedisUtils.hash_incrBy(key,"sign",maxSize);
                    object+=maxSize;
                }
            }
            max = object + 1000;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取e_model_info主键最大id失败");
        }
        return max;
    }



    @RequestMapping("/modelNewSubmit")
    public String modelNewSubmit(@RequestBody String body) {
        JSONObject json = new JSONObject();
        json.put("mid", "");//{“code”:”000”,”msg”:”成功”,”mid”:” 202109011541207100001”}
        //请求内容
        if (StringUtils.isBlank(body)) {
            json.put("code", "-5005");
            json.put("msg", "body内容为空");
            return json.toJSONString();
        }
//        try {
//            body= URLDecoder.decode(body,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
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
        //短信签名id
        String sign_id = bodyJson.containsKey("sign")?bodyJson.getString("sign"):"";
        //模板标题(按 urlencode编码 utf-8)
        String title = bodyJson.containsKey("title")?bodyJson.getString("title"):"";
        try {
            title=URLDecoder.decode(title,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //变量标志（1表示变量模板，2表示非变量模板）
        String variable = bodyJson.containsKey("var")?bodyJson.getString("var"):"2";

        String timestamp = bodyJson.containsKey("ts")?bodyJson.getString("ts"):"";
        String url = bodyJson.containsKey("url")?bodyJson.getString("url"):"";
        String token = bodyJson.containsKey("token")?bodyJson.getString("token"):"";
        //模板媒体的文件内容(json 数组格式字符串)
        JSONArray contentArr = null;
        try{
            contentArr = bodyJson.containsKey("content")?bodyJson.getJSONArray("content"):null;
        }catch (Exception e){

        }
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

        if (StringUtils.isBlank(sign_id)){
            json.put("code", "-5012");
            json.put("msg", "短信签名ID为空");
            return json.toJSONString();
        }
        if (contentArr==null){
            json.put("code", "-5013");
            json.put("msg", "模板内容格式不正确");
            return json.toJSONString();
        }
        if (StringUtils.isBlank(title)){
            json.put("code", "-5014");
            json.put("msg", "模板标题为空");
            return json.toJSONString();
        }
        try {
            title= URLDecoder.decode(title,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
        String backModel = appJson.getString("rpt_model_address");

        //acc=xxx&ts=xxx|||pwd=xxxxxx
        StringBuilder sb = new StringBuilder("acc=");
        sb.append(appName).append("&ts=").append(timestamp).append("&sign=").append(sign_id).append("|||pwd=").append(password);

        String tokenSign = MD5Utils.MD5Encode(sb.toString());
        if(!tokenSign.toLowerCase().equals(token.toLowerCase())){
            json.put("code", "-5004");
            json.put("msg", "数字签名无效");
            return json.toJSONString();
        }

        String payment = appJson.getString("payment");
        long limitCount = appJson.getLong("limit_count");
        if("advance".equals(payment)) {
            //客户消费数
            String consume = RedisUtils.hash_get(RedisUtils.HASH_ACC_SEND, appName);
            long total = limitCount-Integer.parseInt(StringUtils.isNotBlank(consume)?consume:"0");

            if (total <=0) {
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

        long maxId = getMaxId("e_model_info_new","id");
        //模板id
//        String modelId="M"+variable+"1"+appId+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        String modelId="M"+new SimpleDateFormat("MMdd").format(new Date())+(maxId==0?new SimpleDateFormat("mmss").format(new Date()):maxId);
        String submitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        bodyJson.put("variable",variable);//1-变量视频短信模板，2-普通视频短信模板
        bodyJson.put("appId",appId);//用户id
        bodyJson.put("submitTime",submitTime);//提交时间
        bodyJson.put("modelId",modelId);//模板id
        bodyJson.put("companyId",companyId);//公司id
        bodyJson.put("appName",appName);//用户账号
        bodyJson.put("modelFilePath",modelFilePath);//模板文件实际路径
        bodyJson.put("modelFilePaths",modelFilePaths);//模板文件相对路径

        RedisUtils.fifo_push(RedisUtils.FIFO_APP_MODEL_LIST+companyId+":"+appId,bodyJson.toJSONString());
        RedisUtils.hash_incrBy(RedisUtils.HASH_APP_MODEL_TOTAL, companyId+"_"+appId, 1);

        String backUrl=url;
        if(StringUtils.isBlank(url)&&StringUtils.isNotBlank(backModel)){
            backUrl = backModel;//客户回调地址
        }

        //写入模板表
        String updateSql = String.format(" INSERT INTO e_model_info_new(company_id,app_id,sign_id,model_id,title,variate,status,info,source,back_url,user_id,update_time,create_time) "
                + "VALUES(%s,%s,'%s','%s','%s',%s,%s,'%s',%s,'%s',%s,'%s','%s');",companyId,appId,sign_id,modelId,title,variable,"3","待审核","1",backUrl,"0",submitTime,submitTime);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        json.put("code", "000");
        json.put("msg", "成功");
        json.put("mid", modelId);
        return json.toJSONString();
    }

    @RequestMapping("/modelNewOne")
    public String modelNewOne(@RequestParam String body,@RequestParam String ipAddress) {
        JSONObject json = new JSONObject();
        //请求内容
        if (StringUtils.isBlank(body)) {
            json.put("code", "-5005");
            json.put("msg", "body内容为空");
            return json.toJSONString();
        }
//        try {
//            body= URLDecoder.decode(body,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
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
        //模板id
        String modelId = bodyJson.containsKey("mid")?bodyJson.getString("mid"):"";
        String timestamp = bodyJson.containsKey("ts")?bodyJson.getString("ts"):"";
        String token = bodyJson.containsKey("token")?bodyJson.getString("token"):"";

        //用户app_name由视频短信服务方提供
        if (StringUtils.isBlank(appName)) {
            json.put("code", "-5002");
            json.put("msg", "acc内容为空");
            return json.toJSONString();
        }
        //时间戳 访问接口时间  单位：毫秒,允许误差10分钟
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

        if (StringUtils.isBlank(modelId)){
            json.put("code", "-5012");
            json.put("msg", "模板ID为空");
            return json.toJSONString();
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

        //acc=xxx&ts=xxx|||pwd=xxxxxx
        StringBuilder sb = new StringBuilder("acc=");
        sb.append(appName).append("&ts=").append(timestamp).append("|||pwd=").append(password);

        String tokenSign = MD5Utils.MD5Encode(sb.toString());
        if(!tokenSign.toLowerCase().equals(token.toLowerCase())){
            json.put("code", "-5004");
            json.put("msg", "数字签名无效");
            return json.toJSONString();
        }

        /*String auth_ip = appJson.getString("auth_ip");
        //ipAddress = "0.0.0.0";//不限制ip
        if(auth_ip.indexOf(ipAddress)==-1&&auth_ip.indexOf("0.0.0.0")==-1){
            json.put("code", "-5025");
            json.put("msg", "IP地址受限");
            return json.toJSONString();
        }*/
        Map<String,String> map = new HashMap<String,String>();
        map.put("companyId",companyId);
        map.put("appId",appId);
        map.put("modelId",modelId);

        List<Map<String,String>> mapList = serviceMapper.selectModelNewList(map);
        if(mapList == null||mapList.size()==0){
            json.put("code", "-5013");
            json.put("msg", "无此数据");
            return json.toJSONString();
        }
        Map<String,String> mapData = mapList.get(0);
        String cmcc = String.valueOf(mapData.containsKey("to_cmcc")?mapData.get("to_cmcc"):"2");
        String unicom = String.valueOf(mapData.containsKey("to_unicom")?mapData.get("to_unicom"):"2");
        String telecom = String.valueOf(mapData.containsKey("to_telecom")?mapData.get("to_telecom"):"2");
        if(StringUtils.isBlank(cmcc)){
            cmcc="2";
        }
        if(StringUtils.isBlank(unicom)){
            unicom="2";
        }
        if(StringUtils.isBlank(telecom)){
            telecom="2";
        }

        String signId = mapData.get("sign_id");
        String title = mapData.get("title");
        String variate = String.valueOf(mapData.get("variate"));//变量标志：1-变量模板 2-普通模板
        String status = String.valueOf(mapData.get("status"));//状态：1-审核成功2-审核拒绝3-待审核
        String info = mapData.get("info");
        String updateTime = String.valueOf(mapData.get("update_time"));
        JSONObject data = new JSONObject();
        data.put("acc",appName);
        data.put("mid",modelId);
        data.put("sign",signId);
        data.put("title",title);
        data.put("var",variate);
        if(status.equals("3")){
            status="3";
            info="待审核";
        }else if(status.equals("2")){
            status="2";
            info="审核失败";
        }else if(status.equals("4")){
            status="4";
            info="审核中";
        }
        else {
            status="1";
            info="审核成功";
        }
        data.put("status",status);
        data.put("info",info);
        data.put("cmcc",cmcc);
        data.put("unicom",unicom);
        data.put("telecom",telecom);
        data.put("time",updateTime);

        json.put("code", "000");
        json.put("msg", "查询成功");
        json.put("data", data.toJSONString());
        return json.toJSONString();
    }
}
