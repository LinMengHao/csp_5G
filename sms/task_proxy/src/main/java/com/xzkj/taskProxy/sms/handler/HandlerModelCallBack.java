package com.xzkj.taskProxy.sms.handler;

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

public class HandlerModelCallBack implements Runnable {
	//TODO 修改记录的回调状态
					/*
						回调状态：默认0：表示消息成功与否等待状态
									1：表示消息成功了，回调给客户也成功了（完整逻辑）
									2：表示消息失败了，回调客户成功了
									3：消息成功了，回调客户失败了
									4：消息失败了，回调客户也失败了
					 */
public static Logger logger = LoggerFactory.getLogger("HandlerModelCallBack");

	private String companyId;
	private int total;

	public HandlerModelCallBack(String companyId, int total) throws IOException{
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
		String fifo_back_key = RedisUtils.FIFO_MODEL_MT_CLIENT+this.companyId;
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
					logger.info("HandlerModelCallBack jsonString【{}】 操作异常:{}",jsonString, e);
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

		String hash_back_key = RedisUtils.HASH_MODEL_MT_COUNT;
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
				String appInfo = RedisUtils.string_get(RedisUtils.STR_KEY_APP_INFO+appName);
				JSONObject appJson = JSONObject.parseObject(appInfo);
				String password = appJson.getString("password");
				String companyId = appJson.getString("company_id");
				String appId = appJson.getString("app_id");
				String backModel = appJson.getString("rpt_model_address");
//				String password = json.getString("password");
				String title = json.getString("title");
				String modelId = json.getString("modelId");
				String sign = json.getString("sign");
				String variate = json.getString("variate");
				String status = json.getString("status");
				String info = json.getString("info");
				String backUrl = json.containsKey("backUrl")?json.getString("backUrl"):"";
				String toCmcc = json.getString("toCmcc");
				String toUnicom = json.getString("toUnicom");
				String toTelecom = json.getString("toTelecom");
//				try {
//					title = URLEncoder.encode(title,"utf-8");
//				} catch (UnsupportedEncodingException e) {
//				}
				StringBuilder sb = new StringBuilder("acc=");
				sb.append(appName).append("&mid=").append(modelId).append("&sign=").append(sign)
						.append("&status=").append(status).append("|||pwd=").append(password);
				String token = MD5Utils.MD5Encode(sb.toString());

				JSONObject paramJson = new JSONObject();
				paramJson.put("acc", appName);
				paramJson.put("mid", modelId);
				paramJson.put("sign", sign);
				paramJson.put("title", title);
				paramJson.put("var", variate);
				paramJson.put("status", status);//DELIVRD为成功，其他请参考错误码说明。
				paramJson.put("info", info);
				paramJson.put("cmcc", toCmcc);
				paramJson.put("unicom", toUnicom);
				paramJson.put("telecom", toTelecom);
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = format.format(new Date());
				paramJson.put("time", time);
				paramJson.put("token", token);

				jsonArr = map.containsKey(backModel)?map.get(backModel):new JSONArray();
				jsonArr.add(paramJson);
				jsonArray.add(json);
				map.put(backModel,jsonArr);
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
					// 用于修改数据库回调客户状态
					jsonMore.put("jsonArray",jsonArray.toJSONString());
					jsonMore.put("companyId",companyId);
					RedisUtils.zset_set(RedisUtils.ZSET_KEY_BACK_MORE+"modelRobot", Double.parseDouble(format),jsonMore.toString());
					logger.info("回调失败补呼：{}",jsonMore.toJSONString());
				}else{
					for (int i = 0 ;i < jsonArrs.size(); i++) {
						//遍历回调状态
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						String modelId = jsonObject.containsKey("modelId")?jsonObject.getString("modelId"):"";
						String id = jsonObject.containsKey("id")?jsonObject.getString("id"):"";
						Long status = jsonObject.containsKey("status")?jsonObject.getLong("status"):2;
						String info = jsonObject.containsKey("info")?jsonObject.getString("info"):"审核失败";
						info=info+",回调成功";
						//数据库状态同步更新
						//修改模版审核状态
						String updateSql = String.format("update e_model_info_new set status='%s',info='%s' where id='%s';",status,info,id);
						RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
						RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
					}
				}
				logger.info("HandlerModelCallBack线程内：外呼回调通知地址({})参数({})，cbType={},回调通知客户返回值:{}",callUrl,logparamter,cbType,httpResults);

			}

		}catch (Exception e1) {
			logger.info("HandlerModelCallBack线程内：外呼回调通知异常：{}",e1.getMessage());
			e1.printStackTrace();
		}
	}

	
}