package com.xzkj.taskPorxy.sms.constants;


import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.sms.handler.models.check.*;
import com.xzkj.taskPorxy.util.BusinessWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ThreadModelToChannel extends Thread{
	public static Logger logger = LoggerFactory.getLogger("ThreadModelToChannel");

	public void run() { 
		logger.info("开始执行视频短信模板提交到通道任务处理。。。");
		RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
		int i=0;
		while(true){
			//long bt = System.currentTimeMillis();
			String conf_key = RedisUtils.HASH_CHANNEL_MODEL_TOTAL;
			Map<String, String> map = RedisUtils.hash_getFields(conf_key);
			for (Map.Entry<String,String> en : map.entrySet()) {
				String values = en.getValue();
				int curCount = Integer.parseInt(values);
				if(curCount<=0){
					continue;
				}
				String key = en.getKey();
				String[] keyArr = key.split("_");
				String companyId= keyArr[0];
				String app_id = keyArr[1];
				String channelId=keyArr[2];

				curCount = curCount>10?10:curCount;
				switch (channelId) {
					case "1"://beijing cmcc
						BusinessWorker.getExecutor().execute(new HandlerModelToLiDinYD(companyId, app_id, channelId,curCount));
						break;
					case "2"://beijing cmcc2
						BusinessWorker.getExecutor().execute(new HandlerModelToTaSiYD(companyId, app_id, channelId,curCount));
						break;
					case "3"://陕西联通
						BusinessWorker.getExecutor().execute(new HandlerModelToLianTong(companyId, app_id, channelId,curCount));
						break;
					case "4"://beijing cmcc4物朗
						BusinessWorker.getExecutor().execute(new HandlerModelToWuLang(companyId, app_id, channelId,curCount));
						break;
					case "5"://上海电信
						BusinessWorker.getExecutor().execute(new HandlerModelToShanghaiDianXin(companyId, app_id, channelId,curCount));
						break;
					case "6"://beijing cmcc3
						BusinessWorker.getExecutor().execute(new HandlerModelToHLGYD(companyId, app_id, channelId,curCount));
						break;
					case "7"://修治上海电信
						BusinessWorker.getExecutor().execute(new HandlerModelToXZShanghaiDianXin(companyId, app_id, channelId,curCount));
						break;
					case "8"://物朗-电信2
						BusinessWorker.getExecutor().execute(new HandlerModelToWuLangTwo(companyId, app_id, channelId,curCount));
						break;
					default:
						break;
				};

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
						logger.info("..........视频短信模板处理任务退出..........");
						break;
					}
				}else{
					RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
				}
			}catch(Exception e){
				logger.error("ThreadModelToChannel error:string_exist:", e);
			}
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
