package com.xzkj.taskProxy.sms.handler.sign;

import com.alibaba.fastjson.JSONObject;
import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.util.Base64Util;
import com.xzkj.taskProxy.util.HttpInterface;
import org.apache.commons.codec.digest.DigestUtils;
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

public class HandlerSignToLianTong implements Runnable {
	public static Logger logger = LoggerFactory.getLogger("HandlerSignToLianTong");

	private String companyId;
	private String appId;

	private String channelId;
	private int concurrentSize=0;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");//年月日

	public HandlerSignToLianTong(String companyId, String appId, String channelId, int curCount) {
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

				String appInfo = RedisUtils.string_get(RedisUtils.STR_KEY_APP_INFO+appName);
				JSONObject appJson = JSONObject.parseObject(appInfo);
				String companyId = appJson.getString("company_id");
				String appExt = appJson.getString("app_ext");
//				String extNum=appExt+id;
				String extNum=serviceCode.substring(8);


				JSONObject data=new JSONObject();
				MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
				if(!StringUtils.isBlank(filepath)){
					String[] split = filepath.split("/");
					String fileName=split[split.length-1];
					logger.info("文件名称：{}",fileName);
					data.put("fileName",fileName);
					File file=new File(filepath);
					FileBody fileBody=new FileBody(file);
//					multipartEntityBuilder.addPart(split[split.length-1],fileBody);
					multipartEntityBuilder.addPart(fileName,fileBody);
				}
				data.put("sign",reportSignContent);


				CloseableHttpClient httpclient = HttpClients.createDefault();
				JSONObject result=null;
				try{
					String apiKey="JKNEJZ202211";
					String apiSecrect="{bcrypt}$2a$10$A9Q9zmKcHIe5yBnJCWLPZezgIFgGthFDooVfL8v1bB29/0AZT7W4q";
					//默认不含变量
					String url = "http://szdxapi.wo.cn/api/sign/auditSubmit";
					String ts = System.currentTimeMillis() + "";
					String authenticator = DigestUtils.md5Hex(apiKey + ts + apiSecrect);
					data.put("appKey",apiKey);
					data.put("authenticator",authenticator);
					data.put("ts",ts);
					data.put("extNum",extNum);

					logger.info("提交参数：{}",data.toString());
					HttpPost httppost = new HttpPost(url);
					httppost.setConfig(RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build());
					StringBody stringBody = new StringBody(data.toString(), ContentType.APPLICATION_JSON);
					multipartEntityBuilder.addPart("Data",stringBody).setCharset(Charset.forName("utf-8"));
					HttpEntity entity=multipartEntityBuilder.build();
					httppost.setEntity(entity);
					logger.info("陕西联通签名提交请求地址: {}" ,httppost.getRequestLine());
					CloseableHttpResponse response = httpclient.execute(httppost);
					logger.info("陕西联通签名提交响应 : {}" ,response);
					
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
				String resCode = result.containsKey("Code")?result.getString("Code"):(result.containsKey("code")?result.getString("code"):"-1");
				String resMsg = result.getString("msg");
				String traceId = result.getString("traceId");
				String signId = result.getString("signId");
				if("0000".equals(resCode)){
					RedisUtils.hash_set(RedisUtils.HASH_SIGN_MT_CHANNEL+channelId+":"+signId,signId,bodyJson.toJSONString());
					//修改签名为审核中状态
					String updateSql = String.format("update e_model_sign set channel_sign_id='%s', status=%s,info='%s' where id=%s;",signId,4,resMsg,id);
					RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
					RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
					logger.info("updateSql: {}",updateSql);
				}else {
					logger.error("陕西联通签名报备处理失败resCode：{},resMsg: {}",resCode,resMsg);
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
			logger.error("陕西联通签名报备处理失败key：{},ex:{}",RedisUtils.FIFO_CHANNEL_SIGN_LIST+companyId+":"+appId+":"+channelId,e.getMessage());
			e.printStackTrace();
		}
		//处理时长计算
		//long ut = System.currentTimeMillis() - bt;
		//logger.error("时长统计：seqNo={}，itemNum={}，time={}，concurrentSize={}",seqNo,itemNum,ut,concurrentSize);
		return;
    }
}