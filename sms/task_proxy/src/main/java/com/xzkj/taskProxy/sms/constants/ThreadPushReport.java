package com.xzkj.taskProxy.sms.constants;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.service.ICommonService;
import com.xzkj.taskProxy.util.SpringBeanUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;


public class ThreadPushReport extends Thread{
	public static Logger logger = LoggerFactory.getLogger("ThreadPushReport");
	public static ICommonService commonServiceImpl = (ICommonService) SpringBeanUtil.getBean(ICommonService.class);
	public void run() { 
		logger.info("开始执行重推客户状态报告任务。。。");
		int i=0;
		RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");

		while(true){
			//long bt = System.currentTimeMillis();
			Map<String,JSONObject> appMap = new HashMap<String,JSONObject>();
			String jsonString=null;
			int size=0;
			//待推送列表
			while((jsonString = RedisUtils.fifo_pop(RedisUtils.FIFO_REPORT_LIST))!=null){
				JSONObject json = JSONObject.parseObject(jsonString);
				String table = json.getString("table");
				String ids = json.getString("ids");
				String[] idsArr = ids.split(",");
				for (String linkId:idsArr){
					String selectSql = "select * from "+table+" where link_id='"+linkId+"';";
					//下行信息列表
					List<Map<String, Object>> mapList = commonServiceImpl.commonInfoList(selectSql);
					for (Map<String, Object> map:mapList) {
						String mobile = map.containsKey("dest_number") ? map.get("dest_number").toString() : "";
						String companyId = map.containsKey("company_id") ? map.get("company_id").toString() : "0";
						String appId = map.containsKey("app_id") ? map.get("app_id").toString() : "0";
						String appName = map.containsKey("app_name") ? map.get("app_name").toString() : "";
						String mmsId = map.containsKey("batch_id") ? map.get("batch_id").toString() : "";
						String data = map.containsKey("data") ? map.get("data").toString() : "";

						JSONObject appJson =null;
						if(appMap.containsKey(appName)){
							appJson = appMap.get(appName);
						}else{
							String appInfo = RedisUtils.string_get(RedisUtils.STR_KEY_APP_INFO+appName);
							if(StringUtils.isNotBlank(appInfo)){
								appJson = JSONObject.parseObject(appInfo);
							}
						}
						if(appJson==null){
							continue;
						}
						String password = appJson.getString("password");
						String url = appJson.getString("rpt_sync_address");

						String signName="";
						String src="";

						String channelId = map.containsKey("channel_id") ? map.get("channel_id").toString() : "0";
						String batchId = map.containsKey("message_id") ? map.get("message_id").toString() : "";
						String channneMsgId = map.containsKey("channel_msg_id") ? map.get("channel_msg_id").toString() : "";
						String info = map.containsKey("info") ? map.get("info").toString() : "";
						String status = map.containsKey("status") ? map.get("status").toString() : "";
						String reportTime = map.containsKey("report_time") ? map.get("report_time").toString() :new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
						reportTime = reportTime.replaceAll("-","").replaceAll(":","").replaceAll("T","");
						if(reportTime.length()>14){
							reportTime=reportTime.substring(0,14);
						}

						String caller="";
						int sendType=1;//1-普通视频短信发送，2-变量视频短信发送
						JSONObject report = new JSONObject();
						report.put("linkId",linkId);
						report.put("tableName",table);
						report.put("sendType",sendType);
						report.put("mobile",mobile);
						report.put("param",sendType==2?"nama=,mob=":"");
						report.put("companyId",companyId);
						report.put("appId",appId);
						report.put("appName",appName);//客户账号
						report.put("password",password);//客户密码
						report.put("signName",signName);//短信签名
						report.put("srcNum",src);//接入号
						report.put("mmsId",mmsId);//模板id
						report.put("data",data);//
						report.put("url",url);//
						report.put("batchId",batchId);//客户提交相应 messageId
						report.put("allChannelNos", "egg"+caller+channelId);//该批次发送过的渠道，半角逗号分隔

						report.put("channneMsgId",channneMsgId);
						report.put("info",info);
						//客户状态报告推送
						report.put("reportStatus",status);
						report.put("reportTime",reportTime);
						RedisUtils.fifo_push(RedisUtils.FIFO_MMS_MT_CLIENT+companyId,report.toJSONString());
						RedisUtils.hash_incrBy(RedisUtils.HASH_MMS_MT_COUNT, companyId+"", 1);
					}

				}

			}
			/*i++;
			if(i%1000==0){
				logger.info(BusinessWorker.WorkerInfo());
				i=0;
			}*/
			//System.out.println("----------------本次循环耗时:"+(System.currentTimeMillis() - bt)+"----------------------------");
			try{
				if(RedisUtils.string_exist(RedisUtils.STR_KEY_AI_TASK)){
					if(RedisUtils.string_get(RedisUtils.STR_KEY_AI_TASK).equals("stop")){
						logger.info("..........重推客户状态报告任务退出..........");
						break;
					}
				}else{
					RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
				}
			}catch(Exception e){
				logger.error("ThreadPushReport error:string_exist:", e);
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
