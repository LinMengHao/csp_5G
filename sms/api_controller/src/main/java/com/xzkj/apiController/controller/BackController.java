package com.xzkj.apiController.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.apiController.redis.RedisUtils;
import com.xzkj.apiController.service.IBackService;
import com.xzkj.apiController.util.ConstantsReport;
import com.xzkj.apiController.util.MD5Utils;
import com.xzkj.apiController.util.PublicUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping(value="back")
public class BackController {
    public static Logger logger = LoggerFactory.getLogger("BackController");
    @Resource
    private IBackService backService;

    //北京移动3878--01
    @RequestMapping("mt01")
    @ResponseBody
    public String mt01(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result= ConstantsReport.report01Resp("0000","成功");
        //String result = backService.mt01(body,"1",ipAddress);

        //请求内容{"mobile":"15811047193","messageId":"20220816_1659346550002","type":0,"notifyUrl":"http://103.29.16.3:9130/back/mt01","status":"0","msg":"DELIVRD","bizCode":"2384075185690528863"}
        if (StringUtils.isBlank(body)) {
            result= ConstantsReport.report01Resp("9999","内容为空");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport02,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            result= ConstantsReport.report01Resp("9998","参数异常，非json格式");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport02,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
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

        String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+"1"+":"+messageId,mobile);
        if(StringUtils.isBlank(jsonStr)){
            result= ConstantsReport.report01Resp("9997","重复推送或无对应数据");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport01,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject json = JSONObject.parseObject(jsonStr);
        ConstantsReport.updateMmsReport(json,"1",messageId,mobile,status,msg,bizcode);

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsReport01,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }
    //北京移动3878--01模版审核
    @RequestMapping("model01")
    @ResponseBody
    public String model01(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report01Resp("0","非json数据");
            }
            String msgID = jsonObject.containsKey("mmsId")?jsonObject.getString("mmsId"):"";
            String status = jsonObject.containsKey("status")?jsonObject.getString("status"):"";
            String reason = jsonObject.containsKey("msg")?jsonObject.getString("msg"):"审核未知";
            String type = jsonObject.containsKey("type")?jsonObject.getString("type"):"10086";
            String notifyUrl = jsonObject.containsKey("notifyUrl")?jsonObject.getString("notifyUrl"):"";
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(new Date());

            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MODEL_MT_CHANNEL+"1"+":"+msgID,msgID);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report01Resp("0","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);
            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String modelId = object.containsKey("modelId")?object.getString("modelId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            String pModelId=object.containsKey("pModelId")?object.getString("pModelId"):"";
            logger.info("companyId: {}, modelId: {}, id: {}",companyId,modelId,id);
            logger.info("modelReport01,msgID:{},status:{},reason:{},type:{}",msgID,status,reason,type);
            if("1".equals(status)){

                //TODO 模版状态回调（过渡期）
//                if(modelId.length()<=11){
                    //审核通过后创建模版映射
                    //写入模版映射关系,默认500000日限  状态为停用

                //确保回调只存一次映射关系
                String s = RedisUtils.hash_get(RedisUtils.HASH_MODEL_RELATE_COUNT + pModelId + ":" + "1" + ":" + msgID, "total");
                if(StringUtils.isBlank(s)|| !"true".equals(s)){
                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",pModelId,1,msgID,500000,1,date,date);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

                    RedisUtils.hash_set(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"1"+":"+msgID,"total","true");
                    RedisUtils.hash_setExpire(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"1"+":"+msgID,3*24*3600);
                    logger.info("insertSql: {}",insertSql);
                }


                    //修改模版审核状态（新）
                    if(StringUtils.isBlank(reason)){
                        reason="审核成功";
                    }
                    String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                    logger.info("updateSql: {}",updateSql);
//                }else {
//                    String info="MsgId:<<"+msgID+">>"+"; "+"通道侧审核成功";
//                    //审核通过后创建模版映射
//                    //写入模版映射关系,默认500000日限  状态为停用
//                    String s=modelId;
//                    if(!StringUtils.isBlank(pid)){
//                        s=pid;
//                    }
//                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
//                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",s,2,msgID,500000,1,date,date);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("insertSql: {}",insertSql);
//                    //修改模版审核状态
//                    String updateSql = String.format("update e_model_info set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,info,msgID,id);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("updateSql: {}",updateSql);
//
//
//                    //TODO 定时任务或人工处理 回调客户
//                    object.put("status",1);
//                    object.put("info","审核成功");
//                    RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
//                }

            }else if("0".equals(status)){
                //TODO 模版状态回调（过渡期）
//                if(modelId.length()<=11){
                    if(StringUtils.isBlank(reason)){
                        reason="审核失败";
                    }
                    //修改模版审核状态
                    String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                    logger.info("updateSql: {}",updateSql);
//                }else {
//                    //修改模版审核状态
//                    String updateSql = String.format("update e_model_info set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("updateSql: {}",updateSql);
//
//                    ////TODO 定时任务或人工处理 回调客户
//                    object.put("status",2);
//                    object.put("info",reason);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
//                }

            }
        }
        return ConstantsReport.report01Resp("0","成功");
    }
    //北京移动3878--01 签名审核
    @RequestMapping("sign01")
    @ResponseBody
    public String sign01(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report01Resp("0","非json数据");
            }
            String signId = jsonObject.containsKey("signId")?jsonObject.getString("signId"):"";
            String status = jsonObject.containsKey("status")?jsonObject.getString("status"):"";
            String reason = jsonObject.containsKey("msg")?jsonObject.getString("msg"):"审核未知";
            String serviceCode = jsonObject.containsKey("serviceCode")?jsonObject.getString("serviceCode"):"";
            String customerId = jsonObject.containsKey("customerId")?jsonObject.getString("customerId"):"";
            String reportSignContent = jsonObject.containsKey("reportSignContent")?jsonObject.getString("reportSignContent"):"";
            String auditTime = jsonObject.containsKey("auditTime")?jsonObject.getString("auditTime"):"";

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(new Date());

            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_SIGN_MT_CHANNEL+"1"+":"+signId,signId);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report01Resp("0","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);

            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String pSignId = object.containsKey("pSignId")?object.getString("pSignId"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            logger.info("companyId: {}, signId: {}, id: {}",companyId,signId,id);
            logger.info("signReport01,signId:{},status:{},msg:{},serviceCode:{},customerId:{},reportSignContent:{},auditTime:{}",signId,status,reason,serviceCode,customerId,reportSignContent,auditTime);
            if("1".equals(status)){
                if(!StringUtils.isBlank(pSignId)){
                    String s = RedisUtils.hash_get(RedisUtils.HASH_SIGN_RELATE_COUNT + pSignId + ":" + "1" + ":" + signId, "total");
                    if(StringUtils.isBlank(s)|| !"true".equals(s)){
                    //审核通过后创建签名映射
                    String insertSql = String.format(" INSERT INTO e_sign_related(sign_id,channel_id,channel_sign_id,status,update_time,create_time) "
                            + "VALUES('%s',%s,'%s',%s,'%s','%s');",pSignId,1,signId,1,auditTime,auditTime);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                        RedisUtils.hash_set(RedisUtils.HASH_SIGN_RELATE_COUNT+pSignId+":"+"1"+":"+signId,"total","true");
                        RedisUtils.hash_setExpire(RedisUtils.HASH_SIGN_RELATE_COUNT+pSignId+":"+"1"+":"+signId,7*24*3600);
                    logger.info("insertSql: {}",insertSql);
                    }
                }


                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",5,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);

                //TODO 定时任务或人工处理 回调客户
//                object.put("status",1);
//                object.put("info","审核成功");
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }else if("2".equals(status)){
                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",6,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);

//                //TODO 定时任务或人工处理 回调客户
//                object.put("status",2);
//                object.put("info",reason);
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }else if("100".equals(status)){
                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",1,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);
            }else if("000".equals(status)){
                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",2,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);
            }
        }
        return ConstantsReport.report01Resp("0","成功");
    }

    //北京移动3878--02
    @RequestMapping("mt02ts")
    @ResponseBody
    public String mt02ts(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result=ConstantsReport.report01Resp("0000","成功");
        //String result = backService.mt01(body,"2",ipAddress);

        //请求内容{"mobile":"15811047193","messageId":"20220816_1659346550002","type":0,"notifyUrl":"http://103.29.16.3:9130/back/mt01","status":"0","msg":"DELIVRD","bizCode":"2384075185690528863"}
        if (StringUtils.isBlank(body)) {
            result= ConstantsReport.report01Resp("9999","内容为空");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport02,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            result= ConstantsReport.report01Resp("9998","参数异常，非json格式");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport02,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
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

        String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+"2"+":"+messageId,mobile);
        if(StringUtils.isBlank(jsonStr)){
            result= ConstantsReport.report01Resp("9997","重复推送或无对应数据");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport02,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject json = JSONObject.parseObject(jsonStr);
        ConstantsReport.updateMmsReport(json,"2",messageId,mobile,status,msg,bizcode);

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsReport02,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }
    @RequestMapping("model02ts")
    @ResponseBody
    public String model02ts(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report01Resp("0","非json数据");
            }
            String msgID = jsonObject.containsKey("mmsId")?jsonObject.getString("mmsId"):"";
            String status = jsonObject.containsKey("status")?jsonObject.getString("status"):"";
            String reason = jsonObject.containsKey("msg")?jsonObject.getString("msg"):"审核未知";
            String type = jsonObject.containsKey("type")?jsonObject.getString("type"):"10086";
            String notifyUrl = jsonObject.containsKey("notifyUrl")?jsonObject.getString("notifyUrl"):"";
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(new Date());

            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MODEL_MT_CHANNEL+"2"+":"+msgID,msgID);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report01Resp("0","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);
            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String modelId = object.containsKey("modelId")?object.getString("modelId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            String pModelId=object.containsKey("pModelId")?object.getString("pModelId"):"";
            logger.info("companyId: {}, modelId: {}, id: {}",companyId,modelId,id);
            logger.info("modelReport01,msgID:{},status:{},reason:{},type:{}",msgID,status,reason,type);
            if("1".equals(status)){

                //TODO 模版状态回调（过渡期）
//                if(modelId.length()<=11){
                    //审核通过后创建模版映射
                    //写入模版映射关系,默认500000日限  状态为停用

                String s = RedisUtils.hash_get(RedisUtils.HASH_MODEL_RELATE_COUNT + pModelId + ":" + "2" + ":" + msgID, "total");
                if(StringUtils.isBlank(s)|| !"true".equals(s)){
                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",pModelId,2,msgID,500000,1,date,date);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

                    RedisUtils.hash_set(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"2"+":"+msgID,"total","true");
                    RedisUtils.hash_setExpire(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"2"+":"+msgID,3*24*3600);
                    logger.info("insertSql: {}",insertSql);
                }

                    if(StringUtils.isBlank(reason)){
                        reason="审核成功";
                    }
                    //修改模版审核状态（新）
                    String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                    logger.info("updateSql: {}",updateSql);
//                }else {
//                    String info="MsgId:<<"+msgID+">>"+"; "+"通道侧审核成功";
//                    //审核通过后创建模版映射
//                    //写入模版映射关系,默认500000日限  状态为停用
//                    String s=modelId;
//                    if(!StringUtils.isBlank(pid)){
//                        s=pid;
//                    }
//                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
//                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",s,2,msgID,500000,1,date,date);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("insertSql: {}",insertSql);
//                    //修改模版审核状态
//                    String updateSql = String.format("update e_model_info set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("updateSql: {}",updateSql);
//
//
//                    //TODO 定时任务或人工处理 回调客户
//                    object.put("status",1);
//                    object.put("info","审核成功");
//                    RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
//                }

            }else if("0".equals(status)){
                //TODO 模版状态回调（过渡期）
//                if(modelId.length()<=11){
                    if(StringUtils.isBlank(reason)){
                        reason="审核失败";
                    }
                    //修改模版审核状态
                    String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                    logger.info("updateSql: {}",updateSql);
//                }else {
//                    //修改模版审核状态
//                    String updateSql = String.format("update e_model_info set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("updateSql: {}",updateSql);
//
//                    ////TODO 定时任务或人工处理 回调客户
//                    object.put("status",2);
//                    object.put("info",reason);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
//                }

            }
        }
        return ConstantsReport.report01Resp("0","成功");
    }

    @RequestMapping("sign02ts")
    @ResponseBody
    public String sign02ts(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report01Resp("0","非json数据");
            }
            String signId = jsonObject.containsKey("signId")?jsonObject.getString("signId"):"";
            String status = jsonObject.containsKey("status")?jsonObject.getString("status"):"";
            String reason = jsonObject.containsKey("msg")?jsonObject.getString("msg"):"审核未知";
            String serviceCode = jsonObject.containsKey("serviceCode")?jsonObject.getString("serviceCode"):"";
            String customerId = jsonObject.containsKey("customerId")?jsonObject.getString("customerId"):"";
            String reportSignContent = jsonObject.containsKey("reportSignContent")?jsonObject.getString("reportSignContent"):"";
            String auditTime = jsonObject.containsKey("auditTime")?jsonObject.getString("auditTime"):"";

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(new Date());

            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_SIGN_MT_CHANNEL+"2"+":"+signId,signId);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report01Resp("0","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);

            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String pSignId = object.containsKey("pSignId")?object.getString("pSignId"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            logger.info("companyId: {}, signId: {}, id: {}",companyId,signId,id);
            logger.info("signReport01,signId:{},status:{},msg:{},serviceCode:{},customerId:{},reportSignContent:{},auditTime:{}",signId,status,reason,serviceCode,customerId,reportSignContent,auditTime);
            if("1".equals(status)){
                if(!StringUtils.isBlank(pSignId)){
                    String s = RedisUtils.hash_get(RedisUtils.HASH_SIGN_RELATE_COUNT + pSignId + ":" + "2" + ":" + signId, "total");
                    if(StringUtils.isBlank(s)|| !"true".equals(s)){
                        //审核通过后创建签名映射
                        String insertSql = String.format(" INSERT INTO e_sign_related(sign_id,channel_id,channel_sign_id,status,update_time,create_time) "
                                + "VALUES('%s',%s,'%s',%s,'%s','%s');",pSignId,2,signId,1,auditTime,auditTime);
                        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                        RedisUtils.hash_set(RedisUtils.HASH_SIGN_RELATE_COUNT+pSignId+":"+"2"+":"+signId,"total","true");
                        RedisUtils.hash_setExpire(RedisUtils.HASH_SIGN_RELATE_COUNT+pSignId+":"+"2"+":"+signId,7*24*3600);
                        logger.info("insertSql: {}",insertSql);
                    }
                }


                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",5,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);

                //TODO 定时任务或人工处理 回调客户
//                object.put("status",1);
//                object.put("info","审核成功");
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }else if("2".equals(status)){
                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",6,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);

//                //TODO 定时任务或人工处理 回调客户
//                object.put("status",2);
//                object.put("info",reason);
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }else if("100".equals(status)){
                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",1,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);
            }else if("000".equals(status)){
                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",2,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);
            }
        }
        return ConstantsReport.report01Resp("0","成功");
    }

    @RequestMapping("mt03")
    @ResponseBody
    public String mt03(HttpServletRequest request, HttpServletResponse response,@RequestBody String body){
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result=ConstantsReport.report01Resp("0000","成功");
//        String result = backService.mt03(body,"3",ipAddress);
//
//        long duration = System.currentTimeMillis()-begin;
//        logger.info("mmsReport03,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
//        return result;
        if (StringUtils.isBlank(body)) {
            result= ConstantsReport.report01Resp("9999","内容为空");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport03,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            result= ConstantsReport.report01Resp("9998","参数异常，非json格式");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport03,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        String mobile = bodyJson.containsKey("phone")?bodyJson.getString("phone"):"";
        String messageId = bodyJson.containsKey("transId")?bodyJson.getString("transId"):"";

        //下发状态：0-成功，其他-失败(对应msg描述)
        String status = bodyJson.containsKey("state")?bodyJson.getString("state"):"-1";

        String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+"3"+":"+messageId,mobile);
        if(StringUtils.isBlank(jsonStr)){
            result= ConstantsReport.report01Resp("9997","重复推送或无对应数据");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport03,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject json = JSONObject.parseObject(jsonStr);
        ConstantsReport.updateMmsReport(json,"3",messageId,mobile,status,"","");

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsReport03,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }
    @RequestMapping("model03")
    @ResponseBody
    public String model03(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report01Resp("0000","非json数据");
            }
            String msgID = jsonObject.containsKey("mmsId")?jsonObject.getString("mmsId"):"";
            String state = jsonObject.containsKey("state")?jsonObject.getString("state"):"";
            String reason = jsonObject.containsKey("reason")?jsonObject.getString("reason"):"审核未知";
            String details = jsonObject.containsKey("details")?jsonObject.getString("details"):"";
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(new Date());

            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MODEL_MT_CHANNEL+"3"+":"+msgID,msgID);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report01Resp("0000","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);
            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String modelId = object.containsKey("modelId")?object.getString("modelId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            String pModelId=object.containsKey("pModelId")?object.getString("pModelId"):"";
            logger.info("companyId: {}, modelId: {}, id: {}",companyId,modelId,id);
            logger.info("modelReport01,msgID:{},status:{},reason:{},type:{}",msgID,state,reason,details);

            JSONObject jsonObject1 = JSONObject.parseObject(details);
            //默认只看联通
            //联通通道审核情况
            String cuState = jsonObject1.getString("cuState");
            //移动通道审核情况
            String cmState = jsonObject1.getString("cmState");
            //电信通道审核情况
            String ctState = jsonObject1.getString("ctState");
            if("80".equals(state)&&"80".equals(cuState)){
                String s = RedisUtils.hash_get(RedisUtils.HASH_MODEL_RELATE_COUNT + pModelId + ":" + "3" + ":" + msgID, "total");
                if(StringUtils.isBlank(s)|| !"true".equals(s)){
                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",pModelId,3,msgID,500000,1,date,date);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

                    RedisUtils.hash_set(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"3"+":"+msgID,"total","true");
                    RedisUtils.hash_setExpire(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"3"+":"+msgID,3*24*3600);
                    logger.info("insertSql: {}",insertSql);
                }

                if(StringUtils.isBlank(reason)){
                    reason="审核成功";
                }
                //修改模版审核状态（新）
                String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);
//                String info="MsgId:<<"+msgID+">>"+"; "+"通道侧审核成功";
//                //审核通过后创建模版映射
//                //写入模版映射关系,默认500000日限  状态为停用
//                String s=modelId;
//                if(!StringUtils.isBlank(pid)){
//                    s=pid;
//                }
//                String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
//                        + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",s,3,msgID,500000,1,date,date);
//                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
//                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                logger.info("insertSql: {}",insertSql);
//
//                //修改模版审核状态
//                String updateSql = String.format("update e_model_info set status=%s,info='%s' where id=%s;",5,info,id);
//                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                logger.info("updateSql: {}",updateSql);
//
//                //回调客户
//                object.put("status",1);
//                object.put("info","审核成功");
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }else if("99".equals(state)){
                if(StringUtils.isBlank(reason)){
                    reason="审核失败";
                }
                //修改模版审核状态
                String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);
//                String info="MsgId:<<"+msgID+">>"+"; "+reason;
//                //修改模版审核状态
//                String updateSql = String.format("update e_model_info set status=%s,info='%s' where id=%s;",2,info,id);
//                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                logger.info("updateSql: {}",updateSql);
//
//                //回调客户
//                object.put("status",2);
//                object.put("info",reason);
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }else if("60".equals(state)){
                if(StringUtils.isBlank(reason)){
                    reason="审核失败";
                }
                //修改模版审核状态
                String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);
            }
        }
        return ConstantsReport.report01Resp("0000","成功");
    }


    //物朗
    @RequestMapping("mt04")
    @ResponseBody
    public String mt04(HttpServletRequest request, HttpServletResponse response,@RequestBody String reports) {
        logger.info("reports: {}",reports);
        long begin = System.currentTimeMillis();
        String ipAddress = PublicUtil.getClientIp(request);
        String result=ConstantsReport.report01Resp("0000","成功");
        //String result = backService.mt04(reports,"4",ipAddress);
        /*
        reports=180828100924138386,13912345678,0,DELIVRD,00000020140805135416
        ,20181125112640;180828100924138386,13912345678,0,DELIVRD,
        00000020140805135416,20181125112640
        状态报告是批量返回,直接获取 out=后面的字符串即可，多条状态报告用英文
        分号分隔
         */
        if (StringUtils.isBlank(reports)) {
            long duration = System.currentTimeMillis()-begin;
            result= ConstantsReport.report01Resp("9999","内容为空");
            logger.info("mmsReport04,body:{},ipAddress:{},result:{},duration:{}",reports,ipAddress,result,duration);
            return result;
        }
        try {
            reports = URLDecoder.decode(reports, "utf-8");
        }catch (Exception e){
            logger.info("转码错误");
        }
        logger.info("reports: {}",reports);
        String[] split1 = reports.split(";");
        logger.info("split: {}",split1.length);
        for (int i = 0; i < split1.length; i++) {
            String[] split2 = split1[i].split(",");
            logger.info("split"+(i+1)+": "+split1.length);
            logger.info("split[0]:{}",split2[0]);
            String messageId = split2[0].split("=").length>1?split2[0].split("=")[1]:split2[0];
            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+"4"+":"+messageId,split2[1]);
            if(StringUtils.isBlank(jsonStr)){
                long duration = System.currentTimeMillis()-begin;
                result= ConstantsReport.report01Resp("9997","重复推送或无对应数据");
                logger.info("mmsReport04,body:{},ipAddress:{},result:{},duration:{}",reports,ipAddress,result,duration);
                return result;
            }
            JSONObject json = JSONObject.parseObject(jsonStr);

            ConstantsReport.updateMmsReport(json,"4",messageId,split2[1],split2[2],split2[3],split2[4]);
        }

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsReport04,body:{},ipAddress:{},result:{},duration:{}",reports,ipAddress,result,duration);
        return result;
    }
    //物朗-电信2 模版审核回调
    @RequestMapping("model04")
    @ResponseBody
    public String model04(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        //TODO 逻辑完善
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report02Resp("1000","非json数据");
            }
            String msgID = jsonObject.containsKey("mms_id")?jsonObject.getString("mms_id"):"";
            String checkState = jsonObject.containsKey("status")?jsonObject.getString("status"):"";
            String reason = jsonObject.containsKey("content")?jsonObject.getString("content"):"审核未知";
            String date = jsonObject.containsKey("Date")?jsonObject.getString("Date"):new Date().toString();
            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MODEL_MT_CHANNEL+"4"+":"+msgID,msgID);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report01Resp("0","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);
            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String modelId = object.containsKey("modelId")?object.getString("modelId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            String pModelId=object.containsKey("pModelId")?object.getString("pModelId"):"";
            logger.info("companyId: {}, modelId: {}, id: {}",companyId,modelId,id);
            logger.info("modelReport05,msgID:{},checkState:{},reason:{},date:{}",msgID,checkState,reason,date);
            if("T".equals(checkState)){
                String s = RedisUtils.hash_get(RedisUtils.HASH_MODEL_RELATE_COUNT + pModelId + ":" + "4" + ":" + msgID, "total");
                if(StringUtils.isBlank(s)|| !"true".equals(s)){
                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",pModelId,4,msgID,500000,1,date,date);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

                    RedisUtils.hash_set(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"4"+":"+msgID,"total","true");
                    RedisUtils.hash_setExpire(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"4"+":"+msgID,3*24*3600);
                    logger.info("insertSql: {}",insertSql);
                }

                if(StringUtils.isBlank(reason)){
                    reason="审核成功";
                }
                //修改模版审核状态（新）
                String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);


//                String info="MsgId:<<"+msgID+">>"+"; "+"通道侧审核成功";
//                //审核通过后创建模版映射
//                //写入模版映射关系,默认500000日限  状态为停用
//                String s=modelId;
//                if(!StringUtils.isBlank(pid)){
//                    s=pid;
//                }
//                String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
//                        + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",s,5,msgID,500000,1,date,date);
//                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
//                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                logger.info("insertSql: {}",insertSql);
//
//                //修改模版审核状态
//                String updateSql = String.format("update e_model_info set status=%s,info='%s' where id=%s;",5,info,id);
//                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                logger.info("updateSql: {}",updateSql);
//
//                //回调客户
//                object.put("status",1);
//                object.put("info","审核成功");
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }else if("F".equals(checkState)){
                if(StringUtils.isBlank(reason)){
                    reason="审核失败";
                }
                //修改模版审核状态
                String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);


//                String info="MsgId:<<"+msgID+">>"+"; "+reason;
//                //修改模版审核状态
//                String updateSql = String.format("update e_model_info set status=%s,info='%s' where id=%s;",2,info,id);
//                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                logger.info("updateSql: {}",updateSql);
//
//                //回调客户
//                object.put("status",2);
//                object.put("info",reason);
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }
        }
        return ConstantsReport.report01Resp("0","成功");
    }
    //上海电信
    @RequestMapping("mt05")
    @ResponseBody
    public String mt05(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        logger.info("body: {}",body);
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
//        String result = backService.mt05(reports,"5",ipAddress);
        String result = ConstantsReport.report01Resp("0000","成功");
        String key="";
        if (StringUtils.isBlank(body)) {
            result= ConstantsReport.report01Resp("9999","内容为空");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport05,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            result= ConstantsReport.report01Resp("9998","参数异常，非json格式");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport05,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        String siId = bodyJson.containsKey("SiID")?bodyJson.getString("SiID"):"";
        String authenticator = bodyJson.containsKey("Authenticator")?bodyJson.getString("Authenticator"):"";
        String date = bodyJson.containsKey("Date")?bodyJson.getString("Date"):"";
        String method = bodyJson.containsKey("Method")?bodyJson.getString("Method"):"";
        String authenticator1 = MD5Utils.MD5Encode(siId+date+key).toUpperCase();
        logger.info("authenticator: {}",authenticator1);
//        if(!authenticator1.equals(authenticator)){
//            result= ConstantsReport.report01Resp("9998","参数异常，鉴权失败");
//            long duration = System.currentTimeMillis()-begin;
//            logger.info("mmsReport05,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
//            return result;
//        }
        JSONArray reportList = bodyJson.containsKey("ReportList")?bodyJson.getJSONArray("ReportList"):new JSONArray();
        logger.info("ReportList: {}",reportList.toJSONString());
        for (int i = 0; i < reportList.size(); i++) {
            JSONObject states = reportList.getJSONObject(i);
            String msgID = states.containsKey("MsgID")?states.getString("MsgID"):"";
            String phone = states.containsKey("Phone")?states.getString("Phone"):"";
            String state = states.containsKey("State")?states.getString("State"):"-1";
            String messageId = states.containsKey("TransID")?states.getString("TransID"):"";
            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+"5"+":"+messageId,phone);
            if(StringUtils.isBlank(jsonStr)){
                result= ConstantsReport.report01Resp("9997","重复推送或无对应数据");
            }
            JSONObject json = JSONObject.parseObject(jsonStr);
            ConstantsReport.updateMmsReport(json,"5",messageId,phone,state,state,"");
        }

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsReport05,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }
    //上海电信模版审核回调
    @RequestMapping("model05")
    @ResponseBody
    public String model05(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        //TODO 逻辑完善
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report02Resp("1000","非json数据");
            }
            String msgID = jsonObject.containsKey("MsgID")?jsonObject.getString("MsgID"):"";
            String checkState = jsonObject.containsKey("CheckState")?jsonObject.getString("CheckState"):"";
            String reason = jsonObject.containsKey("Reason")?jsonObject.getString("Reason"):"审核未知";
            String date = jsonObject.containsKey("Date")?jsonObject.getString("Date"):new Date().toString();
            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MODEL_MT_CHANNEL+"5"+":"+msgID,msgID);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report02Resp("1000","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);
            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String modelId = object.containsKey("modelId")?object.getString("modelId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            String pModelId=object.containsKey("pModelId")?object.getString("pModelId"):"";
            logger.info("companyId: {}, modelId: {}, id: {}",companyId,modelId,id);
            logger.info("modelReport05,msgID:{},checkState:{},reason:{},date:{}",msgID,checkState,reason,date);
            if("10".equals(checkState)){
                //TODO 模版状态回调（过渡期）
//                if(modelId.length()<=11){
                String s = RedisUtils.hash_get(RedisUtils.HASH_MODEL_RELATE_COUNT + pModelId + ":" + "5" + ":" + msgID, "total");
                if(StringUtils.isBlank(s)|| !"true".equals(s)){
                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",pModelId,5,msgID,500000,1,date,date);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

                    RedisUtils.hash_set(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"5"+":"+msgID,"total","true");
                    RedisUtils.hash_setExpire(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"5"+":"+msgID,3*24*3600);
                    logger.info("insertSql: {}",insertSql);
                }

                    if(StringUtils.isBlank(reason)){
                        reason="审核成功";
                    }
                    //修改模版审核状态
                    String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                    logger.info("updateSql: {}",updateSql);
//                }else {
//                    String info="MsgId:<<"+msgID+">>"+"; "+"通道侧审核成功";
//                    //审核通过后创建模版映射
//                    //写入模版映射关系,默认500000日限  状态为停用
//                    String s=modelId;
//                    if(!StringUtils.isBlank(pid)){
//                        s=pid;
//                    }
//                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
//                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",s,5,msgID,500000,1,date,date);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("insertSql: {}",insertSql);
//
//                    //修改模版审核状态
//                    String updateSql = String.format("update e_model_info set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("updateSql: {}",updateSql);
//
//                    //回调客户
//                    object.put("status",1);
//                    object.put("info","审核成功");
//                    RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
//                }

            }else if("0".equals(checkState)){
                //TODO 模版状态回调（过渡期）
//                if(modelId.length()<=11){
                    if(StringUtils.isBlank(reason)){
                        reason="审核失败";
                    }
                    //修改模版审核状态
                    String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                    logger.info("updateSql: {}",updateSql);
//                }else {
//                    //修改模版审核状态
//                    String updateSql = String.format("update e_model_info set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("updateSql: {}",updateSql);
//
//                    ////TODO 定时任务或人工处理 回调客户
//                    object.put("status",2);
//                    object.put("info",reason);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
//                }
            }
        }
        return ConstantsReport.report02Resp("1000","成功");
    }
    //北京移动3878--02
    @RequestMapping("mt06")
    @ResponseBody
    public String mt06(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result=ConstantsReport.report01Resp("0000","成功");
        //String result = backService.mt01(body,"2",ipAddress);

        //请求内容{"mobile":"15811047193","messageId":"20220816_1659346550002","type":0,"notifyUrl":"http://103.29.16.3:9130/back/mt01","status":"0","msg":"DELIVRD","bizCode":"2384075185690528863"}
        if (StringUtils.isBlank(body)) {
            result= ConstantsReport.report01Resp("9999","内容为空");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport06,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            result= ConstantsReport.report01Resp("9998","参数异常，非json格式");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport02,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
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

        String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+"6"+":"+messageId,mobile);
        if(StringUtils.isBlank(jsonStr)){
            result= ConstantsReport.report01Resp("9997","重复推送或无对应数据");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport02,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject json = JSONObject.parseObject(jsonStr);
        ConstantsReport.updateMmsReport(json,"6",messageId,mobile,status,msg,bizcode);

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsReport06,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }

    @RequestMapping("model06")
    @ResponseBody
    public String model06(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report01Resp("0","非json数据");
            }
            String msgID = jsonObject.containsKey("mmsId")?jsonObject.getString("mmsId"):"";
            String status = jsonObject.containsKey("status")?jsonObject.getString("status"):"";
            String reason = jsonObject.containsKey("msg")?jsonObject.getString("msg"):"审核未知";
            String type = jsonObject.containsKey("type")?jsonObject.getString("type"):"10086";
            String notifyUrl = jsonObject.containsKey("notifyUrl")?jsonObject.getString("notifyUrl"):"";
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(new Date());

            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MODEL_MT_CHANNEL+"6"+":"+msgID,msgID);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report01Resp("0","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);
            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String modelId = object.containsKey("modelId")?object.getString("modelId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            String pModelId=object.containsKey("pModelId")?object.getString("pModelId"):"";
            logger.info("companyId: {}, modelId: {}, id: {}",companyId,modelId,id);
            logger.info("modelReport01,msgID:{},status:{},reason:{},type:{}",msgID,status,reason,type);
            if("1".equals(status)){

                //TODO 模版状态回调（过渡期）
//                if(modelId.length()<=11){
                    //审核通过后创建模版映射
                    //写入模版映射关系,默认500000日限  状态为停用

                String s = RedisUtils.hash_get(RedisUtils.HASH_MODEL_RELATE_COUNT + pModelId + ":" + "6" + ":" + msgID, "total");
                if(StringUtils.isBlank(s)|| !"true".equals(s)){
                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",pModelId,6,msgID,500000,1,date,date);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

                    RedisUtils.hash_set(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"6"+":"+msgID,"total","true");
                    RedisUtils.hash_setExpire(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"6"+":"+msgID,3*24*3600);
                    logger.info("insertSql: {}",insertSql);
                }

                    if(StringUtils.isBlank(reason)){
                        reason="审核成功";
                    }
                    //修改模版审核状态（新）
                    String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                    logger.info("updateSql: {}",updateSql);
//                }else {
//                    String info="MsgId:<<"+msgID+">>"+"; "+"通道侧审核成功";
//                    //审核通过后创建模版映射
//                    //写入模版映射关系,默认500000日限  状态为停用
//                    String s=modelId;
//                    if(!StringUtils.isBlank(pid)){
//                        s=pid;
//                    }
//                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
//                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",s,6,msgID,500000,1,date,date);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("insertSql: {}",insertSql);
//                    //修改模版审核状态
//                    String updateSql = String.format("update e_model_info set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,info,msgID,id);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("updateSql: {}",updateSql);
//
//
//                    //TODO 定时任务或人工处理 回调客户
//                    object.put("status",1);
//                    object.put("info","审核成功");
//                    RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
//                }

            }else if("0".equals(status)){
                //TODO 模版状态回调（过渡期）
//                if(modelId.length()<=11){
                    if(StringUtils.isBlank(reason)){
                        reason="审核失败";
                    }
                    //修改模版审核状态
                    String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                    logger.info("updateSql: {}",updateSql);
//                }else {
//                    //修改模版审核状态
//                    String updateSql = String.format("update e_model_info set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("updateSql: {}",updateSql);
//
//                    ////TODO 定时任务或人工处理 回调客户
//                    object.put("status",2);
//                    object.put("info",reason);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
//                }

            }
        }
        return ConstantsReport.report01Resp("0","成功");
    }

    @RequestMapping("sign06")
    @ResponseBody
    public String sign06(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report01Resp("0","非json数据");
            }
            String signId = jsonObject.containsKey("signId")?jsonObject.getString("signId"):"";
            String status = jsonObject.containsKey("status")?jsonObject.getString("status"):"";
            String reason = jsonObject.containsKey("msg")?jsonObject.getString("msg"):"审核未知";
            String serviceCode = jsonObject.containsKey("serviceCode")?jsonObject.getString("serviceCode"):"";
            String customerId = jsonObject.containsKey("customerId")?jsonObject.getString("customerId"):"";
            String reportSignContent = jsonObject.containsKey("reportSignContent")?jsonObject.getString("reportSignContent"):"";
            String auditTime = jsonObject.containsKey("auditTime")?jsonObject.getString("auditTime"):"";

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(new Date());

            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_SIGN_MT_CHANNEL+"2"+":"+signId,signId);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report01Resp("0","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);

            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String pSignId = object.containsKey("pSignId")?object.getString("pSignId"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            logger.info("companyId: {}, signId: {}, id: {}",companyId,signId,id);
            logger.info("signReport01,signId:{},status:{},msg:{},serviceCode:{},customerId:{},reportSignContent:{},auditTime:{}",signId,status,reason,serviceCode,customerId,reportSignContent,auditTime);
            if("1".equals(status)){
                if(!StringUtils.isBlank(pSignId)){
                    String s = RedisUtils.hash_get(RedisUtils.HASH_SIGN_RELATE_COUNT + pSignId + ":" + "6" + ":" + signId, "total");
                    if(StringUtils.isBlank(s)|| !"true".equals(s)){
                        //审核通过后创建签名映射
                        String insertSql = String.format(" INSERT INTO e_sign_related(sign_id,channel_id,channel_sign_id,status,update_time,create_time) "
                                + "VALUES('%s',%s,'%s',%s,'%s','%s');",pSignId,6,signId,1,auditTime,auditTime);
                        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                        RedisUtils.hash_set(RedisUtils.HASH_SIGN_RELATE_COUNT+pSignId+":"+"6"+":"+signId,"total","true");
                        RedisUtils.hash_setExpire(RedisUtils.HASH_SIGN_RELATE_COUNT+pSignId+":"+"6"+":"+signId,7*24*3600);
                        logger.info("insertSql: {}",insertSql);
                    }
                }


                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",5,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);

                //TODO 定时任务或人工处理 回调客户
//                object.put("status",1);
//                object.put("info","审核成功");
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }else if("2".equals(status)){
                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",6,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);

//                //TODO 定时任务或人工处理 回调客户
//                object.put("status",2);
//                object.put("info",reason);
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }else if("100".equals(status)){
                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",1,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);
            }else if("000".equals(status)){
                //修改模版审核状态
                String updateSql = String.format("update e_model_sign set status=%s,info='%s' where id=%s;",2,reason,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);
            }
        }
        return ConstantsReport.report01Resp("0","成功");
    }

    //修治上海电信
    @RequestMapping("mt07")
    @ResponseBody
    public String mt07(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        logger.info("body: {}",body);
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
//        String result = backService.mt05(reports,"5",ipAddress);
        String result = ConstantsReport.report01Resp("0000","成功");
        String key="";
        if (StringUtils.isBlank(body)) {
            result= ConstantsReport.report01Resp("9999","内容为空");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport07,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject bodyJson=null;
        try {
            bodyJson = JSONObject.parseObject(body);
        }catch (Exception e){
            result= ConstantsReport.report01Resp("9998","参数异常，非json格式");
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport07,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        String siId = bodyJson.containsKey("SiID")?bodyJson.getString("SiID"):"";
        String authenticator = bodyJson.containsKey("Authenticator")?bodyJson.getString("Authenticator"):"";
        String date = bodyJson.containsKey("Date")?bodyJson.getString("Date"):"";
        String method = bodyJson.containsKey("Method")?bodyJson.getString("Method"):"";
        String authenticator1 = MD5Utils.MD5Encode(siId+date+key).toUpperCase();
        logger.info("authenticator: {}",authenticator1);
//        if(!authenticator1.equals(authenticator)){
//            result= ConstantsReport.report01Resp("9998","参数异常，鉴权失败");
//            long duration = System.currentTimeMillis()-begin;
//            logger.info("mmsReport05,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
//            return result;
//        }
        JSONArray reportList = bodyJson.containsKey("ReportList")?bodyJson.getJSONArray("ReportList"):new JSONArray();
        logger.info("ReportList: {}",reportList.toJSONString());
        for (int i = 0; i < reportList.size(); i++) {
            JSONObject states = reportList.getJSONObject(i);
            String msgID = states.containsKey("MsgID")?states.getString("MsgID"):"";
            String phone = states.containsKey("Phone")?states.getString("Phone"):"";
            String state = states.containsKey("State")?states.getString("State"):"-1";
            String messageId = states.containsKey("TransID")?states.getString("TransID"):"";
            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+"7"+":"+messageId,phone);
            if(StringUtils.isBlank(jsonStr)){
                result= ConstantsReport.report01Resp("9997","重复推送或无对应数据");
            }
            JSONObject json = JSONObject.parseObject(jsonStr);
            ConstantsReport.updateMmsReport(json,"7",messageId,phone,state,state,"");
        }

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsReport07,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }
    //修治上海电信模版审核回调
    @RequestMapping("model07")
    @ResponseBody
    public String model07(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        //TODO 逻辑完善
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report02Resp("1000","非json数据");
            }
            String msgID = jsonObject.containsKey("MsgID")?jsonObject.getString("MsgID"):"";
            String checkState = jsonObject.containsKey("CheckState")?jsonObject.getString("CheckState"):"";
            String reason = jsonObject.containsKey("Reason")?jsonObject.getString("Reason"):"审核未知";
            String date = jsonObject.containsKey("Date")?jsonObject.getString("Date"):new Date().toString();
            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MODEL_MT_CHANNEL+"7"+":"+msgID,msgID);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report02Resp("1000","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);
            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String modelId = object.containsKey("modelId")?object.getString("modelId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            String pModelId=object.containsKey("pModelId")?object.getString("pModelId"):"";
            logger.info("companyId: {}, modelId: {}, id: {}",companyId,modelId,id);
            logger.info("modelReport07,msgID:{},checkState:{},reason:{},date:{}",msgID,checkState,reason,date);
            if("10".equals(checkState)){
                //TODO 模版状态回调（过渡期）
//                if(modelId.length()<=11){
                String s = RedisUtils.hash_get(RedisUtils.HASH_MODEL_RELATE_COUNT + pModelId + ":" + "7" + ":" + msgID, "total");
                if(StringUtils.isBlank(s)|| !"true".equals(s)){
                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",pModelId,7,msgID,500000,1,date,date);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

                    RedisUtils.hash_set(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"7"+":"+msgID,"total","true");
                    RedisUtils.hash_setExpire(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"7"+":"+msgID,3*24*3600);
                    logger.info("insertSql: {}",insertSql);
                }

                    if(StringUtils.isBlank(reason)){
                        reason="审核成功";
                    }
                    //修改模版审核状态
                    String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                    logger.info("updateSql: {}",updateSql);
//                }else {
//                    String info="MsgId:<<"+msgID+">>"+"; "+"通道侧审核成功";
//                    //审核通过后创建模版映射
//                    //写入模版映射关系,默认500000日限  状态为停用
//                    String s=modelId;
//                    if(!StringUtils.isBlank(pid)){
//                        s=pid;
//                    }
//                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
//                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",s,7,msgID,500000,1,date,date);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("insertSql: {}",insertSql);
//
//                    //修改模版审核状态
//                    String updateSql = String.format("update e_model_info set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("updateSql: {}",updateSql);
//
//                    //回调客户
//                    object.put("status",1);
//                    object.put("info","审核成功");
//                    RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
//                }

            }else if("0".equals(checkState)){
                //TODO 模版状态回调（过渡期）
//                if(modelId.length()<=11){
                    if(StringUtils.isBlank(reason)){
                        reason="审核失败";
                    }
                    //修改模版审核状态
                    String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                    logger.info("updateSql: {}",updateSql);
//                }else {
//                    //修改模版审核状态
//                    String updateSql = String.format("update e_model_info set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                    logger.info("updateSql: {}",updateSql);
//
//                    ////TODO 定时任务或人工处理 回调客户
//                    object.put("status",2);
//                    object.put("info",reason);
//                    RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                    RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
//                }
            }
        }
        return ConstantsReport.report02Resp("1000","成功");
    }

    //物朗2
    @RequestMapping("mt08")
    @ResponseBody
    public String mt08(HttpServletRequest request, HttpServletResponse response,@RequestBody String reports) {
        logger.info("reports: {}",reports);
        long begin = System.currentTimeMillis();
        String ipAddress = PublicUtil.getClientIp(request);
        String result=ConstantsReport.report01Resp("0000","成功");
        //String result = backService.mt04(reports,"4",ipAddress);
        /*
        reports=180828100924138386,13912345678,0,DELIVRD,00000020140805135416
        ,20181125112640;180828100924138386,13912345678,0,DELIVRD,
        00000020140805135416,20181125112640
        状态报告是批量返回,直接获取 out=后面的字符串即可，多条状态报告用英文
        分号分隔
         */
        if (StringUtils.isBlank(reports)) {
            long duration = System.currentTimeMillis()-begin;
            result= ConstantsReport.report01Resp("9999","内容为空");
            logger.info("mmsReport04,body:{},ipAddress:{},result:{},duration:{}",reports,ipAddress,result,duration);
            return result;
        }
        try {
            reports = URLDecoder.decode(reports, "utf-8");
        }catch (Exception e){
            logger.info("转码错误");
        }
        logger.info("reports: {}",reports);
        String[] split1 = reports.split(";");
        logger.info("split: {}",split1.length);
        for (int i = 0; i < split1.length; i++) {
            String[] split2 = split1[i].split(",");
            logger.info("split"+(i+1)+": "+split1.length);
            logger.info("split[0]:{}",split2[0]);
            String messageId = split2[0].split("=").length>1?split2[0].split("=")[1]:split2[0];
            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+"8"+":"+messageId,split2[1]);
            if(StringUtils.isBlank(jsonStr)){
                long duration = System.currentTimeMillis()-begin;
                result= ConstantsReport.report01Resp("9997","重复推送或无对应数据");
                logger.info("mmsReport08,body:{},ipAddress:{},result:{},duration:{}",reports,ipAddress,result,duration);
                return result;
            }
            JSONObject json = JSONObject.parseObject(jsonStr);

            ConstantsReport.updateMmsReport(json,"8",messageId,split2[1],split2[2],split2[3],split2[4]);
        }

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsReport08,body:{},ipAddress:{},result:{},duration:{}",reports,ipAddress,result,duration);
        return result;
    }

    //物朗-电信2 模版审核回调
    @RequestMapping("model08")
    @ResponseBody
    public String model08(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        JSONObject jsonObject=new JSONObject();
        //TODO 逻辑完善
        if(!StringUtils.isBlank(body)){
            try {
                jsonObject=JSONObject.parseObject(body);
            }catch (Exception e){
                return ConstantsReport.report02Resp("1000","非json数据");
            }
            String msgID = jsonObject.containsKey("mms_id")?jsonObject.getString("mms_id"):"";
            String checkState = jsonObject.containsKey("status")?jsonObject.getString("status"):"";
            String reason = jsonObject.containsKey("content")?jsonObject.getString("content"):"审核未知";
            String date = jsonObject.containsKey("Date")?jsonObject.getString("Date"):new Date().toString();
            String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MODEL_MT_CHANNEL+"8"+":"+msgID,msgID);
            if(StringUtils.isBlank(jsonStr)){
                logger.info("模版映射为空");
                return ConstantsReport.report01Resp("0","失败");
            }
            JSONObject object = JSONObject.parseObject(jsonStr);
            String companyId = object.containsKey("companyId")?object.getString("companyId"):"";
            String modelId = object.containsKey("modelId")?object.getString("modelId"):"";
            String pid = object.containsKey("pid")?object.getString("pid"):"";
            String id = object.containsKey("id")?object.getString("id"):"";
            String pModelId=object.containsKey("pModelId")?object.getString("pModelId"):"";
            logger.info("companyId: {}, modelId: {}, id: {}",companyId,modelId,id);
            logger.info("modelReport08,msgID:{},checkState:{},reason:{},date:{}",msgID,checkState,reason,date);
            if("T".equals(checkState)){
                String s = RedisUtils.hash_get(RedisUtils.HASH_MODEL_RELATE_COUNT + pModelId + ":" + "8" + ":" + msgID, "total");
                if(StringUtils.isBlank(s)|| !"true".equals(s)){
                    String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
                            + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",pModelId,8,msgID,500000,1,date,date);
                    RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
                    RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

                    RedisUtils.hash_set(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"8"+":"+msgID,"total","true");
                    RedisUtils.hash_setExpire(RedisUtils.HASH_MODEL_RELATE_COUNT+pModelId+":"+"8"+":"+msgID,3*24*3600);
                    logger.info("insertSql: {}",insertSql);
                }

                if(StringUtils.isBlank(reason)){
                    reason="审核成功";
                }
                //修改模版审核状态（新）
                String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",1,reason,msgID,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);


//                String info="MsgId:<<"+msgID+">>"+"; "+"通道侧审核成功";
//                //审核通过后创建模版映射
//                //写入模版映射关系,默认500000日限  状态为停用
//                String s=modelId;
//                if(!StringUtils.isBlank(pid)){
//                    s=pid;
//                }
//                String insertSql = String.format(" INSERT INTO e_model_related(model_id,channel_id,channel_model_id,limit_count,status,update_time,create_time) "
//                        + "VALUES('%s',%s,'%s',%s,%s,'%s','%s');",s,5,msgID,500000,1,date,date);
//                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,insertSql);
//                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                logger.info("insertSql: {}",insertSql);
//
//                //修改模版审核状态
//                String updateSql = String.format("update e_model_info set status=%s,info='%s' where id=%s;",5,info,id);
//                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                logger.info("updateSql: {}",updateSql);
//
//                //回调客户
//                object.put("status",1);
//                object.put("info","审核成功");
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }else if("F".equals(checkState)){
                if(StringUtils.isBlank(reason)){
                    reason="审核失败";
                }
                //修改模版审核状态
                String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",2,reason,msgID,id);
                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
                logger.info("updateSql: {}",updateSql);
//                String info="MsgId:<<"+msgID+">>"+"; "+reason;
//                //修改模版审核状态
//                String updateSql = String.format("update e_model_info set status=%s,info='%s' where id=%s;",2,info,id);
//                RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//                RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//                logger.info("updateSql: {}",updateSql);
//
//                //回调客户
//                object.put("status",2);
//                object.put("info",reason);
//                RedisUtils.fifo_push(RedisUtils.FIFO_MODEL_MT_CLIENT+companyId,object.toJSONString());
//                RedisUtils.hash_incrBy(RedisUtils.HASH_MODEL_MT_COUNT, companyId+"", 1);
            }
        }
        return ConstantsReport.report01Resp("0","成功");
    }

    //单元科技
    @RequestMapping("mt09")
    @ResponseBody
    public String mt09(HttpServletRequest request, HttpServletResponse response,@RequestBody String body) {
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result= "0";
        //String result = backService.mt01(body,"1",ipAddress);

        //请求内容{"mobile":"15811047193","messageId":"20220816_1659346550002","type":0,"notifyUrl":"http://103.29.16.3:9130/back/mt01","status":"0","msg":"DELIVRD","bizCode":"2384075185690528863"}
        if (StringUtils.isBlank(body)) {
            //"内容为空"
            result= "1";
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport09,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject bodyJson=null;
        try {
            //ChnlNo=C0190&IPass=123456&IUser=test12&Mobile=18756232770&MsgId=20230210497027431001&RptTime=20230210121135165&Stat=DELIVRD
            String decode = URLDecoder.decode(body, "utf-8");
            String[] split = decode.split("&");
            bodyJson=new JSONObject();
            for (int i = 0; i < split.length; i++) {
                String[] split1 = split[i].split("=");
                bodyJson.put(split1[0],split1[1]);
            }

        }catch (Exception e){
            //参数异常，
            result= "2";
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport09,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        String mobile = bodyJson.containsKey("Mobile")?bodyJson.getString("Mobile"):"";
        String messageId = bodyJson.containsKey("MsgId")?bodyJson.getString("MsgId"):"";
        //下发状态：0-成功，其他-失败(对应msg描述)
        String status = bodyJson.containsKey("Stat")?bodyJson.getString("Stat"):"-1";
        String msg = bodyJson.containsKey("msg")?bodyJson.getString("msg"):status;
        //平台唯一ID
        String bizcode = bodyJson.containsKey("bizcode")?bodyJson.getString("bizcode"):"";

        String jsonStr =  RedisUtils.hash_get(RedisUtils.HASH_MMS_MT_CHANNEL+"9"+":"+messageId,mobile);
        if(StringUtils.isBlank(jsonStr)){
            //重复推送或无对应数据
            result= "3";
            long duration = System.currentTimeMillis()-begin;
            logger.info("mmsReport09,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
            return result;
        }
        JSONObject json = JSONObject.parseObject(jsonStr);
        ConstantsReport.updateMmsReport(json,"9",messageId,mobile,status,msg,bizcode);

        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsReport09,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }
    @RequestMapping("mo03")
    @ResponseBody
    public String mo03(HttpServletRequest request, HttpServletResponse response,@RequestBody String body){
        long begin = System.currentTimeMillis();

        String ipAddress = PublicUtil.getClientIp(request);
        String result = backService.mo03(body,"3",ipAddress);
        long duration = System.currentTimeMillis()-begin;
        logger.info("mmsReport03,body:{},ipAddress:{},result:{},duration:{}",body,ipAddress,result,duration);
        return result;
    }
}
