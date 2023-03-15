/**
* @author LJL
* @version 创建时间：2019年10月24日 下午7:51:24
* @ClassName 类名称
* @Description 类描述
*/
package com.xzkj.taskProxy.sms.constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.util.BusinessWorker;
import com.xzkj.taskProxy.util.HttpInterface;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author admin
 */
public class ThreadCallbackMore extends Thread{
	
	public static Logger logger = LoggerFactory.getLogger("ThreadCallbackMore");

	public void run() { 
		logger.info("开始执行回调补呼任务。。。");
		RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
		SimpleDateFormat ftf = new SimpleDateFormat("yyyyMMddHHmmss");
		while(true){
			/*logger.info(BusinessWorker.WorkerInfo());*/
			String score=ftf.format(new Date());
			List<String> resultList = RedisUtils.zset_rangeByScore(RedisUtils.ZSET_KEY_BACK_MORE+"robot",0,Double.parseDouble(score));
	    	for (String result : resultList) {
	    		if(StringUtils.isBlank(result)){
	    			RedisUtils.zset_remove(RedisUtils.ZSET_KEY_BACK_MORE+"robot",result);
	    			continue;
	    		}
	    		submit(JSONObject.parseObject(result));
				
				RedisUtils.zset_remove(RedisUtils.ZSET_KEY_BACK_MORE+"robot",result);
			}
	    	try{
				if(RedisUtils.string_exist(RedisUtils.STR_KEY_AI_TASK)){
					if(RedisUtils.string_get(RedisUtils.STR_KEY_AI_TASK).equals("stop")){
						logger.info("..........回调补呼任务退出..........");
						break;
					}
				}else{
					RedisUtils.string_set(RedisUtils.STR_KEY_AI_TASK,"run");
				}
			}catch(Exception e){
				logger.error("ThreadValidCode error:string_exist:", e);
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void submit(JSONObject json){
		BusinessWorker.getExecutor().execute(new Writer(json));
	}
	class Writer implements Runnable {
		
		private JSONObject json;
		
		public Writer(JSONObject json) {
			super();
			this.json = json;
		}

		public JSONObject getJson() {
			return json;
		}
		public void setJson(JSONObject json) {
			this.json = json;
		}

		@Override
		public void run() {
			String callUrl=json.getString("callUrl");
			int times=json.getInteger("times");
			int cbType=json.containsKey("cbType")?json.getInteger("cbType"):0;
			String param=json.containsKey("param")?json.getString("param"):"";
			String jsonArray=json.containsKey("jsonArray")?json.getString("jsonArray"):"";
			String companyId=json.containsKey("companyId")?json.getString("companyId"):"";
			String jsonStr=json.containsKey("jsonStr")?json.getString("jsonStr"):"";
			Map<String, String> paramMap=(Map<String, String>)json.get("paramMap");
			JSONArray jsonStrs = JSONArray.parseArray(jsonStr);
			JSONArray jsonArrays = JSONArray.parseArray(jsonArray);
			
			String logparamter=callUrl+paramMap.toString();
			String httpResults="";
			switch (cbType) {
			case 1:
				logparamter=callUrl+param;
				httpResults = HttpInterface.httpClientGet(callUrl+param,10000,"utf-8",null);
				break;
			case 2:
				logparamter=callUrl+param;
				httpResults = HttpInterface.httpClientGet(callUrl+param,10000,"GBK",null);
				break;
			case 3:
				httpResults = HttpInterface.httpClientPostByBody(callUrl,paramMap,10000,"utf-8");
				break;
			case 4:
				httpResults = HttpInterface.httpClientPostByBody(callUrl,paramMap,10000,"GBK");
				break;
			case 5:
				logparamter=json.toString();
				httpResults = HttpInterface.httpClientPostBody(callUrl,jsonStr,10000,"utf-8");
				break;
			case 6:
				logparamter=json.toString();
				httpResults = HttpInterface.httpClientPostEntity(callUrl,jsonStr,10000,"GBK");
				break;
			default:
				httpResults = HttpInterface.httpClientPostByBody(callUrl,paramMap,30000,"utf-8");
				break;
			}
			if(!httpResults.equals("000")){
				if(times<10){
					times++;
					SimpleDateFormat ftf = new SimpleDateFormat("yyyyMMddHHmmss");
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.MINUTE, times*5); //当前时间加5分钟
					Date date = calendar.getTime();
					String format = ftf.format(date);
					JSONObject json =new JSONObject();
					json.put("callUrl", callUrl);
					json.put("times", times);
					json.put("paramMap", paramMap);
					json.put("cbType", cbType);
					json.put("param", param);
					json.put("jsonStr", jsonStr);
					//TODO 用于修改数据库回调客户状态
					json.put("jsonArray",jsonArray);
					json.put("companyId",companyId);
					RedisUtils.zset_set(RedisUtils.ZSET_KEY_BACK_MORE+"robot", Double.parseDouble(format),json.toString());
					logger.info("回调失败补呼：{}",json.toJSONString());
				}else{
					logger.info("回调补呼失败通知("+logparamter+"),回调【"+times+"次】通知客户返回值:"+httpResults);
				}
			}else{
				//TODO 消息成功，回调客户也成功
				for (int i = 0 ;i < jsonStrs.size(); i++) {
					//遍历回调状态
					JSONObject jsonObject = jsonArrays.getJSONObject(i);
					JSONObject object = jsonStrs.getJSONObject(i);
					if(!jsonObject.getString("batchId").equals(object.getString("bn"))){
						continue;
					}
					String code = object.getString("code");
					String linkId=jsonObject.getString("linkId");//订单唯一id
					String tableName=jsonObject.getString("tableName");
					if("DELIVRD".equals(code)){
						logger.info("回调客户成功,linkId:，{}",linkId);
						//消息正常且回调客户也正常
						//数据库状态同步更新
						String updateSql = String.format("update %s set callback='%s' where link_id='%s';",
								tableName, "success",linkId);
						RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
						RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
					}else {
						logger.info("回调客户失败,linkId:，{}",linkId);
						//消息失败且回调客户正常
						//数据库状态同步更新
						String updateSql = String.format("update %s set callback='%s' where link_id='%s';",
								tableName, "defeat",linkId);
						RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
						RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
					}
				}
				logger.info("回调补呼成功通知("+logparamter+"),回调【"+times+"次】通知客户返回值:"+httpResults);
			}
		}
	}

}
