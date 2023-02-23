package com.xzkj.taskPorxy.sms.handler.models.query;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.util.Base64Util;
import com.xzkj.taskPorxy.util.HttpInterface;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HandlerModelQueryToLiDinYD implements Runnable {
	public static Logger logger = LoggerFactory.getLogger("HandlerModelQueryToLiDinYD");

	private String companyId;
	private String appId;

	private String channelId;
	private int concurrentSize=0;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");//年月日

	public HandlerModelQueryToLiDinYD(String companyId, String appId, String channelId, int curCount) {
		this.companyId=companyId;
		this.appId=appId;
		this.concurrentSize=curCount;
		this.channelId=channelId;
	}
	public void run(){
		//long bt = System.currentTimeMillis();
		try {
			//队列key
			String fifo_key = RedisUtils.FIFO_QUERY_MODEL_LIST+companyId+":"+appId+":"+channelId;
			String jsonStr = null;
			int count=0;
			while((jsonStr = RedisUtils.fifo_pop(fifo_key))!=null){
				JSONObject modelInfo = JSONObject.parseObject(jsonStr);
				logger.info("模版查询数据：{}",modelInfo);


				String channelModelId = modelInfo.containsKey("channelModelId")?modelInfo.getString("channelModelId"):"";

				long reqTime=System.currentTimeMillis();
				String id = modelInfo.containsKey("id")?modelInfo.getString("id"):"";
				String pModelId = modelInfo.containsKey("pModelId")?modelInfo.getString("pModelId"):"";
				String s="mmsId="+channelModelId+"&reqtime="+reqTime;
				String sign= Base64Util.encodeBASE64(Base64Util.getSHA256(s));

				MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

				CloseableHttpClient httpclient = HttpClients.createDefault();
				JSONObject result=null;
				try{
					String apiKey="XIUZHIBJ01";
					String apiSecrect="x1&@9%&6";
					String url = "http://36.138.133.214:30080/api/mms/selectMmsStatus";

					String token=RedisUtils.string_get(RedisUtils.STR_CHANNEL_TOKEN+channelId);
					if(StringUtils.isBlank(token)){
						token=getToken(apiKey,apiSecrect,channelId);
					}


					HttpPost httppost = new HttpPost(url);
//					httppost.setHeader("Content-Type", "multipart/form-data;charset=utf-8");
					httppost.setHeader("Authorization", token);
					httppost.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

					httppost.setConfig(RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build());
					StringBody mmsIdBody = new StringBody(channelModelId, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("mmsId",mmsIdBody).setCharset(Charset.forName("utf-8"));
					StringBody reqTimeBody = new StringBody(String.valueOf(reqTime), ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("reqtime",reqTimeBody).setCharset(Charset.forName("utf-8"));
					StringBody signBody = new StringBody(sign, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("sign",signBody).setCharset(Charset.forName("utf-8"));
					HttpEntity entity=multipartEntityBuilder.build();

					httppost.setEntity(entity);
					logger.info("立鼎移动模版查询请求地址: {}" ,httppost.getRequestLine());
					CloseableHttpResponse response = httpclient.execute(httppost);
					logger.info("立鼎移动模版查询响应 : {}" ,response);
					
					try {
						System.out.println(response.getStatusLine());
						HttpEntity resEntity = response.getEntity(); //多媒体消息平台返回的消息，包含MsgId
						if (resEntity != null) {
							String responseEntityStr = EntityUtils.toString(response.getEntity());
							System.out.println("服务端返回信息：" + responseEntityStr);
							System.out.println("Response content length: " + resEntity.getContentLength());
							result= JSONObject.parseObject(responseEntityStr);
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
				//提交成功返回通道模版id，需要跟模版id做一个映射
				String resCode = result.getString("code");
				String resMsg = result.getString("msg");
				String data = result.getString("data");
				JSONObject jsonObject = JSONObject.parseObject(data);
				if("0".equals(resCode)){
					String cmccStatus = jsonObject.getString("cmccStatus");
					String cmccReason = jsonObject.getString("cmccReason");
					String info="审核中";
					Long status=4L;
					if("201".equals(cmccStatus)){
						info="待初审";
					}
					if("202".equals(cmccStatus)){
						info="待终审";
					}
					if("000".equals(cmccStatus)){
						info="审核失败"+cmccReason;
						status=2L;
					}
					if("100".equals(cmccStatus)){
						info="审核成功";
						status=1L;
					}

					String updateSql = String.format("update e_model_info_new set status=%s,info='%s' where id=%s;",status,info,id);
					RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
					RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
					logger.info("updateSql: {}",updateSql);


				}else if("3000021".equals(resCode)){
					String updateSql = String.format("update e_model_info_new set status=%s,info='%s' where id=%s;",2,"模版失效，通道侧模版id不存在",id);
					RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
					RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
					logger.info("updateSql: {}",updateSql);
					//删除模版映射关系
					String deleteSql = String.format("update e_model_related set status=%s where model_id='%s' and channel_model_id='%s' and channel_id=%s;",2,pModelId,channelModelId,1);
					RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,deleteSql);
					RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
					logger.info("deleteSql: {}",deleteSql);
				} else {
					logger.error("视频短信模板素材处理失败resCode：{},resMsg: {},transId: {}",resCode,resMsg);
				}

				count++;
				// 如果超过X条
				if (count >= concurrentSize) {
					break;
				}
			}
			String conf_key = RedisUtils.HASH_QUERY_MODEL_TOTAL;
			RedisUtils.hash_incrBy(conf_key,companyId+"_"+appId+"_"+channelId,0-count);
		} catch (Exception e) {
			logger.error("视频短信模板素材处理失败key：{},ex:{}",RedisUtils.FIFO_QUERY_MODEL_LIST+companyId+":"+appId+":"+channelId,e.getMessage());
			e.printStackTrace();
		}

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