package com.xzkj.taskPorxy.sms.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONArray;

import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.util.HttpInterface;
import com.xzkj.taskPorxy.util.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class HandlerCallBack implements Runnable {
	//TODO 修改记录的回调状态
					/*
						回调状态：默认0：表示消息成功与否等待状态
									1：表示消息成功了，回调给客户也成功了（完整逻辑）
									2：表示消息失败了，回调客户成功了
									3：消息成功了，回调客户失败了
									4：消息失败了，回调客户也失败了
					 */
public static Logger logger = LoggerFactory.getLogger("HandlerCallBack");
	
	private String companyId;
	private int total;

	public HandlerCallBack(String companyId,int total) throws IOException{
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
		String fifo_back_key = RedisUtils.FIFO_MMS_MT_CLIENT+this.companyId;
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
					logger.info("HandlerCallBack jsonString【{}】 操作异常:{}",jsonString, e);
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

		String hash_back_key = RedisUtils.HASH_MMS_MT_COUNT;
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
				String appName = json.getString("appName");
				String password = json.getString("password");
				String batchId = json.getString("batchId");
				String mmsId = json.getString("mmsId");
				String srcNum = json.getString("srcNum");
				String mobile = json.getString("mobile");
				String reportStatus = json.getString("reportStatus");
				String info = json.getString("info");
				String reportTime = json.getString("reportTime");
				String data = json.getString("data");
				String url = json.getString("url");
				try {
					info = URLEncoder.encode(info,"utf-8");
				} catch (UnsupportedEncodingException e) {
				}
				StringBuilder sb = new StringBuilder("acc=");
				sb.append(appName).append("&mid=").append(mmsId).append("&mob=").append(mobile)
						.append("&bn=").append(batchId).append("|||pwd=").append(password);
				String token = MD5Utils.MD5Encode(sb.toString());

				JSONObject paramJson = new JSONObject();
				paramJson.put("acc", appName);
				paramJson.put("bn", batchId);
				paramJson.put("mid", mmsId);
				paramJson.put("src", srcNum);
				paramJson.put("mob", mobile);
				paramJson.put("code", reportStatus);//DELIVRD为成功，其他请参考错误码说明。
				paramJson.put("msg", info);
				paramJson.put("time", reportTime);
				paramJson.put("down", "");//下载时间
				paramJson.put("data", data);
				paramJson.put("token", token);

				jsonArr = map.containsKey(url)?map.get(url):new JSONArray();
				jsonArr.add(paramJson);
				jsonArray.add(json);
				map.put(url,jsonArr);
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
					//TODO 用于修改数据库回调客户状态
					jsonMore.put("jsonArray",jsonArray);
					jsonMore.put("companyId",companyId);
					RedisUtils.zset_set(RedisUtils.ZSET_KEY_BACK_MORE+"robot", Double.parseDouble(format),jsonMore.toString());
					logger.info("回调失败补呼：{}",jsonMore.toJSONString());
				}else{
					//TODO 消息成功，回调客户也成功
					for (int i = 0 ;i < jsonArrs.size(); i++) {
						//遍历回调状态
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						JSONObject object = jsonArrs.getJSONObject(i);
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
				}
				logger.info("HandlerCallBack线程内：外呼回调通知地址({})参数({})，cbType={},回调通知客户返回值:{}",callUrl,logparamter,cbType,httpResults);

			}

		}catch (Exception e1) {
			logger.info("HandlerCallBack线程内：外呼回调通知异常：{}",e1.getMessage());
			e1.printStackTrace();
		}
	}

	
}