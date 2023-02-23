package com.xzkj.taskPorxy.sms.constants;

import java.util.Map;


import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.sms.handler.HandlerCallBack;
import com.xzkj.taskPorxy.util.BusinessWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThreadCallBack extends Thread{
	public static Logger logger = LoggerFactory.getLogger("ThreadCallBack");

	public void run() { 
		logger.info("开始执行回调通知任务。。。");
		int i=0;
		RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
		while(true){
			//long bt = System.currentTimeMillis();
			String hash_sql_key = RedisUtils.HASH_MMS_MT_COUNT;
			Map<String, String> map = RedisUtils.hash_getFields(hash_sql_key);
			for (Map.Entry<String,String> en : map.entrySet()) {
				String companyId=en.getKey();
				int count = Integer.parseInt(en.getValue());
				if(count<=0){
					continue;
				}
				int total = count>100?100:count;
		        try {
		        	//new Thread(new HandlerCallBack(seqNo,total)).start();
		        	BusinessWorker.getExecutor().execute(new HandlerCallBack(companyId,total));
		        } catch (Exception e) {
		        	logger.error("ThreadCallBack error", e);
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
						logger.info("..........客户回调任务退出..........");
						break;
					}
				}else{
					RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
				}
			}catch(Exception e){
				logger.error("ThreadCallBack error:string_exist:", e);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
