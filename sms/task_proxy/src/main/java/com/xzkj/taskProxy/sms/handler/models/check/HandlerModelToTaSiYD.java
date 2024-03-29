package com.xzkj.taskProxy.sms.handler.models.check;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.util.Base64Util;
import com.xzkj.taskProxy.util.HttpInterface;
import com.xzkj.taskProxy.util.MmsUtils;
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

public class HandlerModelToTaSiYD implements Runnable {
	public static Logger logger = LoggerFactory.getLogger("HandlerModelToLiDinYD");

	private String companyId;
	private String appId;

	private String channelId;
	private int concurrentSize=0;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");//年月日

	public HandlerModelToTaSiYD(String companyId, String appId, String channelId, int curCount) {
		this.companyId=companyId;
		this.appId=appId;
		this.concurrentSize=curCount;
		this.channelId=channelId;
	}
	public void run(){
		//long bt = System.currentTimeMillis();
		try {
			//队列key
			String fifo_key = RedisUtils.FIFO_CHANNEL_MODEL_LIST+companyId+":"+appId+":"+channelId;
			String jsonStr = null;
			int count=0;
			while((jsonStr = RedisUtils.fifo_pop(fifo_key))!=null){
				JSONObject json = JSONObject.parseObject(jsonStr);
				logger.info("模版提交通道数据：{}",json);
				String password = json.getString("password");
				JSONObject modelInfo = JSONObject.parseObject(json.getString("modelInfo"));
				modelInfo.put("password",password);

				//用户账号
				String appName = modelInfo.containsKey("appName")?modelInfo.getString("appName"):"";
				//短信签名id
				String signId = modelInfo.containsKey("signId")?modelInfo.getString("signId"):"";
				//模板标题(按 urlencode编码 utf-8)
				String title = modelInfo.containsKey("title")?modelInfo.getString("title"):"";
				//变量标志（1表示变量模板，2表示非变量模板）
				String variable = modelInfo.containsKey("variate")?modelInfo.getString("variate"):"2";
				if ("2".equals(variable)){
					variable="0";
				}
				//提交渠道id
				String channelId = modelInfo.containsKey("channelId")?modelInfo.getString("channelId"):"";
				//模版id
				String modelId = modelInfo.containsKey("modelId")?modelInfo.getString("modelId"):"";

				String backUrl = modelInfo.containsKey("backUrl")?modelInfo.getString("backUrl"):"";

				String id = modelInfo.containsKey("id")?modelInfo.getString("id"):"";

				if (StringUtils.isBlank(backUrl)){
					backUrl=json.getString("backUrl");
					modelInfo.put("backUrl",backUrl);
				}
				String extNum = modelInfo.containsKey("extNum")?modelInfo.getString("extNum"):"";

				//模板媒体的文件内容(json 数组格式字符串)
				Map<String,JSONArray> map=(Map<String,JSONArray>)json.get("data");
				logger.info("素材：{}",map);
				logger.info("map：{}",map.get("1"));
				//提交时间
				String submitTime = json.containsKey("submitTime")?json.getString("submitTime"):"";
				if(StringUtils.isBlank(submitTime)){
					submitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				}
				/*//公司id
				String companyId = modelInfo.containsKey("companyId")?modelInfo.getString("companyId"):"";*/
				//账号id
				String appId = modelInfo.containsKey("appId")?modelInfo.getString("appId"):"0";
				//模板文件实际路径
				String modelFilePath = json.containsKey("modelFilePath")?json.getString("modelFilePath"): MmsUtils.modelFilePath;
				//模板文件相对路径
				String modelFilePaths = json.containsKey("modelFilePaths")?json.getString("modelFilePaths"):MmsUtils.modelFilePaths;
				//模板媒体的文件内容(json 数组格式字符串) [{"type":1,"ext":"txt","body":"…"},{"type":2,"ext":"jpg","body":"…"}]
				//文件内容（文本类型按 urlencode编码 utf-8；媒体文件按二进制内容读取后的 base64 编码 ）
				//文件内容解析
				JSONArray contentArr=new JSONArray();
				MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
				//有变量模版 记录该模版变量所在帧以及帧排序

//				String frame="";
				List<List> frames=new ArrayList<>();
				for(Map.Entry<String,JSONArray> entry:map.entrySet()){
					//帧排序
					Integer key = Integer.parseInt(entry.getKey());
					JSONArray value = entry.getValue();
					logger.info("key: {}, value: {}",key,value);
					List<JSONObject> list=new ArrayList<>();
					for (int i = 0; i < value.size(); i++) {
						JSONObject jsonObject=new JSONObject();
						String s = value.getString(i);
						JSONObject jsonObject1 = JSONObject.parseObject(s);
						//帧内排序
						String sort = jsonObject1.getString("sort");

						String type = jsonObject1.getString("type");
						String content = jsonObject1.getString("content");
						if("1".equals(type)){//1-文本，2-图片，3-视频，4-音频
							jsonObject.put("type","text");
							jsonObject.put("src",content);
//							if("1".equals(variable)){
//								//有变量模版 记录该模版变量所在帧以及帧排序
//								frame=key+"-"+sort;
//							}
						}else {
							String[] split = content.split("/");
							String fileName=split[split.length-1];
							jsonObject.put("type",String.valueOf(Integer.parseInt(type)-1));
							jsonObject.put("src",fileName);
							logger.info("文件名称：{}",fileName);
							logger.info("文件路径：{}",modelFilePath+split[split.length-2]+"/"+split[split.length-1]);
							File file=new File(modelFilePath+split[split.length-2]+"/"+split[split.length-1]);
							FileBody fileBody=new FileBody(file);
							multipartEntityBuilder.addPart("files",fileBody);
						}

						list.add(jsonObject);
					}
					frames.add(list);
				}

				CloseableHttpClient httpclient = HttpClients.createDefault();
				JSONObject result=null;
				try{
					String apiKey="xiuzhi02ts";
					String apiSecrect="a0%DUY5Q";
					String url = "http://36.138.133.214:30080/api/mms/saveMms";

					String token=RedisUtils.string_get(RedisUtils.STR_CHANNEL_TOKEN+channelId);
					if(StringUtils.isBlank(token)){
						token=getToken(apiKey,apiSecrect,channelId);
					}

					JSONObject reqElementsJsonObject = new JSONObject();
					//0-不支持异网 默认
					//1-支持异网
					reqElementsJsonObject.put("otherCarrier", "0");
					reqElementsJsonObject.put("signId",signId);
					reqElementsJsonObject.put("title",title);
//					是否支持变量：
//					1-是
//					0-不是
					reqElementsJsonObject.put("isVable",variable);
					reqElementsJsonObject.put("frames",frames);
//					是否添加退订信息,默认不添加
					reqElementsJsonObject.put("isUnsubscribe",false);
					StringBuilder stringBuilder=new StringBuilder();
					stringBuilder.append("frames=").append(frames).append("&").append("isUnsubscribe=false").append("&")
							.append("isVable=").append(variable).append("&").append("otherCarrier=0").append("&")
							.append("signId=").append(signId).append("&").append("title=").append(title);
					String sign= Base64Util.encodeBASE64(Base64Util.getSHA256(stringBuilder.toString()));
					logger.info("sign: {}",stringBuilder.toString());

					logger.info("提交数据：{}",reqElementsJsonObject.toJSONString());
					logger.info("素材multipartEntityBuilder: {}",multipartEntityBuilder.toString());
					HttpPost httppost = new HttpPost(url);
//					httppost.setHeader("Content-Type", "multipart/form-data;charset=utf-8");
					httppost.setHeader("Authorization", token);
					httppost.setConfig(RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build());

					StringBody stringBody = new StringBody(reqElementsJsonObject.toString(), ContentType.APPLICATION_JSON);
					multipartEntityBuilder.addPart("content",stringBody).setCharset(Charset.forName("utf-8"));

					StringBody signBody = new StringBody(sign, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("sign",signBody).setCharset(Charset.forName("utf-8"));
					HttpEntity entity=multipartEntityBuilder.build();

					httppost.setEntity(entity);
					logger.info("立鼎移动模版提交请求地址: {}" ,httppost.getRequestLine());
					CloseableHttpResponse response = httpclient.execute(httppost);
					logger.info("立鼎移动模版提交响应 : {}" ,response);
					
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
				String msgID = result.getString("mmsId");
				if("0".equals(resCode)){
					RedisUtils.hash_set(RedisUtils.HASH_MODEL_MT_CHANNEL+channelId+":"+msgID,msgID,modelInfo.toJSONString());
//					if("1".equals(variable)){
//						//有变量模版 记录该模版变量所在帧以及帧排序
//						RedisUtils.string_set(RedisUtils.HASH_MODEL_VARS_CHANNEL+channelId+":"+msgID,frame);
//					}
					//TODO 修改模版状态(过渡期)
//					if(modelId.length()<=11){
						//修改模版审核状态
						String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",4,resMsg,msgID,id);
						RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
						RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
						logger.info("updateSql: {}",updateSql);
//					}else {
//						//修改模版审核状态
//						String info="MsgId:<<"+msgID+">>"+"; "+resMsg;
//						String updateSql = String.format("update e_model_info set status=%s,info='%s' where id=%s;",4,info,id);
//						RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
//						RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
//						logger.info("updateSql: {}",updateSql);
//					}

				}else {
					logger.error("视频短信模板素材处理失败resCode：{},resMsg: {},transId: {}",resCode,resMsg);
				}

				count++;
				// 如果超过X条
				if (count >= concurrentSize) {
					break;
				}
			}
			String conf_key = RedisUtils.HASH_CHANNEL_MODEL_TOTAL;
			RedisUtils.hash_incrBy(conf_key,companyId+"_"+appId+"_"+channelId,0-count);
		} catch (Exception e) {
			logger.error("视频短信模板素材处理失败key：{},ex:{}",RedisUtils.FIFO_CHANNEL_MODEL_LIST+companyId+":"+appId+":"+channelId,e.getMessage());
			e.printStackTrace();
		}
		//处理时长计算
		//long ut = System.currentTimeMillis() - bt;
		//logger.error("时长统计：seqNo={}，itemNum={}，time={}，concurrentSize={}",seqNo,itemNum,ut,concurrentSize);
		return;
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