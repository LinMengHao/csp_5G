package com.xzkj.taskPorxy.sms.constants;


import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.sms.handler.HandlerAppBase;
import com.xzkj.taskPorxy.util.BusinessWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThreadAppBase extends Thread{
	public static Logger logger = LoggerFactory.getLogger("ThreadAppBase");

	public void run() {
		logger.info("开始执行加载用户信息进redis缓存任务。。。");
		RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
		while(true){
			//long bt = System.currentTimeMillis();
			BusinessWorker.getExecutor().execute(new HandlerAppBase());
			//System.out.println("----------------本次循环耗时:"+(System.currentTimeMillis() - bt)+"----------------------------");
			try{
				if(RedisUtils.string_exist(RedisUtils.STR_KEY_AI_TASK)){
					if(RedisUtils.string_get(RedisUtils.STR_KEY_AI_TASK).equals("stop")){
						logger.info("..........加载用户信息进redis缓存任务退出..........");
						break;
					}
				}else{
					RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
				}
			}catch(Exception e){
				logger.error("ThreadAppBase error:string_exist:", e);
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
