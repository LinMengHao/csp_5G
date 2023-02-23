package com.xzkj.taskChannel.sms.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskChannel.redis.RedisUtils;
import com.xzkj.taskChannel.util.Base64Util;
import com.xzkj.taskChannel.util.HttpInterface;
import com.xzkj.taskChannel.util.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;


public class HandlerDanYuanKJ implements Runnable {

	public static Logger logger = LoggerFactory.getLogger("HandlerDanYuanKJ");

	private int linkSpeed=0;
	private JSONObject jsonChannel=null;
	private String channelId=null;
	private String channelMmsId=null;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("HH");//只有小时
	public HandlerDanYuanKJ(int linkSpeed, String channelId, String channelMmsId, JSONObject jsonChannel){
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
			String apiKey="TBjhxM4";
			String apiSecrect="9zpYPCFy";
			String url = "http://8.136.153.186:8888/smsapi/SmsMt";

			Date date1 = new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			String date2 = sdf.format(date1);
			String token= MD5Utils.MD5Encode(apiKey+date2+apiSecrect).toLowerCase();
			String authorization= Base64Util.encodeBASE64(apiKey+":"+date2);


			String mobile=json.getString("mobile");//手机号
			String linkId=json.getString("linkId");//客户唯一id
			String signName=json.getString("signName");//签名
			//String param=json.getString("param");//变量模板参数
			String channelParam=json.containsKey("channelParam")?json.getString("channelParam"):"";//通道变量模板参数
			int sendType = json.getInteger("sendType");//1-普通视频短信发送，2-变量视频短信发送
			//TODO 通过
			String content="【"+signName+"】"+"templateId="+channelMmsId;
			String variableText=channelParam;//非必填，变量参数 姓名=599,性别=599
			logger.info("变量。。。。");
			if(sendType==2){
				StringBuilder c=new StringBuilder();
				c.append("【").append(signName).append("】").append("templateId=").append(channelMmsId);
				if(com.xzkj.taskChannel.util.StringUtils.isBlank(channelParam)){
					String[] split = channelParam.split(",");
					for (int i = 1; i <= split.length; i++) {
						c.append("#");
						c.append("variable"+i);
						c.append("=").append(split[i-1]);
					}
				}
				c.append("【").append(signName).append("】");
				content=c.toString();
			}

			Map<String,String> map=new HashMap<>();
			map.put("token",token);
			map.put("mobile",mobile);
			map.put("msg",content);
			map.put("serialno",linkId);
			Hashtable<String, String> formHeaders = new Hashtable<String, String>();
			formHeaders.put("Content-Type", "application/x-www-form-urlencoded");
			formHeaders.put("Authorization", authorization);
			logger.info("请求前。。。。");
			String str = HttpInterface.httpClientPostByBody(url, map, 60000, "UTF-8",formHeaders);

			/*if(str.indexOf("http请求异常")==0){
				str = HttpInterface.httpClientPost(url, submitJson.toJSONString(), 300000, "UTF-8", formHeaders);
			}*/
			logger.info("视频短信北京移动提交结果：{}-{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,mobile,map.toString(),str);
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
				logger.info("视频短信北京移动提交结果超时：{}-{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,mobile,map.toString(),str);
			}else{
				JSONObject result = JSONObject.parseObject(str);
				String retCode = result.getString("RetCode");
				if("0".equals(retCode)){
					if(result.containsKey("Rets")){
						JSONArray rets = result.getJSONArray("Rets");
						for (int i = 0; i < rets.size(); i++) {
							JSONObject jsonObject1 = rets.getJSONObject(i);
							code= jsonObject1.getString("RetCode");
							channneMsgId = jsonObject1.getString("MsgId");
							if(code.equals("0")){
								msg="提交通道成功";
							}
						}
					}
				}else {
					code=retCode;
					msg=code+": 提交通道失败";
					channneMsgId = linkId;
				}



			}
			json.put("channneMsgId",channneMsgId);
			json.put("info",msg);
			//提交成功
			if(code.equals("0")){
				insertMT(json,channelId,channneMsgId,msg);
			}else{
				insertFailMT(json,channelId,"UNDELIV",msg);
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

}