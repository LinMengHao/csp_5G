package com.xzkj.taskProxy.sms.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.service.ICommonService;
import com.xzkj.taskProxy.util.SpringBeanUtil;
import com.xzkj.taskProxy.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerAppBase implements Runnable {

public static Logger logger = LoggerFactory.getLogger("HandlerAppBase");

	public static ICommonService commonServiceImpl = (ICommonService) SpringBeanUtil.getBean(ICommonService.class);

	public HandlerAppBase(){}

	public void run(){
		try{
			//用户信息列表
			List<Map<String, Object>> mapList = commonServiceImpl.appInfoList();
			for (Map<String, Object> map: mapList) {
				String id = map.containsKey("id")?map.get("id").toString():"0";
				String app_name = map.containsKey("app_name")?map.get("app_name").toString():"";
				String password = map.containsKey("password")?map.get("password").toString():"";
				String status = map.containsKey("status")?map.get("status").toString():"normal";
				String product_id = map.containsKey("product_id")?map.get("product_id").toString():"0";
				String limit_count = map.containsKey("limit_count")?map.get("limit_count").toString():"0";
				String rpt_sync_model = map.containsKey("rpt_sync_model")?map.get("rpt_sync_model").toString():"push";
				String rpt_model_address = map.containsKey("rpt_model_address")?map.get("rpt_model_address").toString():"";
				String rpt_sign_address = map.containsKey("rpt_sign_address")?map.get("rpt_sign_address").toString():"";
				String rpt_sync_address = map.containsKey("rpt_sync_address")?map.get("rpt_sync_address").toString():"";
				String mo_sync_model = map.containsKey("mo_sync_model")?map.get("mo_sync_model").toString():"push";
				String mo_sync_address = map.containsKey("mo_sync_address")?map.get("mo_sync_address").toString():"";
				String auth_ip = map.containsKey("auth_ip")?map.get("auth_ip").toString():"";
				String company_id = map.containsKey("company_id")?map.get("company_id").toString():"0";
				String push_report_format = map.containsKey("push_report_format")?map.get("push_report_format").toString():"";
				String start_time = map.containsKey("start_time")?map.get("start_time").toString():"0000";
				String end_time = map.containsKey("end_time")?map.get("end_time").toString():"2400";
				String payment = map.containsKey("payment")?map.get("payment").toString():"advance";
				String black_levels = map.containsKey("black_levels")?map.get("black_levels").toString():"0";
				if(StringUtils.isBlank(black_levels)){
					black_levels="0";
				}
				JSONObject json = new JSONObject();
				json.put("app_id",id);
				json.put("app_name",app_name);
				json.put("password",password);
				json.put("status",status);
				json.put("product_id",product_id);
				json.put("limit_count",Long.valueOf(limit_count));
				json.put("rpt_sync_model",rpt_sync_model);
				json.put("rpt_model_address",rpt_model_address);
				json.put("rpt_sign_address",rpt_sign_address);
				json.put("rpt_sync_address",rpt_sync_address);
				json.put("mo_sync_model",mo_sync_model);
				json.put("mo_sync_address",mo_sync_address);
				json.put("auth_ip",auth_ip);
				json.put("company_id",company_id);
				json.put("push_report_format",push_report_format);
				json.put("start_time",start_time);
				json.put("end_time",end_time);
				json.put("payment",payment);
				json.put("group_id",black_levels);//黑名单组id

				JSONObject jsonGroup = new JSONObject();
				List<Map<String, Object>> blackGroup = commonServiceImpl.blactGroupInfo(black_levels);
				if(blackGroup!=null&& blackGroup.size()!=0){
					String repeat_day = blackGroup.get(0).containsKey("repeat_day")?blackGroup.get(0).get("repeat_day").toString():"0";
					String repeat_count = blackGroup.get(0).containsKey("repeat_count")?blackGroup.get(0).get("repeat_count").toString():"0";
					String black_count = blackGroup.get(0).containsKey("black_count")?blackGroup.get(0).get("black_count").toString():"0";
					String black_rate = blackGroup.get(0).containsKey("black_rate")?blackGroup.get(0).get("black_rate").toString():"0";
					jsonGroup.put("repeatDay",repeat_day);
					jsonGroup.put("repeatCount",repeat_count);
					jsonGroup.put("blackCount",black_count);
					jsonGroup.put("blackRate",black_rate);
				}
				json.put("groupJson",jsonGroup);
				List<Map<String, Object>> blackList = commonServiceImpl.blactRelateList(black_levels);
				JSONArray jsonArray = new JSONArray();
				if(blackList!=null&& blackList.size()!=0){
					for (Map<String, Object> mapBlack: blackList) {
						//黑名单渠道 local-自有黑名单，JSMX-江苏梦翔，ZJLJ-浙江棱镜，DYDX-东云短信，DYYY-东云语音
						String channel = mapBlack.containsKey("channel")?mapBlack.get("channel").toString():"other";
						//高危1-选中2-未选中
						String riskHigh = mapBlack.containsKey("risk_high")?mapBlack.get("risk_high").toString():"1";
						String riskMedium = mapBlack.containsKey("risk_medium")?mapBlack.get("risk_medium").toString():"1";
						String riskLow = mapBlack.containsKey("risk_low")?mapBlack.get("risk_low").toString():"1";
						String riskPrivate = mapBlack.containsKey("risk_private")?mapBlack.get("risk_private").toString():"1";
						String priority = mapBlack.containsKey("priority")?mapBlack.get("priority").toString():"0";
						String filterRate = mapBlack.containsKey("filter_rate")?mapBlack.get("filter_rate").toString():"0";
						JSONObject blackJson = new JSONObject();
						blackJson.put("channel",channel);
						blackJson.put("riskHigh",Integer.parseInt(riskHigh));
						blackJson.put("riskMedium",Integer.parseInt(riskMedium));
						blackJson.put("riskLow",Integer.parseInt(riskLow));
						blackJson.put("riskPrivate",Integer.parseInt(riskPrivate));
						blackJson.put("priority",Integer.parseInt(priority));
						blackJson.put("filterRate",Double.valueOf(filterRate));
						jsonArray.add(blackJson);
					}
				}
				json.put("blackGroup",jsonArray);
				//更新余额
				//RedisUtils.hash_set(RedisUtils.HASH_LOGIC_ACC_BALANCE, app_name,limit_count);
				//更新用户信息
				RedisUtils.string_set(RedisUtils.STR_KEY_APP_INFO+app_name,json.toString());
			}

			//TODO 模板信息列表（新表过渡）
//			List<Map<String, Object>> modelList = commonServiceImpl.modelInfoList();
//			for (Map<String, Object> map:modelList) {
//				String model_id = map.containsKey("model_id")?map.get("model_id").toString():"";
//				String app_id = map.containsKey("app_id")?map.get("app_id").toString():"0";
//				String sign_name = map.containsKey("sign_id")?map.get("sign_id").toString():"";
//				String title = map.containsKey("title")?map.get("title").toString():"";
//
//				sign_name=sign_name.replaceAll("【","").replaceAll("】","");
//
//				JSONObject json = new JSONObject();
//				json.put("title",title);
//				json.put("sign_name",sign_name);
//				//更新模板
//				RedisUtils.hash_set(RedisUtils.HASH_APP_MODEL_SIGN+app_id, model_id,json.toJSONString());
//			}
			List<Map<String, Object>> modelNewList = commonServiceImpl.modelInfoNewList();
			for (Map<String, Object> map:modelNewList) {
				String model_id = map.containsKey("model_id")?map.get("model_id").toString():"";
				String app_id = map.containsKey("app_id")?map.get("app_id").toString():"0";
				String sign_id = map.containsKey("sign_id")?map.get("sign_id").toString():"";
				Map<String, Object> sign=commonServiceImpl.signInfo(sign_id);
				String sign_name=sign.containsKey("report_sign_content")?sign.get("report_sign_content").toString():"";
				String title = map.containsKey("title")?map.get("title").toString():"";

				sign_name=sign_name.replaceAll("【","").replaceAll("】","");

				JSONObject json = new JSONObject();
				json.put("title",title);
				json.put("sign_name",sign_name);
				json.put("sign_id",sign_id);
				//更新模板
				RedisUtils.hash_set(RedisUtils.HASH_APP_MODEL_SIGN+app_id, model_id,json.toJSONString());
			}

			//通道信息列表
			List<Map<String, Object>> channelList = commonServiceImpl.channelInfoList();
			for (Map<String, Object> map:channelList) {
				String channel_id = map.containsKey("id")?map.get("id").toString():"";
				//String channel_id = map.containsKey("channel_id")?map.get("channel_id").toString():"";
				String link_max = map.containsKey("link_max")?map.get("link_max").toString():"0";
				String model = map.containsKey("model")?map.get("model").toString():"";
				String variant = map.containsKey("variant")?map.get("variant").toString():"";
				String svc_addr = map.containsKey("svc_addr")?map.get("svc_addr").toString():"";
				String link_speed = map.containsKey("link_speed")?map.get("link_speed").toString():"0";
				String account = map.containsKey("account")?map.get("account").toString():"";
				String password = map.containsKey("password")?map.get("password").toString():"";
				String extras = map.containsKey("extras")?map.get("extras").toString():"";
				String service_code = map.containsKey("service_code")?map.get("service_code").toString():"";
				String source_ment = map.containsKey("source_ment")?map.get("source_ment").toString():"";
				String enterprise_code = map.containsKey("enterprise_code")?map.get("enterprise_code").toString():"";
				String token = map.containsKey("token")?map.get("token").toString():"";
				String ignore_province = map.containsKey("ignore_province")?map.get("ignore_province").toString():"";
				String timeout_count = map.containsKey("timeout_count")?map.get("timeout_count").toString():"-1";

				JSONObject json = new JSONObject();
				json.put("channel_id",channel_id);
				json.put("link_max",link_max);//最大连接数
				//json.put("model",model);//每个模块对应线程,存放形式:模块号:线程数,模块号:线程数
				//json.put("variant",variant);//lei ming ying she
				json.put("svc_addr",svc_addr);
				json.put("link_speed",link_speed);//每连接最大发送速度(条/秒)
				json.put("account",account);
				json.put("password",password);
				json.put("extras",extras);
				json.put("service_code",service_code);
				json.put("source_ment",source_ment);
				json.put("enterprise_code",enterprise_code);
				json.put("token",token);
				json.put("ignore_province",ignore_province);//忽略省份
				json.put("timeout_count",timeout_count);//数字字段，表示每条出现多少条超时，通道暂停
				//更新通道
				RedisUtils.hash_set(RedisUtils.HASH_CHANNEL_INFO+"mms", channel_id,json.toJSONString());
			}

			//路由信息列表
			Map<String,List<String>> routeMap = new HashMap<String,List<String>>();
			Map<String,String> routeMap2 = new HashMap<String,String>();
			List<Map<String, Object>> routeBaseList = commonServiceImpl.routeBaseList();
			for (Map<String, Object> map:routeBaseList) {
				String id = map.containsKey("id")?map.get("id").toString():"0";
				String company_id = map.containsKey("company_id")?map.get("company_id").toString():"0";
				String app_id = map.containsKey("app_id")?map.get("app_id").toString():"0";
				String to_cmcc = map.containsKey("to_cmcc")?map.get("to_cmcc").toString():"";
				String to_unicom = map.containsKey("to_unicom")?map.get("to_unicom").toString():"";
				String to_telecom = map.containsKey("to_telecom")?map.get("to_telecom").toString():"";
				String to_international = map.containsKey("to_international")?map.get("to_international").toString():"";
				String sign_name = map.containsKey("sign_name")?map.get("sign_name").toString():"";
				String province = map.containsKey("province")?map.get("province").toString():"";
				String priority = map.containsKey("priority")?map.get("priority").toString():"1";
				String disprate = map.containsKey("disprate")?map.get("disprate").toString():"0";
				String channel_id = map.containsKey("channel_id")?map.get("channel_id").toString():"0";
				String channel_limit = map.containsKey("channel_limit")?map.get("channel_limit").toString():"";
				String channel_caller = map.containsKey("channel_caller")?map.get("channel_caller").toString():"";
				String status = map.containsKey("status")?map.get("status").toString():"1";

				JSONObject json = new JSONObject();
				json.put("route_id",id);
				json.put("company_id",company_id);
				json.put("app_id",app_id);
				json.put("to_cmcc",to_cmcc);
				json.put("to_unicom",to_unicom);
				json.put("to_telecom",to_telecom);
				json.put("to_international",to_international);
				json.put("sign_name",sign_name);
				json.put("province",province);
				json.put("priority",priority);
				json.put("disprate",disprate);
				json.put("channel_id",channel_id);//
				json.put("channel_limit",channel_limit);
				json.put("channel_caller",channel_caller);
				json.put("status",status);

				List<String> list=null;
				if(!routeMap.containsKey(company_id+":"+app_id)){
					list = RedisUtils.zset_range(RedisUtils.ZSET_ROUTE_BASE+company_id+":"+app_id,0,-1);
					routeMap.put(company_id+":"+app_id,list);
				}else{
					list = routeMap.get(company_id+":"+app_id);
				}

				for(String value:list){
					if(value.equals(json.toJSONString())){
						routeMap2.put(value,"");
					}
				}
				//更新路由
				String routeKey = RedisUtils.ZSET_ROUTE_BASE+company_id+":"+app_id;
				RedisUtils.zset_set(routeKey, Integer.parseInt(priority),json.toJSONString());
			}
			for (Map.Entry<String,List<String>> en: routeMap.entrySet()) {
				String key = en.getKey();
				List<String> valueList = en.getValue();
				if(valueList!=null&&valueList.size()!=0){
					for(String value:valueList){
						if(routeMap2.containsKey(value)){
							continue;
						}
						RedisUtils.zset_remove(RedisUtils.ZSET_ROUTE_BASE+key,value);
					}
				}
			}
			//模板关系映射
			Map<String,String> relateMap = new HashMap<String,String>();
			String selectSql = "select * from e_model_related where status=1 ;";
			List<Map<String, Object>> relatedList = commonServiceImpl.commonInfoList(selectSql);
			for (Map<String, Object> map:relatedList) {
				String model_id = map.containsKey("model_id") ? map.get("model_id").toString() : "";
				String channel_id = map.containsKey("channel_id") ? map.get("channel_id").toString() : "0";
				String channel_model_id = map.containsKey("channel_model_id") ? map.get("channel_model_id").toString() : "";
				String limit_count = map.containsKey("limit_count") ? map.get("limit_count").toString() : "0";
				//变量参数映射key1=value1&key2=value2&客户变量key=通道变量key
				String param_ext = map.containsKey("param_ext") ? map.get("param_ext").toString() : "";

				JSONObject json = new JSONObject();
				json.put("limit_count",limit_count);
				json.put("param_ext",param_ext);
				if(!relateMap.containsKey(model_id)){
					RedisUtils.hash_del(RedisUtils.HASH_MODEL_RELATED+model_id);
					relateMap.put(model_id,model_id);
				}
				//更新模板映射关系
				RedisUtils.hash_set(RedisUtils.HASH_MODEL_RELATED+model_id,channel_id+"_"+channel_model_id,json.toJSONString());
			}
			relateMap.clear();
			relateMap=null;
			//初始化号段表进缓存
			/*List<Map<String, Object>> segmentList = commonServiceImpl.segmentList();
			for (Map<String, Object> map:segmentList) {
				String cc_mobile = map.containsKey("cc_mobile")?map.get("cc_mobile").toString():"0";
				String city_id = map.containsKey("city_id")?map.get("city_id").toString():"0";
				String city_name = map.containsKey("city_name")?map.get("city_name").toString():"0";
				String province_id = map.containsKey("province_id")?map.get("province_id").toString():"0";
				String province_name = map.containsKey("province_name")?map.get("province_name").toString():"0";
				String cc_provider = map.containsKey("cc_provider")?map.get("cc_provider").toString():"0";

				String value = province_id+"_"+province_name+"_"+city_id+"_"+city_name+"_"+cc_provider;

				//更新号段
				RedisUtils.hash_set(RedisUtils.HASH_MOBILE_SEGMENT,cc_mobile,value);
			}*/
		}catch(Exception e){
			logger.error("HandlerAppBase 查询用户信息异常:"+ e);
			e.printStackTrace();
		}
    }
}