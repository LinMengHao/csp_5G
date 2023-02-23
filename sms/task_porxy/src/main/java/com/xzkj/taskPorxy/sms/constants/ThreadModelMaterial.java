package com.xzkj.taskPorxy.sms.constants;


import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.sms.handler.HandlerModelMaterial;
import com.xzkj.taskPorxy.util.BusinessWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ThreadModelMaterial extends Thread{
	public static Logger logger = LoggerFactory.getLogger("ThreadModelMaterial");

	public void run() { 
		logger.info("开始执行视频短信模板处理任务。。。");
		RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
		int i=0;
		while(true){
			//long bt = System.currentTimeMillis();
			String conf_key = RedisUtils.HASH_APP_MODEL_TOTAL;
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

				curCount = curCount>10?10:curCount;

				BusinessWorker.getExecutor().execute(new HandlerModelMaterial(companyId, app_id, curCount));

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
				logger.error("ThreadModelMaterial error:string_exist:", e);
			}
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
