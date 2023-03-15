package com.xzkj.taskProxy.sms.handler;


import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.service.ICommonService;
import com.xzkj.taskProxy.util.SpringBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HandlerDataBase implements Runnable {

public static Logger logger = LoggerFactory.getLogger("HandlerDataBase");
	
	private String companyId;
	private int total;
	public static ICommonService voiceServiceImpl = (ICommonService) SpringBeanUtil.getBean(ICommonService.class);
	
	public HandlerDataBase(String companyId,int total) throws IOException{
		this.companyId=companyId;
		this.total=total;
	}
	public void run(){
		//long bt = System.currentTimeMillis();
		List<String> sqlList = new ArrayList<String>();
		String fifo_sql_key = RedisUtils.FIFO_SQL_LIST+this.companyId;
		String sql=null;
		int size=0;
		while((sql = RedisUtils.fifo_pop(fifo_sql_key))!=null){
			if(sql.indexOf(";")==-1){
				sql+=";";
			}
			sqlList.add(sql);
			size ++;
			if(size>=this.total){
				break;
			}
		}
		String hash_sql_key = RedisUtils.HASH_SQL_COUNT;
		RedisUtils.hash_incrBy(hash_sql_key, companyId, 0-size);
		// 如果有需要入库操作的
		if (sqlList.size()!= 0) {
			try{
				voiceServiceImpl.batchUpdate(sqlList);
			}catch(Exception e){
				logger.error("HandlerDataBase 入库操作异常:"+ e);
				logger.error("HandlerDataBase 入库操作异常:"+ sqlList.toString());
			}
			
		}
		return;
    }
}