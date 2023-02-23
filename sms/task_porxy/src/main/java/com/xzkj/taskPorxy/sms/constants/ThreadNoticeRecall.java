package com.xzkj.taskPorxy.sms.constants;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.sms.handler.HandlerNoticeRecallPretreat;
import com.xzkj.taskPorxy.util.BusinessWorker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ThreadNoticeRecall extends Thread{
	public static Logger logger = LoggerFactory.getLogger("ThreadNoticeRecall");

	public void run() { 
		logger.info("开始执行语音通知补呼预处理任务。。。");
		RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
		int i=0;
		while(true){
			//long bt = System.currentTimeMillis();
			String recall_key = RedisUtils.HASH_RECALL_ALL+"notice";
			Map<String, String> map = RedisUtils.hash_getFields(recall_key);
			for (Map.Entry<String,String> en : map.entrySet()) {
				
				String key=en.getKey();
				String values = en.getValue();
				String[] keyArr = key.split("_");
				String seqNo = keyArr[0];
				String item_num = keyArr[1];
				int count = Integer.parseInt(values);
				if(count==0||StringUtils.isBlank(seqNo)||StringUtils.isBlank(item_num)){
					continue;
				}
				int total = count>100?100:count;
				
				String jsonStr = RedisUtils.hash_get(RedisUtils.HASH_ITEM_JSON+seqNo, item_num);
				JSONObject json = JSONObject.parseObject(jsonStr);
				int recallLimit = json.containsKey("recallTimes")?json.getInteger("recallTimes"):0;
				BusinessWorker.getExecutor().execute(new HandlerNoticeRecallPretreat(seqNo, item_num, total,recallLimit));
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
						logger.info("..........语音通知呼叫任务退出..........");
						break;
					}
				}else{
					RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
				}
			}catch(Exception e){
				logger.error("ThreadNoticeRecall error:string_exist:", e);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
