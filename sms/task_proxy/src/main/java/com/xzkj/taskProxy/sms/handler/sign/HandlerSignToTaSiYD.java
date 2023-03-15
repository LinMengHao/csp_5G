package com.xzkj.taskProxy.sms.handler.sign;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.util.Base64Util;
import com.xzkj.taskProxy.util.HttpInterface;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class HandlerSignToTaSiYD implements Runnable {
	public static Logger logger = LoggerFactory.getLogger("HandlerSignToLiDinYD");

	private String companyId;
	private String appId;

	private String channelId;
	private int concurrentSize=0;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");//年月日

	public HandlerSignToTaSiYD(String companyId, String appId, String channelId, int curCount) {
		this.companyId=companyId;
		this.appId=appId;
		this.concurrentSize=curCount;
		this.channelId=channelId;
	}
	public void run(){
		//long bt = System.currentTimeMillis();
		try {
			//队列key
			String fifo_key = RedisUtils.FIFO_CHANNEL_SIGN_LIST+companyId+":"+appId+":"+channelId;
			String jsonStr = null;
			int count=0;
			while((jsonStr = RedisUtils.fifo_pop(fifo_key))!=null){
				JSONObject bodyJson = JSONObject.parseObject(jsonStr);
				logger.info("模版提交通道数据：{}",bodyJson);
				String password = bodyJson.getString("password");
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
				String industry = bodyJson.containsKey("industry")?bodyJson.getString("industry"):"13";
				//客户分类
				Long customerType = bodyJson.containsKey("customerType")?bodyJson.getLong("customerType"):1l;
				//操作类型
				Long operatorType = bodyJson.containsKey("operatorType")?bodyJson.getLong("operatorType"):1l;
				//申请文件地址
				String filepath = bodyJson.containsKey("filepath")?bodyJson.getString("filepath"):"";

				Long id = bodyJson.containsKey("id")?bodyJson.getLong("id"):0l;

				MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
				if(!StringUtils.isBlank(filepath)){
					String[] split = filepath.split("/");
					File file=new File(filepath);
					FileBody fileBody=new FileBody(file);
//					multipartEntityBuilder.addPart(split[split.length-1],fileBody);
					multipartEntityBuilder.addPart("upLoadFile",fileBody);
				}


				CloseableHttpClient httpclient = HttpClients.createDefault();
				JSONObject result=null;
				try{
					String apiKey="xiuzhi02ts";
					String apiSecrect="a0%DUY5Q";
					String url = "http://36.138.133.214:30080/api/sign/new";

					String token=RedisUtils.string_get(RedisUtils.STR_CHANNEL_TOKEN+channelId);
					if(StringUtils.isBlank(token)){
						token=getToken(apiKey,apiSecrect,channelId);
					}


					StringBuilder stringBuilder=new StringBuilder();
					stringBuilder.append("customerType=").append(customerType).append("&").append("ecCity=").append(ecCity)
							.append("&").append("ecName=").append(ecName).append("&").append("ecProvince=").append(ecProvince)
							.append("&").append("industry=").append(industry).append("&").append("operatorType=").append(operatorType)
							.append("&").append("rcsIndustry=").append(rcsIndustry).append("&").append("reportSignContent=").append(reportSignContent)
							.append("&").append("serviceCode=").append(serviceCode);
					String sign= getSign(stringBuilder.toString());
					logger.info("参数: {}",stringBuilder.toString());

					HttpPost httppost = new HttpPost(url);
					httppost.setHeader("Authorization", token);
					httppost.setConfig(RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build());

					StringBody signBody = new StringBody(sign, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("sign",signBody).setCharset(Charset.forName("utf-8"));

					StringBody customerTypeBody = new StringBody(String.valueOf(customerType), ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("customerType",customerTypeBody).setCharset(Charset.forName("utf-8"));

					StringBody ecCityBody = new StringBody(ecCity, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("ecCity",ecCityBody).setCharset(Charset.forName("utf-8"));

					StringBody ecNameBody = new StringBody(ecName, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("ecName",ecNameBody).setCharset(Charset.forName("utf-8"));

					StringBody ecProvinceBody = new StringBody(ecProvince, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("ecProvince",ecProvinceBody).setCharset(Charset.forName("utf-8"));

					StringBody industryBody = new StringBody(industry, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("industry",industryBody).setCharset(Charset.forName("utf-8"));

					StringBody operatorTypeBody = new StringBody(String.valueOf(operatorType), ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("operatorType",operatorTypeBody).setCharset(Charset.forName("utf-8"));

					StringBody rcsIndustryBody = new StringBody(rcsIndustry, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("rcsIndustry",rcsIndustryBody).setCharset(Charset.forName("utf-8"));

					StringBody reportSignContentBody = new StringBody(reportSignContent, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("reportSignContent",reportSignContentBody).setCharset(Charset.forName("utf-8"));

					StringBody serviceCodeBody = new StringBody(serviceCode, ContentType.TEXT_PLAIN);
					multipartEntityBuilder.addPart("serviceCode",serviceCodeBody).setCharset(Charset.forName("utf-8"));


					HttpEntity entity=multipartEntityBuilder.build();
					logger.info("提交参数：{}",entity.toString());
					httppost.setEntity(entity);
					logger.info("立鼎移动签名提交请求地址: {}" ,httppost.getRequestLine());
					CloseableHttpResponse response = httpclient.execute(httppost);
					logger.info("立鼎移动签名提交响应 : {}" ,response);
					
					try {
						System.out.println(response.getStatusLine());
						HttpEntity resEntity = response.getEntity(); //多媒体消息平台返回的消息，包含signId
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
				String signId = result.getString("signId");
				if("0".equals(resCode)){
					RedisUtils.hash_set(RedisUtils.HASH_SIGN_MT_CHANNEL+channelId+":"+signId,signId,bodyJson.toJSONString());
					//修改签名为审核中状态
					String updateSql = String.format("update e_model_sign set channel_sign_id='%s', status=%s,info='%s' where id=%s;",signId,4,resMsg,id);
					RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
					RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
					logger.info("updateSql: {}",updateSql);
				}else {
					logger.error("视频短信模板素材处理失败resCode：{},resMsg: {},transId: {}",resCode,resMsg);
				}

				count++;
				// 如果超过X条
				if (count >= concurrentSize) {
					break;
				}
			}
			String conf_key = RedisUtils.HASH_CHANNEL_SIGN_TOTAL;
			RedisUtils.hash_incrBy(conf_key,companyId+"_"+appId+"_"+channelId,0-count);
		} catch (Exception e) {
			logger.error("视频短信模板素材处理失败key：{},ex:{}",RedisUtils.FIFO_CHANNEL_SIGN_LIST+companyId+":"+appId+":"+channelId,e.getMessage());
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

	private String getSign(String s){
		//格式：ecProvince=${ecProvince}&ecCity=${ecCity}&serviceCode=${serviceCode}&reportSignContent=${reportSignContent}&ecName=${ecName}&rcsIndustry=${rcsIndustry}&industry=${industry}&customerType=${customerType}&operatorType=${operatorType}
		String[] split = s.split("&");
		TreeMap strMap=new TreeMap();
		for (int i = 0; i < split.length; i++) {
			String[] pandvalue = split[i].split("=");
			if (pandvalue.length>1){
				String[] values = Arrays.copyOfRange(pandvalue, 1, pandvalue.length);
				if(!"".equals(values[0])){
					strMap.put(pandvalue[0],values[0]);
				}
			}else {
//				strMap.put(pandvalue[0],null);
			}

		}
		String sign="";
		Set set=strMap.entrySet();
		Iterator iterator= set.iterator();
		int iterationCount=0;
		while(iterator.hasNext()){
			Map.Entry mentry=(Map.Entry)iterator.next();
			if (iterationCount==0){
				sign+=mentry.getKey()+"="+mentry.getValue();
			}else {
				sign+="&"+mentry.getKey()+"="+mentry.getValue();
			}
			iterationCount++;
		}
		try {
			sign= URLDecoder.decode(sign,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		sign= Base64Util.encodeBASE64(Base64Util.getSHA256(sign.toString()));
		return sign;
	}
}