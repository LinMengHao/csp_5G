package com.xzkj.taskChannel.sms.handler;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskChannel.redis.RedisUtils;
import com.xzkj.taskChannel.util.HttpInterface;
import com.xzkj.taskChannel.util.MD5Utils;
import com.xzkj.taskChannel.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class HandlerShenZhenWuLang implements Runnable {

	public static Logger logger = LoggerFactory.getLogger("HandlerShenZhenWuLang");

	private int linkSpeed=0;
	private JSONObject jsonChannel=null;
	private String channelId=null;
	private String channelMmsId=null;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("HH");//只有小时
	public HandlerShenZhenWuLang(int linkSpeed, String channelId, String channelMmsId, JSONObject jsonChannel){
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
				if(org.apache.commons.lang.StringUtils.isBlank(jsonStr)){
					continue;
				}
				JSONObject json = JSONObject.parseObject(jsonStr);

				int count = send(json);//提交
				size+=count;
				if(size>=linkSpeed){
					break;
				}
			}
//			while((jsonStr = RedisUtils.fifo_pop(channel_key))!=null){
//				size ++;
//				if(StringUtils.isNotBlank(jsonStr)){
//					try{
//						JSONObject json = JSONObject.parseObject(jsonStr);
//						orderList.add(json);
//					}catch(Exception e){
//						logger.info("HandlerShenZhenWuLang jsonStr【{}】 操作异常:{}",jsonStr, e);
//						continue;
//					}
//				}
//				if(size>=21){
//					sends(orderList);
//					orderList.clear();
//					break;
//				}
//
//			}
//			// 如果有未提交的
//			if (orderList.size() > 0) {
//				sends(orderList);
//				orderList.clear();
//			}
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
			logger.info("物朗开始提交...");
			String spid="1195";
			String login="admin";
			String pwd= MD5Utils.MD5Encode(login+"!yJIE7TU6").toUpperCase();
			String url = "http://120.24.229.213:8513/sms/templateSend";

			String mobile=json.getString("mobile");//手机号
			String linkId=json.getString("linkId");//客户唯一id
			//String param=json.getString("param");//变量模板参数
			String channelParam=json.getString("channelParam");//通道变量模板参数
			int sendType = json.getInteger("sendType");//1-普通视频短信发送，2-变量视频短信发送



			JSONObject submitJson = new JSONObject();
			submitJson.put("phones",mobile);
			submitJson.put("sn",linkId);
			submitJson.put("spid",spid);
			submitJson.put("login",login);
			submitJson.put("pwd",pwd);
			submitJson.put("tid",channelMmsId);

			if(sendType==2){
				JSONObject params=new JSONObject();
				if(!StringUtils.isBlank(channelParam)){
					String[] split = channelParam.split(",");
					for (int i = 1; i <= split.length; i++) {
						String key=split[i-1].split("=").length>1?split[i-1].split("=")[0]:"v"+i;
						String value=split[i-1].split("=").length>1?split[i-1].split("=")[1]:split[i-1];
						if(!key.equals("v"+i)){
							key="v"+i;
						}
						params.put(key,value);
					}
				}
				//目前是单号码单发送，循环便利，则无需关注号码数和变量组一一对应
				submitJson.put("vars","["+params.toJSONString()+"]");
			}
			logger.info("正在提交...:{}",submitJson.toJSONString());
			Hashtable<String, String> formHeaders = new Hashtable<String, String>();
			formHeaders.put("Content-Type", "application/json;charset=utf-8");
			String str = HttpInterface.httpClientPost(url, submitJson.toJSONString(), 60000, "UTF-8", formHeaders);
			/*if(str.indexOf("http请求异常")==0){
				str = HttpInterface.httpClientPost(url, submitJson.toJSONString(), 300000, "UTF-8", formHeaders);
			}*/
			logger.info("视频短信物朗提交结果：{}-{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,mobile,submitJson.toJSONString(),str);
			String code="";
			String msg="提交通道失败";
			String channneMsgId="";
			if(str.indexOf("Read timed out")!=-1){
				code = "0";
				msg = "Read timed out";
				channneMsgId = linkId;
			}else if(str.indexOf("http请求异常")==0){
				code="-1";
				msg=str;
				logger.info("视频短信物朗移动提交结果超时：{}-{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,mobile,submitJson.toJSONString(),str);
			}else{
				JSONObject result = JSONObject.parseObject(str);
				code = result.containsKey("result")?result.getString("result"):"-1";
				msg = result.containsKey("description")?result.getString("description"):"解析参数错误";
				channneMsgId = result.containsKey("taskid")?result.getString("taskid"):"";
			}
			json.put("channneMsgId",channneMsgId);
			json.put("info",msg);
			//提交成功
			if(code.equals("0")){
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

	//组21包
	public int sends(List<JSONObject> jsonList) {
		try {
			logger.info("物朗开始提交...");
			String spid="1195";
			String login="admin";
			String pwd= MD5Utils.MD5Encode(login+"!yJIE7TU6").toUpperCase();
			String url = "http://120.24.229.213:8513/sms/templateSend";
			JSONObject submitJson = new JSONObject();
			submitJson.put("spid",spid);
			submitJson.put("login",login);
			submitJson.put("pwd",pwd);
			submitJson.put("tid",channelMmsId);
			StringBuilder phones=new StringBuilder();
			JSONArray vars=new JSONArray();
			for (int j=0;j<jsonList.size();j++){
				JSONObject json = jsonList.get(j);
				String mobile=json.getString("mobile");//手机号
				//String param=json.getString("param");//变量模板参数
				String channelParam=json.getString("channelParam");//通道变量模板参数
				int sendType = json.getInteger("sendType");//1-普通视频短信发送，2-变量视频短信发送
				if(j==jsonList.size()-1){
					phones.append(mobile);
				}else {
					phones.append(mobile).append(",");
				}

				if(sendType==2){
					JSONObject params=new JSONObject();
					if(!StringUtils.isBlank(channelParam)){
						String[] split = channelParam.split(",");
						for (int i = 1; i <= split.length; i++) {
							String key=split[i-1].split("=").length>1?split[i-1].split("=")[0]:"v"+i;
							String value=split[i-1].split("=").length>1?split[i-1].split("=")[1]:split[i-1];
							params.put(key,value);
						}
					}
					vars.add(params);
				}
			}
			submitJson.put("phones",phones.toString());
			submitJson.put("vars",vars.toJSONString());
			logger.info("正在提交...:{}",submitJson.toJSONString());
			Hashtable<String, String> formHeaders = new Hashtable<String, String>();
			formHeaders.put("Content-Type", "application/json;charset=utf-8");
			String str = HttpInterface.httpClientPost(url, submitJson.toJSONString(), 60000, "UTF-8", formHeaders);
			/*if(str.indexOf("http请求异常")==0){
				str = HttpInterface.httpClientPost(url, submitJson.toJSONString(), 300000, "UTF-8", formHeaders);
			}*/
			logger.info("视频短信物朗提交结果：{}-{}:{}:{}",channelId,channelMmsId,submitJson.toJSONString(),str);
			String code="";
			String msg="提交通道失败";
			String channneMsgId="";
			for (JSONObject json:jsonList){
				String linkId=json.getString("linkId");//客户唯一id
				if(str.indexOf("Read timed out")!=-1){
					code = "0";
					msg = "Read timed out";
					channneMsgId = linkId;
				}else if(str.indexOf("http请求异常")==0){
					code="-1";
					msg=str;
					logger.info("视频短信物朗移动提交结果超时：{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,submitJson.toJSONString(),str);
				}else{
					JSONObject result = JSONObject.parseObject(str);
					code = result.containsKey("result")?result.getString("result"):"-1";
					msg = result.containsKey("description")?result.getString("description"):"解析参数错误";
					channneMsgId = result.containsKey("taskid")?result.getString("taskid"):"";
				}
				json.put("channneMsgId",channneMsgId);
				json.put("info",msg);
				//提交成功
				if(code.equals("0")){
					insertMT(json,channelId,channneMsgId,msg);
				}else{
					insertFailMT(json,channelId,"mk:0010",msg);
				}
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

	private void insertFailMT(JSONObject json, String channelId, String status, String info) {
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

}