package com.xzkj.taskPorxy.sms.handler.models.check;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.util.Base64Util;
import com.xzkj.taskPorxy.util.HttpInterface;
import com.xzkj.taskPorxy.util.MD5Utils;
import com.xzkj.taskPorxy.util.MmsUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

public class HandlerModelToWuLang implements Runnable {
	public static Logger logger = LoggerFactory.getLogger("HandlerModelToWuLang");

	private String companyId;
	private String appId;

	private String channelId;
	private int concurrentSize=0;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");//年月日

	public HandlerModelToWuLang(String companyId, String appId, String channelId, int curCount) {
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
				String sign_id = modelInfo.containsKey("signId")?modelInfo.getString("signId"):"";
				//模板标题(按 urlencode编码 utf-8)
				String title = modelInfo.containsKey("title")?modelInfo.getString("title"):"";
				//变量标志（1表示变量模板，2表示非变量模板）
				String variable = modelInfo.containsKey("variate")?modelInfo.getString("variate"):"2";
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
				for(Map.Entry<String,JSONArray> entry:map.entrySet()){
					//帧排序
					Integer key = Integer.parseInt(entry.getKey());
					JSONArray value = entry.getValue();
					logger.info("key: {}, value: {}",key,value);
					for (int i = 0; i < value.size(); i++) {
						JSONObject jsonObject=new JSONObject();
						String s = value.getString(i);
						JSONObject jsonObject1 = JSONObject.parseObject(s);
						String type = jsonObject1.getString("type");
						String content = jsonObject1.getString("content");
						if("1".equals(type)){//1-文本，2-图片，3-视频，4-音频
							jsonObject.put("type","text");
							jsonObject.put("extType","txt");
							jsonObject.put("body",content);
						}else if("2".equals(type)){
							jsonObject.put("type","image");
							String[] split = content.split("\\.");
							String[] split1 = content.split("/");
							jsonObject.put("extType",split[split.length-1]);
							File file=new File(modelFilePath+"/"+split1[split1.length-2]+"/"+split1[split1.length-1]);
							byte[] bytes=null;
							FileInputStream in=null;
							try {
								in=new FileInputStream(file);
								bytes=new byte[in.available()];
								in.read(bytes);
							}catch (IOException e){
								e.printStackTrace();
							}finally {
								in.close();
							}
							String body = Base64Util.encodeBASE64(bytes);
							jsonObject.put("body",body);
						}else if("3".equals(type)){
							jsonObject.put("type","video");
							String[] split = content.split("\\.");
							String[] split1 = content.split("/");
							jsonObject.put("extType",split[split.length-1]);
							File file=new File(modelFilePath+"/"+split1[split1.length-2]+"/"+split1[split1.length-1]);
							byte[] bytes=null;
							FileInputStream in=null;
							try {
								in=new FileInputStream(file);
								bytes=new byte[in.available()];
								in.read(bytes);
							}catch (IOException e){
								e.printStackTrace();
							}finally {
								in.close();
							}
							String body = Base64Util.encodeBASE64(bytes);
							jsonObject.put("body",body);
						}else if("4".equals(type)){
							jsonObject.put("type","audio");
							String[] split = content.split("\\.");
							String[] split1 = content.split("/");
							jsonObject.put("extType",split[split.length-1]);
							File file=new File(modelFilePath+"/"+split1[split1.length-2]+"/"+split1[split1.length-1]);
							byte[] bytes=null;
							FileInputStream in=null;
							try {
								in=new FileInputStream(file);
								bytes=new byte[in.available()];
								in.read(bytes);
							}catch (IOException e){
								e.printStackTrace();
							}finally {
								in.close();
							}
							String body = Base64Util.encodeBASE64(bytes);
							jsonObject.put("body",body);
						}
						contentArr.add(jsonObject);
					}
				}

				String spid="1195";
				String login="admin";
				String pwd= MD5Utils.MD5Encode(login+"!yJIE7TU6").toUpperCase();
				String url = "http://120.24.229.213:8513/sms/templateAdd";
				JSONObject submitJson=new JSONObject();
				submitJson.put("spid",spid);
				submitJson.put("login",login);
				submitJson.put("pwd",pwd);
				submitJson.put("name",sign_id);
				JSONObject content=new JSONObject();
				content.put("subject",title);
				content.put("mmsbody",contentArr.toJSONString());
				submitJson.put("content",content);
				logger.info("正在提交...:{}",submitJson.toJSONString());
				Hashtable<String, String> formHeaders = new Hashtable<String, String>();
				formHeaders.put("Content-Type", "application/json;charset=utf-8");
				String str = HttpInterface.httpClientPost(url, submitJson.toJSONString(), 60000, "UTF-8", formHeaders);
				JSONObject result = JSONObject.parseObject(str);

				//提交成功返回通道模版id，需要跟模版id做一个映射
				String resCode = result.getString("result");
				String resMsg = result.getString("desc");
				String msgID = result.getString("templateId");
				if("0".equals(resCode)){
					RedisUtils.hash_set(RedisUtils.HASH_MODEL_MT_CHANNEL+channelId+":"+msgID,msgID,modelInfo.toJSONString());
					//修改模版状态
					//修改模版审核状态
					String updateSql = String.format("update e_model_info_new set status=%s,info='%s',channel_model_id='%s' where id=%s;",4,resMsg,msgID,id);
					RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
					RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
					logger.info("updateSql: {}",updateSql);
				}else {
					logger.error("视频短信模板素材处理失败resCode：{},resMsg: {},msgID: {}",resCode,resMsg,msgID);
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

}