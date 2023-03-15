package com.xzkj.taskProxy.sms.handler.mo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.util.HttpInterface;
import com.xzkj.taskProxy.util.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HandlerMoCallBack implements Runnable {
	public static Logger logger = LoggerFactory.getLogger("HandlerMoCallBack");

	private String companyId;
	private int total;

	public HandlerMoCallBack(String companyId, int total) throws IOException{
		this.companyId=companyId;
		this.total=total;
	}
	public void run(){
		//long bt = System.currentTimeMillis();

		/*String spJson = RedisUtils.hash_get(RedisUtils.HASH_SP_JSON,seqNo);
		JSONObject jsonOne = JSONObject.parseObject(spJson);
		int cbType = (jsonOne!=null&&jsonOne.containsKey("cbType"))?jsonOne.getIntValue("cbType"):0;
		*/
		int cbType=5;//回调类型 1-2 get  3-4 post form  5-6 post json
		String fifo_back_key = RedisUtils.FIFO_APP_MO_LIST+this.companyId;
		String jsonString=null;
		int size=0;
		//发送列表
		List<JSONObject> jsonList = new Vector<JSONObject>();
		while((jsonString = RedisUtils.fifo_pop(fifo_back_key))!=null){
			size ++;
			if(StringUtils.isNotBlank(jsonString)){
				try{
					JSONObject json = JSONObject.parseObject(jsonString);
					jsonList.add(json);
				}catch(Exception e){
					logger.info("HandlerMoCallBack jsonString【{}】 操作异常:{}",jsonString, e);
					continue;
				}
			}
			if(size>=this.total){
				callback(jsonList,cbType);
				jsonList.clear();
				break;
			}
		}
		// 如果有未提交的
		if (jsonList.size() > 0) {
			callback(jsonList,cbType);
			jsonList.clear();
		}

		String hash_back_key = RedisUtils.HASH_APP_MO_TOTAL;
		RedisUtils.hash_incrBy(hash_back_key, companyId, 0-size);
		return;
	}

	private void callback(List<JSONObject> jsonList,int cbType){
		try {
			Map<String,JSONArray> map = new HashMap<String,JSONArray>();
			JSONArray jsonArr = null;
			//用于回调后，修改回调数据库数据
			JSONArray jsonArray=new JSONArray();
			for (JSONObject json:jsonList) {
				String appName = json.getString("acc");
				String appInfo = RedisUtils.string_get(RedisUtils.STR_KEY_APP_INFO+appName);
				JSONObject appJson = JSONObject.parseObject(appInfo);
				String password = appJson.getString("password");
				String companyId = appJson.getString("company_id");
				String appId = appJson.getString("app_id");
				String backMo = appJson.getString("mo_sync_address");

				String serviceNo = json.getString("serviceNo");
				String Mobile = json.getString("mob");
				String MsgCont = json.getString("msg");
				String MoTime = json.getString("moTime");
				String sendTime =json.containsKey("sendTime")?json.getString("sendTime"):"";
				String tableName = json.getString("tableName");
				long timestamp = new Date().getTime();
				StringBuilder sb = new StringBuilder("acc=");
				sb.append(appName).append("&ts=").append(timestamp).append("|||pwd=").append(password);

				String tokenSign = MD5Utils.MD5Encode(sb.toString());


				JSONObject paramJson = new JSONObject();
				paramJson.put("acc", appName);
				paramJson.put("ts", timestamp);
				paramJson.put("sign", tokenSign);
				paramJson.put("mob", Mobile);
				paramJson.put("serviceNo", serviceNo);
				paramJson.put("msg", MsgCont);
				paramJson.put("moTime", MoTime);
				paramJson.put("sendTime", sendTime);



				jsonArr = map.containsKey(backMo)?map.get(backMo):new JSONArray();
				jsonArr.add(paramJson);
				jsonArray.add(json);
				map.put(backMo,jsonArr);
			}
			Hashtable<String, String> formHeaders =new Hashtable<String, String>();
			formHeaders.put("Content-Type","application/json");
			for(Map.Entry<String,JSONArray> en:map.entrySet()){
				String callUrl = en.getKey();
				if(StringUtils.isBlank(callUrl)){
					continue;
				}
				JSONArray jsonArrs = en.getValue();

				String logparamter="";
				String httpResults="";
				switch (cbType) {
					case 1:
						httpResults = HttpInterface.httpClientGet(callUrl+"param",10000,"utf-8",null);
						break;
					case 2:
						httpResults = HttpInterface.httpClientGet(callUrl+"param",10000,"GBK",null);
						break;
					case 3:
						httpResults = HttpInterface.httpClientPostByBody(callUrl,null,10000,"utf-8");
						break;
					case 4:
						httpResults = HttpInterface.httpClientPostByBody(callUrl,null,10000,"GBK");
						break;
					case 5:
						logger.info("上行回调数据：{}",jsonArr.toString());
						logparamter=jsonArrs.toString();
						httpResults = HttpInterface.httpClientPost(callUrl,jsonArrs.toString(),10000,"utf-8",formHeaders);
						break;
					case 6:
						logparamter=jsonArrs.toString();
						httpResults = HttpInterface.httpClientPost(callUrl,jsonArrs.toString(),10000,"GBK",formHeaders);
						break;
					default:
						logparamter=jsonArrs.toString();
						httpResults = HttpInterface.httpClientPost(callUrl,jsonArrs.toString(),10000,"utf-8",formHeaders);
				}
				logger.info("回调客户response：{}",httpResults);
				//回调失败之后补充回调
				if(!httpResults.equals("000")){
					SimpleDateFormat ftf = new SimpleDateFormat("yyyyMMddHHmmss");
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.MINUTE, 3); //当前时间加3分钟
					Date date = calendar.getTime();
					String format = ftf.format(date);
					JSONObject jsonMore =new JSONObject();
					jsonMore.put("callUrl", callUrl);
					jsonMore.put("times", 1);
					jsonMore.put("paramMap", null);
					jsonMore.put("param", "param");
					jsonMore.put("cbType", cbType);
					jsonMore.put("jsonStr", jsonArrs.toString());
					// 用于修改数据库回调客户状态
					jsonMore.put("jsonArray",jsonArray.toJSONString());
					jsonMore.put("companyId",companyId);
					RedisUtils.zset_set(RedisUtils.ZSET_KEY_BACK_MORE+"moRobot", Double.parseDouble(format),jsonMore.toString());
					logger.info("回调失败补呼中：{}",jsonMore.toJSONString());
				}else{
					for (int i = 0 ;i < jsonArrs.size(); i++) {
						//遍历回调状态
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						String tableName = jsonObject.getString("tableName");
						String moId = jsonObject.getString("moId");
						//数据库状态同步更新

						String updateSql = String.format("update %s set status=%s where mo_id='%s';",tableName,1,moId);
						RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
						RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
					}
				}

			}

		}catch (Exception e1) {
			logger.info("HandlerMoCallBack线程内：外呼回调通知异常：{}",e1.getMessage());
			e1.printStackTrace();
		}
	}


}