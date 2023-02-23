package com.xzkj.taskPorxy.sms.constants;


import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.sms.handler.HandlerDataBase;
import com.xzkj.taskPorxy.util.BusinessWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class ThreadDataBase extends Thread{
	public static Logger logger = LoggerFactory.getLogger("ThreadDataBase");

	public void run() { 
		logger.info("开始执行数据库任务。。。");
		RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
		int i=0;
		while(true){
			//long bt = System.currentTimeMillis();
			String hash_sql_key = RedisUtils.HASH_SQL_COUNT;
			Map<String, String> map = RedisUtils.hash_getFields(hash_sql_key);
			for (Map.Entry<String,String> en : map.entrySet()) {
				String companyId=en.getKey();
				int count = Integer.parseInt(en.getValue());
				if(count<=0){
					continue;
				}
				int total = count>1000?1000:count;
		        try {
		        	//new Thread(new HandlerDataBase(seqNo,total)).start();
		        	BusinessWorker.getExecutor().execute(new HandlerDataBase(companyId,total));
		        } catch (Exception e) {
		        	logger.error("HandlerDataBase error", e);
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
						logger.info("..........数据库操作任务退出..........");
						break;
					}
				}else{
					RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
				}
			}catch(Exception e){
				logger.error("HandlerDataBase error:string_exist:", e);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
