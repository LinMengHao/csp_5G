package com.xzkj.taskChannel.sms.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskChannel.redis.RedisUtils;
import com.xzkj.taskChannel.util.Base64Util;
import com.xzkj.taskChannel.util.HttpInterface;
import com.xzkj.taskChannel.util.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HandlerShanghaiDianXin implements Runnable{
    public static Logger logger = LoggerFactory.getLogger("HandlerShanghaiDianXin");

    private int linkSpeed=0;
    private JSONObject jsonChannel=null;
    private String channelId=null;
    private String channelMmsId=null;

    public
    HandlerShanghaiDianXin(int linkSpeed,String channelId,String channelMmsId,JSONObject jsonChannel){
        this.linkSpeed=linkSpeed;
        this.jsonChannel=jsonChannel;
        this.channelId=channelId;
        this.channelMmsId=channelMmsId;
    }
    public void run(){
        long bt = System.currentTimeMillis();
        int size=0;
        try {
            //发送列表
            List<JSONObject> orderList = new Vector<JSONObject>();
            //渠道列表key
            String channel_key = RedisUtils.FIFO_CHANNEL_REQUEST+channelId+":"+channelMmsId;
            String jsonStr = null;
            while((jsonStr = RedisUtils.fifo_pop(channel_key))!=null){
                if(StringUtils.isBlank(jsonStr)){
                    continue;
                }
                JSONObject json = JSONObject.parseObject(jsonStr);

                int count = send(json);//提交
                size+=count;
                if(size>=linkSpeed){
                    break;
                }
            }
            RedisUtils.hash_incrBy(RedisUtils.HASH_CHANNEL_REQ_COUNT+channelId, channelMmsId, 0-size);


        } catch (Exception e) {
            logger.error("视频短信渠道发送失败key：{},channelNo:{},ex:{}","notice",channelId,e.getMessage());
            e.printStackTrace();
        }
        if(size > 0){//控制提交速度
            long ut = System.currentTimeMillis() - bt;
            int sleepTime = 1000 / linkSpeed * size;
            logger.info("视频短信速度控制：channelId:{}-sleepTime:{}-ut:{}-size:{}-linkSpeed:{}-bt:{}",channelId,sleepTime,ut,size,linkSpeed,bt);
            //ut=11-84秒 sleepTime=1秒
            if (ut < sleepTime) {
                try {
                    Thread.sleep(sleepTime - ut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //处理时长计算
        //long ut = System.currentTimeMillis() - bt;
        //logger.error("时长统计：seqNo={}，itemNum={}，time={}，concurrentSize={}",seqNo,itemNum,ut,concurrentSize);
        RedisUtils.hash_incrBy(RedisUtils.HASH_CHANNEL_THREAD_COUNT,channelId,-1);
        return;
    }

    public int send(JSONObject json) {
        try {
            String siId="22100670010006";
            String key="ViEW4aoQNU";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(new Date());
            String authenticator = MD5Utils.MD5Encode(siId+date+key).toUpperCase();
            String url = "http://124.126.120.102:8896/sapi/send";


            String mobile=json.getString("mobile");//手机号
            String[] phones={mobile};
            String linkId=json.getString("linkId");//客户唯一id
            //String param=json.getString("param");//变量模板参数
            String channelParam=json.getString("channelParam");//通道变量模板参数
            int sendType = json.getInteger("sendType");//1-普通视频短信发送，2-变量视频短信发送

            JSONObject submitJson = new JSONObject();
            submitJson.put("Phones",phones);
            submitJson.put("SiID",siId);
            submitJson.put("Authenticator",authenticator);
            submitJson.put("Date",date);
            submitJson.put("Method","send");
            submitJson.put("MsgID",channelMmsId);
//            JSONArray content=new JSONArray();
            List<JSONObject>list=new ArrayList<>();
            if(sendType==2){
                //获取变量所在帧的位置
                String frame=RedisUtils.string_get(RedisUtils.HASH_MODEL_VARS_CHANNEL+channelId+":"+channelMmsId);
                url="http://124.126.120.102:8896/sapi/option";
                submitJson.put("Method","option");
                String[] split = channelParam.split(",");
                JSONObject jsonObject=new JSONObject();
                JSONObject jsonObject1=new JSONObject();
                //根据模版id查找素材，查看文本素材的帧数和排序
                jsonObject.put("Frame",frame);
                for (int i = 1; i <= split.length; i++) {
                    String[] value=split[i-1].split("=");
                    logger.info("参数"+i+"： {}",value[1]);

                    jsonObject1.put(value[0],value[1]);
                }
                jsonObject.put("Param",jsonObject1);
//                content.add(jsonObject);
                list.add(jsonObject);
            }
            submitJson.put("Content",list);
//            submitJson.put("content",list);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            JSONObject result=null;
            try {
                HttpPost httppost = new HttpPost(url);
                httppost.setConfig(RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build());
                httppost.setHeader("Content-Type","application/json");
                StringEntity reqEntity = new StringEntity(submitJson.toString(),"utf-8");
                httppost.setEntity(reqEntity);
                System.err.println("executing request " + httppost.getRequestLine());

                CloseableHttpResponse response = httpclient.execute(httppost);
                System.out.println("response:"+response);
                try {
                    System.out.println(response.getStatusLine());
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        String responseEntityStr = EntityUtils.toString(response.getEntity());
                        System.out.println("响应参数：" + responseEntityStr);
                        System.out.println("Response content length: " + resEntity.getContentLength());
                        result=JSONObject.parseObject(responseEntityStr);
                    }
                    EntityUtils.consume(resEntity);
                } finally {
                    response.close();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


//            Hashtable<String, String> formHeaders = new Hashtable<String, String>();
//            formHeaders.put("Content-Type", "application/json;charset=utf-8");
//            String str = HttpInterface.httpClientPost(url, submitJson.toJSONString(), 30000, "UTF-8", formHeaders);
			/*if(str.indexOf("http请求异常")==0){
				str = HttpInterface.httpClientPost(url, submitJson.toJSONString(), 300000, "UTF-8", formHeaders);
			}*/
            logger.info("视频短信上海电信提交结果：{}-{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,mobile,submitJson.toJSONString(),result.toJSONString());
            String code="";
            String msg="提交通道失败";
            String channneMsgId="";
            if(!"1000".equals(result.getString("ResCode"))){
                code="-1";
                msg=result.getString("ResMsg");
                channneMsgId=linkId;
                logger.info("视频短信上海电信提交结果超时：{}-{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,mobile,submitJson.toJSONString(),result.toJSONString());
            }else{
                code = result.getString("ResCode");
                msg = result.getString("ResMsg");
                channneMsgId = result.getString("TransID");
            }
            json.put("channneMsgId",channneMsgId);
            json.put("info",msg);
            //提交成功
            if(code.equals("1000")){
                insertMT(json,channelId,channneMsgId,msg);
            }else{
                insertFailMT(json,channelId,"mk:0010",msg);
            }
            return 1;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 提交入库
     */
    private void insertMT(JSONObject json,String channelId,String channneMsgId,String info){
        String linkId=json.getString("linkId");//订单唯一id
        String tableName=json.getString("tableName");
        String companyId=json.getString("companyId");
        String appName=json.getString("appName");
        //String appId=json.getString("appId");

        String status="submit";
        String mobile=json.getString("mobile");//手机号

        //客户消费数加一
        RedisUtils.hash_incrBy(RedisUtils.HASH_ACC_SEND, appName,1);
        //添加至回调返回状态
        RedisUtils.hash_set(RedisUtils.HASH_MMS_MT_CHANNEL+channelId+":"+channneMsgId,mobile,json.toJSONString());
        //数据库状态同步更新
        String updateSql = String.format("update %s set status='%s',data_status='%s',info='%s',channel_msg_id='%s' where link_id='%s';",
                tableName, status, "submited",info, channneMsgId,linkId);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
    }

    private void insertFailMT(JSONObject json,String channelId,String status,String info) {
        String linkId=json.getString("linkId");//订单唯一id
        String tableName=json.getString("tableName");
        String companyId=json.getString("companyId");

        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String reportTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        //提交失败数据入库
        String updateSql = String.format("update %s set status='%s',info='%s',report_time='%s' where link_id='%s';",
                tableName,status,info,nowTime,linkId);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        //失败回调客户
        json.put("reportStatus",status);
        json.put("reportTime",reportTime);
        RedisUtils.fifo_push(RedisUtils.FIFO_MMS_MT_CLIENT+companyId,json.toJSONString());
        RedisUtils.hash_incrBy(RedisUtils.HASH_MMS_MT_COUNT, companyId+"", 1);
    }

    private synchronized String getToken(String apiKey,String apiSecrect,String channelId) {
        String token=RedisUtils.string_get(RedisUtils.STR_CHANNEL_TOKEN+channelId);
        if(StringUtils.isNotBlank(token)){
            return token;
        }

        String url = "http://36.138.133.214:30080/api/user/token";
        long reqTime=System.currentTimeMillis();
        String signStr="apiKey="+apiKey+"&apiSecrect="+apiSecrect+"&reqTime="+reqTime;
        String sign= Base64Util.encodeBASE64(Base64Util.getSHA256(signStr));
        JSONObject json = new JSONObject();
        json.put("apiKey",apiKey);
        json.put("apiSecrect",apiSecrect);
        json.put("reqTime",reqTime);
        json.put("sign",sign);

        String str = HttpInterface.httpClientPostBody(url, json.toJSONString(), 30000, "UTF-8");
        if(str.indexOf("http请求异常")==0){
            str = HttpInterface.httpClientPostBody(url, json.toJSONString(), 30000, "UTF-8");
        }
        System.out.println("-----------------getToken-------------"+str);
        if(str.indexOf("http请求异常")==0){
            token="";
        }else{
            JSONObject result = JSONObject.parseObject(str);
            String code = result.getString("code");
            if(code.equals("0")){
                token=result.getString("token");
                RedisUtils.string_set(RedisUtils.STR_CHANNEL_TOKEN+channelId,24*3600,token);
            }else{
                token="";
            }
        }
        return token;
    }
}
