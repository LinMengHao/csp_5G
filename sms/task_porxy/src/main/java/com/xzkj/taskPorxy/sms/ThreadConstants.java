package com.xzkj.taskPorxy.sms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskPorxy.conf.GlobalBlackFilterConfig;
import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.util.HttpInterface;
import com.xzkj.taskPorxy.util.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.xzkj.taskPorxy.conf.GlobalBlackFilterConfig.*;


public class ThreadConstants {
	public static Logger logger = LoggerFactory.getLogger("ThreadConstants");
	public static void main(String[] args) {
		String allChannelNos="";
		
		System.out.println("-------------");
		System.out.println(allChannelNos.replaceAll("egg",""));
		System.out.println("-------------");
	}
	public static List<String> getRouteList(String routeKey) {
		List<Double> scores = new ArrayList<Double>();
		List<String> members = new ArrayList<String>();
		RedisUtils.zset_revrangeWithScore(routeKey, 0, -1, scores, members);
		List<String> routes = new ArrayList<String>();
		int length=scores.size();
		if(length==0){
			return routes;
		}
		double prfDou=scores.get(0);
		String member="|"+members.get(0);
		for (int i=1;i<length;i++) {
			if(prfDou==scores.get(i).doubleValue()){
				member+="|"+members.get(i);
			}else{
				routes.add(member.substring(1));
				member="|"+members.get(i);
				prfDou=scores.get(i);
			}
		}
		routes.add(member.substring(1));
		return routes;
	}
	/**
	 * 新路由预处理
	 * @param companyId 公司id
	 * @param appId 用户id
	 * @param provider //运营商 1-电信2-联通3-移动4-国际0-所有
	 * @param province 省份 
	 * @param route 路由组
	 * @return
	 */
	public static JSONObject disRoutePretreat(String companyId, String appId, int provider,String signName, String province,String today,String route,Map<String, String> relatedMap,String param){
		JSONObject jsonPre = null;
		double dispratePre=0;
		long countPre = 0L;
		int routeIdPre =0;
		String channelIdPre="";
		String routeCountKey = RedisUtils.HASH_ROUTE_DISPENSE+companyId+":"+appId+":"+today;
		String[] routArr = route.split("\\|");
		for (String routeOne : routArr) {
			JSONObject routejson = JSONObject.parseObject(routeOne);

			//路由json串：route_id,to_cmcc，to_unicom，to_telecom，to_international，sign_name，province，disprate，channel_id，channel_limit，channel_caller
			int routeId = routejson.getInteger("route_id");//路由id
			String to_cmcc = routejson.getString("to_cmcc");//移动，yes，no
			String to_unicom = routejson.getString("to_unicom");//联通，yes，no
			String to_telecom = routejson.getString("to_telecom");//电信，yes，no
			String to_international = routejson.getString("to_international");//国际，yes，no
			String sign_name = routejson.getString("sign_name");//签名
			String routeProvince = routejson.getString("province");//省份，多个以半角逗号分隔
			double disprate = routejson.getDouble("disprate");//分发比例
			String channel_id = routejson.getString("channel_id");//渠道id
			int channel_limit = routejson.getInteger("channel_limit");//渠道限制发送数
			String channel_caller = routejson.getString("channel_caller");//渠道主叫

			if(disprate==0){
				continue;
			}
			if(StringUtils.isNotBlank(sign_name)&&sign_name.indexOf(signName)==-1){
				continue;
			}
			//运营商 1-电信2-联通3-移动4-国际0-所有
			if(provider==1&&to_telecom.equals("no")){
				continue;
			}if(provider==2&&to_unicom.equals("no")){
				continue;
			}if(provider==3&&to_cmcc.equals("no")){
				continue;
			}if(provider==4&&to_international.equals("no")){
				continue;
			}
			if(StringUtils.isNotBlank(routeProvince)){
				if(StringUtils.isBlank(province)){
					continue;
				}
				if(routeProvince.indexOf(province)==-1){
					continue;
				}
			}
			String countStr = RedisUtils.hash_get(routeCountKey,routeId+"_"+channel_id);
			long count = Long.parseLong(StringUtils.isNotBlank(countStr)?countStr:"0");
			if(count>=channel_limit){
				continue;
			}

			//模板映射关系
			boolean flag=false;
			String channelModelId="";
			String channelParam="";
			String channelModelKey = RedisUtils.HASH_CHANNEL_MODEL_LIMIT+today+":"+channel_id;
			for(Map.Entry<String, String> entry:relatedMap.entrySet()){
				String[] keyArr = entry.getKey().split("_");
				if(!keyArr[0].equals(channel_id)){
					continue;
				}
				JSONObject relateJson = JSONObject.parseObject(entry.getValue());
				long limitCount = relateJson.getLong("limit_count");//通道模板限制数
				String param_ext = relateJson.getString("param_ext");

				String total = RedisUtils.hash_get(channelModelKey,keyArr[1]);
				long modelTotal = StringUtils.isNotBlank(total)?Long.parseLong(total):0L;
				if(modelTotal>=limitCount){
					flag = true;
					continue;
				}
				flag=false;
				channelModelId = keyArr[1];
				if(StringUtils.isNotBlank(param_ext)){
					String[] paramExtArr=param_ext.split("&");
					for (String paramExt:paramExtArr){
						String[] extArr = paramExt.split("=");
						channelParam=param.replace(extArr[0]+"=",extArr[1]+"=");
					}
				}
			}
			if(flag){
				continue;
			}

			if(dispratePre==0){
				routejson.put("channel_model_id",channelModelId);
				routejson.put("channel_param",channelParam);
				dispratePre=disprate;
				countPre = count;
				jsonPre = routejson;
				routeIdPre = routeId;
				channelIdPre = channel_id;
				continue;
			}
			if(count/disprate<countPre/dispratePre){
				routejson.put("channel_model_id",channelModelId);
				routejson.put("channel_param",channelParam);
				dispratePre=disprate;
				countPre = count;
				jsonPre = routejson;
				routeIdPre = routeId;
				channelIdPre = channel_id;
				continue;
			}
			if(count/disprate==countPre/dispratePre&&disprate>dispratePre){
				routejson.put("channel_model_id",channelModelId);
				routejson.put("channel_param",channelParam);
				dispratePre=disprate;
				countPre = count;
				jsonPre = routejson;
				routeIdPre = routeId;
				channelIdPre = channel_id;
				continue;
			}
		}
		if(routeIdPre!=0){
			RedisUtils.hash_incrBy(routeCountKey,routeIdPre+"_"+channelIdPre,1);
		}
		return jsonPre;
	}
	/**
	 * 补呼路由预处理
	 * @param seqNo 商户编号
	 * @param itemNum 项目编号
	 * @param allChannelNos 呼叫过的渠道编号，半角逗号分隔
	 * @param channelNo 上次呼叫失败的渠道编号
	 * @param sip_code 上次呼叫失败的sip失败码
	 * @param provider 运营商 1-电信2-联通3-移动4-国际0-所有
	 * @param province 省份 
	 * @param route 路由组
	 * @return
	 */
	public static JSONObject disRecallRoutePretreat(String seqNo,String itemNum,String allChannelNos,String channelNo,String sip_code,int provider,String province,String route){
		JSONObject jsonPre = null;
		long dispratePre=0L;
		long countPre = 0L;
		int routeIdPre =0;
		String channelNoPre="";
		String routeCountKey = RedisUtils.HASH_ROUTE_RECALL_DISPENSE+seqNo+":"+itemNum;
		String[] routArr = route.split("\\|");
		for (String routeOne : routArr) {
			JSONObject routejson = JSONObject.parseObject(routeOne);
			//补呼路由json串：route_id,failure_code，to_cmcc，to_unicom，to_telecom，to_international，sign_name，province，disprate，channel_id，channel_limit，channel_caller

			String channelNofst = routejson.getString("channelNo");//原始渠道编号
			String channelRecall = routejson.getString("channelRecall");//补呼渠道编号
			String failureCode = routejson.getString("failureCode");//补呼失败码，多个以半角逗号分开
			long disprate = routejson.getLong("disprate");//分发比例
			int routeId = routejson.getInteger("routeId");//路由id
			int routeProvider = routejson.getInteger("provider");//运营商 1-电信2-联通3-移动4-国际0-所有
			String routeProvince = routejson.getString("province");//省份，多个以半角逗号分隔
			if(disprate==0){
				continue;
			}
			if(routeProvider!=0&&routeProvider!=provider){
				continue;
			}
			String caller = routejson.getString("caller");//渠道主叫号码
			if(allChannelNos.indexOf("egg"+caller+channelRecall)!=-1){
				continue;//该通道发送过
			}
			if(StringUtils.isNotBlank(channelNofst)&&!channelNofst.equals(channelNo)){
				continue;//发送通道不匹配
			}
			if(StringUtils.isNotBlank(failureCode)&&failureCode.indexOf(sip_code)==-1){
				continue;//失败码规则不匹配
			}
			if(StringUtils.isNotBlank(routeProvince)){
				if(StringUtils.isBlank(province)){
					continue;
				}
				if(routeProvince.indexOf(province)==-1){
					continue;
				}
				routejson.put("prefixCalled", province.substring(1));
			}
			String countStr = RedisUtils.hash_get(routeCountKey,routeId+"_"+channelRecall);
			long count = Long.parseLong(StringUtils.isNotBlank(countStr)?countStr:"0");
			if(dispratePre==0){
				dispratePre=disprate;
				countPre = count;
				jsonPre = routejson;
				routeIdPre = routeId;
				channelNoPre = channelRecall;
				continue;
			}
			if(count/disprate<countPre/dispratePre){
				dispratePre=disprate;
				countPre = count;
				jsonPre = routejson;
				routeIdPre = routeId;
				channelNoPre = channelRecall;
				continue;
			}
			if(count/disprate==countPre/dispratePre&&disprate>dispratePre){
				dispratePre=disprate;
				countPre = count;
				jsonPre = routejson;
				routeIdPre = routeId;
				channelNoPre = channelRecall;
				continue;
			}
		}
		if(routeIdPre!=0){
			RedisUtils.hash_incrBy(routeCountKey,routeIdPre+"_"+channelNoPre,1);
		}
		return jsonPre;
	}
	
	/**
	 * 路由选择
	 * @param seqNo 商户编号
	 * @param itemNum 项目编号
	 * @param callCount 发送数量
	 * @return
	 */
	public static Map<String, JSONObject> getBestRoute(String seqNo,String itemNum,int callCount){
		//路由配置--分发比例
		String routeListKey = RedisUtils.HASH_CUR_ROUTE_ALL+seqNo+":"+itemNum;
		Map<String, String> mapResult = RedisUtils.hash_getFields(routeListKey);
		if(mapResult==null||mapResult.isEmpty()){
			return new HashMap<String, JSONObject>();
		}
		//路由配置--分发数量
		String routeCountKey = RedisUtils.HASH_ROUTE_DISPENSE+seqNo+":"+itemNum;
		Map<String, String> mapCount = RedisUtils.hash_getFields(routeCountKey);
		
		for (Map.Entry<String,String> men : mapResult.entrySet()) {
			String keyNo = men.getKey();
			String valueJson = men.getValue();
			JSONObject countJson = JSONObject.parseObject(valueJson);
			if(mapCount!=null&&mapCount.containsKey(keyNo)){
				int hisCount = Integer.parseInt(mapCount.get(keyNo).toString()) ;
				countJson.put("hisCount", hisCount);
			}else{
				countJson.put("hisCount", 0);
			}
			countJson.put("curCount", 0);
			mapResult.put(keyNo, countJson.toString());
		}
		for(int i=0;i<callCount;i++){
			String bestKey="";
			String bestChannelNo="";
			String bestVosGw="";
			double bestDispRate=0;
			int bestHisCount=0;
			int bestCurCount=0;
			
			for (Map.Entry<String,String> men : mapResult.entrySet()) {
				String keyNo = men.getKey();
				String valueJson = men.getValue();
				JSONObject countJson = JSONObject.parseObject(valueJson);
				double dispRate=countJson.getDouble("disprate");
				if(dispRate==0){
					continue;
				}
				String vosGw = countJson.getString("vosGw");
				String channelNo = countJson.getString("channelNo");
				int hisCount=countJson.getInteger("hisCount");
				int curCount=countJson.getInteger("curCount");
				if(bestDispRate==0){
					bestKey=keyNo;
					bestVosGw=vosGw;
					bestChannelNo=channelNo;
					bestDispRate=dispRate;
					bestHisCount=hisCount;
					bestCurCount=curCount;
				}else{
					int bestCount=bestHisCount+bestCurCount;
					int totalCount=hisCount+curCount;
					if((bestCount/bestDispRate>totalCount/dispRate)||(bestCount/bestDispRate==totalCount/dispRate&&dispRate>bestDispRate)){
						bestKey=keyNo;
						bestVosGw=vosGw;
						bestChannelNo=channelNo;
						bestDispRate=dispRate;
						bestHisCount=hisCount;
						bestCurCount=curCount;
					}
				}
			}
			if(bestDispRate>0){
				JSONObject countJson = new JSONObject();
				countJson.put("disprate",bestDispRate);
				countJson.put("vosGw", bestVosGw);
				countJson.put("channelNo", bestChannelNo);
				countJson.put("hisCount", bestHisCount);
				countJson.put("curCount", bestCurCount+1);
				mapResult.put(bestKey, countJson.toString());
			}
		}
		Map<String, JSONObject> map = new HashMap<String, JSONObject>();
		for (Map.Entry<String,String> men : mapResult.entrySet()) {
			String keyNo = men.getKey();
			String valueJson = men.getValue();
			JSONObject countJson = JSONObject.parseObject(valueJson);
			int curCount=countJson.getInteger("curCount");
			if(curCount==0){
				continue;
			}
			RedisUtils.hash_incrBy(routeCountKey,keyNo,curCount);
			map.put(keyNo, countJson);
		}
		
		return map;
	}

	//手机号码校验
	public static JSONObject mobileCheck(String today,String mobile, String companyId, String appId, JSONArray blackGroup,JSONObject groupJson){
		JSONObject json = new JSONObject();
		//白名单校验
		if(mobileWhite(mobile,companyId,appId)){
			json.put("check",1);//1-白名单 2-受限 3-未受限
			return json;
		}
		//修治白名单 --TODO
		/*if(mobileXZWhite(mobile)){
			json.put("check",1);//1-白名单 2-受限 3-未受限
			return json;
		}*/
		json.put("check",2);
		//修治黑名单 --TODO
		/*if(mobileXZBlack(mobile)){
			json.put("check",2);//1-白名单 2-受限 3-未受限
			//黑名单
			json.put("status","BLACK");//状态
			json.put("statusName","黑名单");//状态描述
			return json;
		}*/
		//黑名单校验
		if(mobileBlack(today,mobile,companyId,appId,blackGroup,groupJson)){
			json.put("check",2);//1-白名单 2-受限 3-未受限
			//黑名单
			json.put("status","BLACK");//状态
			json.put("statusName","黑名单");//状态描述

			//当日黑名单触黑数
			String repeatKey = RedisUtils.HASH_BLACK_REPEAT_RATE+ today + ":" + companyId + ":" + appId;
			RedisUtils.hash_incrBy(repeatKey,"black",1);
			return json;
		}
		//频次校验
		if(bandLimit(mobile,companyId,appId)){
			//频次受限
			json.put("status","LIMIT");//状态
			json.put("statusName","频次受限");//状态描述
			return json;
		}
		//地区校验
		if(areaLimit(mobile,companyId,appId)){
			//频次受限
			json.put("status","LIMIT");//状态
			json.put("statusName","省市受限");//状态描述
			return json;
		}
		json.put("check",3);//1-白名单 2-受限 3-未受限
		return json;
	}

	//渠道侧手机号码校验
	public static JSONObject mobileChannelCheck(String today,String mobile, String spId, String channelId){
		JSONObject json = new JSONObject();
		json.put("check",2);
		//频次校验渠道侧
		if(bandLimitChannel(mobile,spId,channelId)){
			//频次受限
			json.put("status","LIMIT");//状态
			json.put("statusName","渠道频次受限");//状态描述
			return json;
		}

		json.put("check",3);//1-白名单 2-受限 3-未受限
		return json;
	}

	//修治黑名单
	/*private static boolean mobileXZBlack(String mobile){
		logger.info("修治黑名单过滤：{}",mobile);
		for (int i = 0; i < XZ_BLACK.size(); i++) {
			if(mobile.equals(XZ_BLACK.get(i))){
				logger.info("修治黑名单命中：{}",mobile);
				return true;
			}
		}
		return false;
	}*/
	//黑名单校验
	private static boolean mobileBlack(String today,String mobile,String companyId,String appId,JSONArray blackGroup,JSONObject groupJson){
		Iterator<Object> it   = blackGroup.iterator();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			//黑名单渠道 local-自有黑名单，JSMX-江苏梦翔，ZJLJ-浙江棱镜，DYDX-东云短信，DYYY-东云语音
			String channel = json.getString("channel");
			//高危1-选中2-未选中
			int riskHigh = json.getInteger("riskHigh");
			int riskMedium = json.getInteger("riskMedium");
			int riskLow = json.getInteger("riskLow");
			int riskPrivate = json.getInteger("riskPrivate");
			//int priority = json.getInteger("priority");
			double filterRate = json.getDouble("filterRate");

			int repeatDay=groupJson.containsKey("repeatDay")?groupJson.getInteger("repeatDay"):0;
			int repeatCount=groupJson.containsKey("repeatCount")?groupJson.getInteger("repeatCount"):0;
			int blackCount=groupJson.containsKey("blackCount")?groupJson.getInteger("blackCount"):0;
			double blackRate=groupJson.containsKey("blackRate")?groupJson.getDouble("blackRate"):0;
			//校验过滤
			if(checkBlackControl(today,companyId,appId,mobile,repeatDay,repeatCount,blackCount,blackRate) && checkBlackTotal(today,channel,companyId,appId,filterRate)){
				continue;
			}
			switch (channel){
				case "local":
					logger.info("黑名单过滤选择：{}","local");
					if(mobileCheckLocal(mobile,companyId,riskHigh,riskMedium,riskLow,riskPrivate)){
						return true;
					}
					break;
				case "JSMX":
					logger.info("黑名单过滤选择：{}","JSMX");
					//1为正常号码，31、32、33分别为高、中、低危号码，30为私有库号码，20为白名单号码
					int jsmx = mobileCheckJS(mobile,companyId,appId);
					if((riskHigh==1&&jsmx==31)||(riskMedium==1&&jsmx==32)||(riskLow==1&&jsmx==33)||(riskPrivate==1&&jsmx==30)){
						int ruleLevel=1;
						if(jsmx==32){
							ruleLevel=2;
						}
						if(jsmx==33){
							ruleLevel=3;
						}
						String updateSql = String.format(" INSERT INTO e_black_info(mobile,rule_level,source,remark,update_time,create_time) "
								+ "VALUES('%s',%s,'%s','%s',now(),now());",mobile,ruleLevel,"JSMX","江苏梦翔黑名单");
						RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
						RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

						String key = RedisUtils.HASH_MOBILE_BLACK+ruleLevel;
						RedisUtils.hash_set(key,mobile,"1");

						return true;
					}
					break;
				case "ZJLJ":
					logger.info("黑名单过滤选择：{}","ZJLJ");
					//1为正常号码，5为危险
					int zjlj = mobileCheckNj(mobile,companyId,appId,NJ_USERNAME,NJ_PASSWORD,"1",NJ_SERVICEURL);
					if((riskHigh==1||riskMedium==1||riskLow==1||riskPrivate==1)&&zjlj==5){
						int ruleLevel=1;
						String updateSql = String.format(" INSERT INTO e_black_info(mobile,rule_level,source,remark,update_time,create_time) "
								+ "VALUES('%s',%s,'%s','%s',now(),now());",mobile,ruleLevel,"ZJLJ","浙江棱镜黑名单");
						RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
						RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

						String key = RedisUtils.HASH_MOBILE_BLACK+ruleLevel;
						RedisUtils.hash_set(key,mobile,"1");

						return true;
					}
					break;
				case "DYDX":
					logger.info("黑名单过滤选择：{}","DYDX");
					//大于0，即为黑名单，为0 表示正常
					int dydx = mobileCheckDy(mobile,companyId,appId,DY_DX_ACCESSKEY,DY_DX_SECRETKEY,DY_DX_SERVICEURL);
					if((riskHigh==1||riskMedium==1||riskLow==1||riskPrivate==1)&&dydx>0){
						int ruleLevel=1;
						String updateSql = String.format(" INSERT INTO e_black_info(mobile,rule_level,source,remark,update_time,create_time) "
								+ "VALUES('%s',%s,'%s','%s',now(),now());",mobile,ruleLevel,"DYDX","东云短信黑名单");
						RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
						RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

						String key = RedisUtils.HASH_MOBILE_BLACK+ruleLevel;
						RedisUtils.hash_set(key,mobile,"1");
						return true;
					}
					break;
				case "DYYY":
					logger.info("黑名单过滤选择：{}","DYYY");
					//大于0，即为黑名单，为0 表示正常
					int dyyy = mobileCheckDy(mobile,companyId,appId,DY_VOICE_ACCESSKEY,DY_VOICE_SECRETKEY,DY_VOICE_SERVICEURL);
					if((riskHigh==1||riskMedium==1||riskLow==1||riskPrivate==1)&&dyyy>0){
						int ruleLevel=1;
						String updateSql = String.format(" INSERT INTO e_black_info(mobile,rule_level,source,remark,update_time,create_time) "
								+ "VALUES('%s',%s,'%s','%s',now(),now());",mobile,ruleLevel,"DYYY","东云语音黑名单");
						RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
						RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

						String key = RedisUtils.HASH_MOBILE_BLACK+ruleLevel;
						RedisUtils.hash_set(key,mobile,"1");
						return true;
					}
					break;
				default:
					break;
			}

		}
		/*//默认过滤江苏...
		if (BLACK_SORT.size()==0){
			//1为正常号码，31、32、33分别为高、中、低危号码，30为私有库号码，20为白名单号码
			int result = mobileCheckJS(mobile,companyId,appId);
			//全局控制策略
			if(GlobalBlackFilterConfig.JS_BLACK_LEVEL.size()==0){
				if(result==31||result==32||result==33||result==30){
					return true;
				}
			}else {
				for (int i = 0; i < GlobalBlackFilterConfig.JS_BLACK_LEVEL.size(); i++) {
					if(result==GlobalBlackFilterConfig.JS_BLACK_LEVEL.get(i)){
						logger.info("过滤码号：{}",GlobalBlackFilterConfig.JS_BLACK_LEVEL.get(i));
						return true;
					}
				}
			}
		}else {
			for (int j = 0; j < BLACK_SORT.size(); j++) {
				if("JS".equals(BLACK_SORT.get(j))){
					logger.info("黑名单过滤选择：{}","JS");
					int result = mobileCheckJS(mobile,companyId,appId);
					//全局控制策略
					if(GlobalBlackFilterConfig.JS_BLACK_LEVEL.size()==0){
						if(result==31||result==32||result==33||result==30){
							return true;
						}
					}else {
						for (int i = 0; i < GlobalBlackFilterConfig.JS_BLACK_LEVEL.size(); i++) {
							if(result==GlobalBlackFilterConfig.JS_BLACK_LEVEL.get(i)){
								logger.info("过滤码号：{}",GlobalBlackFilterConfig.JS_BLACK_LEVEL.get(i));
								return true;
							}
						}
					}
				}else if("NJ".equals(BLACK_SORT.get(j))){
					logger.info("黑名单过滤选择：{}","NJ");
					//1为正常号码，5为危险
					int result = mobileCheckNj(mobile,companyId,appId,NJ_USERNAME,NJ_PASSWORD,"1",NJ_SERVICEURL);
					if(result==5){
						return true;
					}
				}else if("DYV".equals(BLACK_SORT.get(j))){
					logger.info("黑名单过滤选择：{}","DYV");
					//大于0，即为黑名单，为0 表示正常
					int result = mobileCheckDy(mobile,companyId,appId,DY_VOICE_ACCESSKEY,DY_VOICE_SECRETKEY,DY_VOICE_SERVICEURL);
					if(result>0){
						return true;
					}
				}else if("DYD".equals(BLACK_SORT.get(j))){
					logger.info("黑名单过滤选择：{}","DYD");
					//大于0，即为黑名单，为0 表示正常
					int result = mobileCheckDy(mobile,companyId,appId,DY_DX_ACCESSKEY,DY_DX_SECRETKEY,DY_DX_SERVICEURL);
					if(result>0){
						return true;
					}
				}
			}
		}*/
		return false;
	}

	//黑名单智能管控 N天重复率，当天黑名单触黑率 返回false则后续比例100%校验
	private static boolean checkBlackControl(String today,String companyId,String appId,String mobile,int repeatDay,int repeatCount,int blackCount,double blackRate){
		String repeatKey = RedisUtils.HASH_BLACK_REPEAT_RATE+ today + ":" + companyId + ":" + appId;
		long repeatTotal = RedisUtils.hash_incrBy(repeatKey,"total",1);
		if(repeatTotal<=1){
			RedisUtils.hash_setExpire(repeatKey,24*3600);
		}
		if(repeatDay>0&&repeatCount>0){
			long repeats = 0;
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < repeatDay; i++) {
				String data=new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
				String limitKey = RedisUtils.HASH_BAND_LIMIT+data+":"+companyId+":"+appId;
				if(RedisUtils.hash_exists(limitKey,mobile)){
					repeats = RedisUtils.hash_incrBy(repeatKey,"repeat",1);
					break;
				}
				cal.add(Calendar.DATE, -1);
			}
			if(repeats>=repeatCount){
				return false;
			}
		}

		if(blackCount>0&&blackRate>0&&repeatTotal%blackCount==0){
			String blackStr = RedisUtils.hash_get(repeatKey,"black");
			long blacks=Long.parseLong(StringUtils.isBlank(blackStr)?"0":blackStr);
			if(blacks*100/repeatTotal>=blackRate){
				return false;
			}
		}

		return true;
	}

	//校验各个黑名单渠道的过滤比例 true则不校验
	private static boolean checkBlackTotal(String today,String channel,String companyId,String appId,double filterRate){
		if(filterRate>=100){
			return false;
		}
		if(filterRate<=0){
			return true;
		}
		String filterKey = RedisUtils.HASH_BLACK_FILTER_RATE+today+":"+companyId+":"+appId;
		long total = RedisUtils.hash_incrBy(filterKey,"total",1);
		if(total<=1){
			RedisUtils.hash_incrBy(filterKey,"filter",1);
			RedisUtils.hash_setExpire(filterKey,24*3600);
			return false;
		}
		String filterStr = RedisUtils.hash_get(filterKey,"filter");
		long filter = Long.parseLong(filterStr);
		if((filter*100/total)<filterRate){
			RedisUtils.hash_incrBy(filterKey,"filter",1);
			return false;
		}
		return true;
	}

	//江苏梦翔孙丙翔黑名单质检线路AI
	private static int mobileCheckJS(String mobile,String companyId,String appId){
		//黑名单校验
		String blackUrl = "http://bj.mxtx.cn:5678/Api/blackcheckbyphonefordate/121/000f4f4b3250b1b73e06ee09cc5723e6/"+mobile;
		Hashtable<String, String> formHeaders = new Hashtable<String, String>();
		formHeaders.put("Content-Type", "application/xml");
		String str = HttpInterface.httpClientGet(blackUrl, 30000, "UTF-8", formHeaders);
		if(str.indexOf("http请求异常")==0){
			str = HttpInterface.httpClientGet(blackUrl, 30000, "UTF-8", formHeaders);
		}
		int blackStatus=1;
		if(str.indexOf("http请求异常")==0){
			logger.info("黑名单三方校验提交结果异常：{}：{}",mobile,str);
		}else{
			//{"code":0,"msg":"Success","data":{"unique_id":"1661155223067838744","status":32,"date":"20191225"}}
			JSONObject black = JSONObject.parseObject(str);
			int code = black.getInteger("code");
			if(code==0){
				JSONObject blackData = black.getJSONObject("data");
				//1为正常号码，31、32、33分别为高、中、低危号码，30为私有库号码，20为白名单号码
				blackStatus=blackData.getInteger("status");
			}
		}
		return blackStatus;
	}

	/**浙江棱镜信息科技有限公司黑名单质检
	 * @param mobile 码号
	 * @param companyId 公司id
	 * @param appId
	 * @param username
	 * @param password
	 * @param checktype 1 表示黑名单（目前只能取1）
	 * @param url
	 * @return
	 */
	private static int mobileCheckNj(String mobile,String companyId,String appId,String username,String password,String checktype,String url){
		//url拼接
		StringBuilder blackUrl = new StringBuilder(url);
		blackUrl.append("?mobile=");
		blackUrl.append(mobile);
		blackUrl.append("&username=");
		blackUrl.append(username);
		blackUrl.append("&password=");
		blackUrl.append(password);
		blackUrl.append("&timestamp=");
		long timeStamp  = new Date().getTime();
		blackUrl.append(timeStamp);
		blackUrl.append("&sign=");
		String sign = MD5Utils.MD5Encode(checktype + mobile + GlobalBlackFilterConfig.NJ_SECRETKEY + timeStamp).toUpperCase();
		blackUrl.append(sign);
		blackUrl.append("&checktype=");
		blackUrl.append(checktype);
		//防止对方解析的是body不是url路由明文
		Map<String,String> paramMap=new HashMap<>();
//		paramMap.put("username",username);
//		paramMap.put("password",password);
//		paramMap.put("mobile",mobile);
//		paramMap.put("timestamp",String.valueOf(timeStamp));
//		paramMap.put("checktype",checktype);
//		paramMap.put("sign",sign);
		String str = HttpInterface.httpClientPostByBody(blackUrl.toString(), paramMap, 30000, "UTF-8");
		//重试
		if(str.indexOf("http请求异常")==0){
			str = HttpInterface.httpClientPostByBody(blackUrl.toString(), paramMap, 30000, "UTF-8");
		}
		int blackStatus=1;
		if(str.indexOf("http请求异常")==0){
			logger.info("黑名单三方校验提交结果异常：{}：{}",mobile,str);
		}else{
			//{"code":0,"msg":"Success","data":{"unique_id":"1661155223067838744","status":32,"date":"20191225"}}
			JSONObject black = JSONObject.parseObject(str);
			int code = black.getInteger("code");
			if(code==110){
				logger.info("黑名单三方校验接口异常：{}：{}",mobile,str);
			}else if(code==-1){
				logger.info("黑名单三方校验接口参数异常：{}：{}",mobile,str);
			}else if(code==-2){
				logger.info("黑名单三方校验接口签名不通过：{}：{}",mobile,str);
			}else if(code==-3){
				logger.info("黑名单三方校验接口查询号码超为最大单次数（单次最大为1000）：{}：{}",mobile,str);
			}
			if(code==1||code==5){
				//1为正常号码，5为危险
				blackStatus=code;
			}
		}
		return blackStatus;
	}
	//东云
	private static int mobileCheckDy(String mobile,String companyId,String appId,String accessKey,String secretKey,String url){
		//呼叫唯一标识
		String callId = UUID.randomUUID().toString().replace("-","").toLowerCase();
		JSONObject paramMap=new JSONObject();
		String sign = MD5Utils.MD5Encode(accessKey + callId + secretKey);
		paramMap.put("ak",accessKey);
		paramMap.put("caller","123");
		paramMap.put("callee",mobile);
		paramMap.put("callId", callId);
		paramMap.put("sign",sign);
		Hashtable<String, String> formHeaders=new Hashtable<>();
		formHeaders.put("Content-Type","application/json");
		String str = HttpInterface.httpClientPost(GlobalBlackFilterConfig.DY_DX_SERVICEURL, paramMap.toJSONString(), 30000, "UTF-8",formHeaders);
		//重试
		if(str.indexOf("http请求异常")==0){
			str = HttpInterface.httpClientPost(GlobalBlackFilterConfig.DY_DX_SERVICEURL, paramMap.toJSONString(), 30000, "UTF-8",formHeaders);
		}
		int blackStatus= -1;
		if(str.indexOf("http请求异常")==0){
			logger.info("黑名单三方校验提交结果异常：{}：{}",mobile,str);
		}else{
			/*
				{
				"code": 1,
				"msg": "success",
				"callId": 321,
				"forbid": 1,
				"transactionId": "123456"
				}
			 */
			JSONObject black = JSONObject.parseObject(str);
			if(!callId.equals(black.getString("callId"))){
				logger.info("黑名单三方校验接口响应数据与查询数据无上下文关系，存在交叉响应，数据不准确：{}：{}",mobile,str);
			}
			int code = black.getInteger("code");
			if(code==4001){
				logger.info("黑名单三方校验接口参数异常：{}：{}",mobile,str);
			}else if(code==4000){
				logger.info("黑名单三方校验接口accessKey错误：{}：{}",mobile,str);
			}else if(code==4002){
				logger.info("黑名单三方校验接口账号错误：{}：{}",mobile,str);
			}else if(code==4010){
				logger.info("黑名单三方校验接口签名错误：{}：{}",mobile,str);
			}else if(code==4003){
				logger.info("IP地址禁止访问第三方黑名单接口：{}：{}",mobile,str);
			}else if (code==4011){
				logger.info("黑名单三方校验接口超过阈值：{}：{}",mobile,str);
			}else if(code==4004){
				logger.info("余额不足,无法访问第三方黑名单接口：{}：{}",mobile,str);
			}
			if(code==1){
				//1为正常号码，5为危险
				blackStatus=black.getInteger("forbid");
			}
		}
		return blackStatus;
	}

	//白名单校验
	private static boolean mobileWhite(String mobile,String companyId,String appId){
		//全局白名单
		String key = RedisUtils.HASH_MOBILE_WHITE+"0:0";
		if(RedisUtils.hash_exists(key,mobile)){
			return true;
		}
		//客户白名单
		key = RedisUtils.HASH_MOBILE_WHITE+companyId+":0";
		if(RedisUtils.hash_exists(key,mobile)){
			return true;
		}
		//账号白名单
		key = RedisUtils.HASH_MOBILE_WHITE+companyId+":"+appId;
		if(RedisUtils.hash_exists(key,mobile)){
			return true;
		}
		return false;
	}
	//本地黑名单校验
	private static boolean mobileCheckLocal(String mobile,String companyId,int riskHigh,int riskMedium,int riskLow,int riskPrivate){
		//全局黑名单-高
		if(riskHigh==1){
			String key = RedisUtils.HASH_MOBILE_BLACK+"1";
			if(RedisUtils.hash_exists(key,mobile)){
				return true;
			}
		}
		//全局黑名单-中
		if(riskMedium==1){
			String key = RedisUtils.HASH_MOBILE_BLACK+"2";
			if(RedisUtils.hash_exists(key,mobile)){
				return true;
			}
		}
		//全局黑名单-低
		if(riskLow==1){
			String key = RedisUtils.HASH_MOBILE_BLACK+"3";
			if(RedisUtils.hash_exists(key,mobile)){
				return true;
			}
		}
		//客户私有黑名单
		if(riskPrivate==1){
			String key = RedisUtils.HASH_MOBILE_BLACK+"0"+companyId;
			if(RedisUtils.hash_exists(key,mobile)){
				return true;
			}
		}

		return false;
	}
	//修治白名单
	/*private static boolean mobileXZWhite(String mobile){
		logger.info("修治白名单过滤：{}",mobile);
		for (int i = 0; i < XZ_WHITE.size(); i++) {
			if(mobile.equals(XZ_WHITE.get(i))){
				logger.info("修治白名单命中：{}",mobile);
				return true;
			}
		}
		return false;
	}*/
	//频次控制客户侧
	private static boolean bandLimit(String mobile,String companyId,String appId){
		Map<String, Integer> mapCount = new HashMap<String, Integer>();
		Map<String, String> mapPublic = RedisUtils.hash_getFields(RedisUtils.HASH_BAND_LIMIT_APP+"0:0");
		Map<String, String> map = RedisUtils.hash_getFields(RedisUtils.HASH_BAND_LIMIT_APP+companyId+":"+appId);
		mapMerge(mapPublic, map);

		for (Map.Entry<String,String> en:map.entrySet()) {
			int days = Integer.parseInt(en.getKey());
			int times = Integer.parseInt(en.getValue());
			int count = 0;
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < days; i++) {
				String date=new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
				int dateCount = 0;
				if(mapCount.containsKey(date)){
					dateCount = mapCount.get(date);
				}else{
					String limitKey = RedisUtils.HASH_BAND_LIMIT+date+":"+companyId+":"+appId;
					String dayCount = RedisUtils.hash_get(limitKey,mobile);
					dateCount = StringUtils.isNotBlank(dayCount)?Integer.parseInt(dayCount):0;
					mapCount.put(date,dateCount);
				}
				count += dateCount;
				if(count>=times){
					return true;
				}
				cal.add(Calendar.DATE, -1);
			}
		}

		return false;
	}

	//频次控制渠道侧
	private static boolean bandLimitChannel(String mobile, String spId, String channelId){
		Map<String, Integer> mapCount = new HashMap<String, Integer>();
		Map<String, String> mapPublic = RedisUtils.hash_getFields(RedisUtils.HASH_BAND_LIMIT_CHANNEL+"0:0");
		Map<String, String> map = RedisUtils.hash_getFields(RedisUtils.HASH_BAND_LIMIT_CHANNEL+spId+":"+channelId);
		mapMerge(mapPublic, map);
		for (Map.Entry<String,String> en:map.entrySet()) {
			int days = Integer.parseInt(en.getKey());
			int times = Integer.parseInt(en.getValue());
			int count = 0;
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < days; i++) {
				String date=new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
				int dateCount = 0;
				if(mapCount.containsKey(date)){
					dateCount = mapCount.get(date);
				}else{
					String limitKey = RedisUtils.HASH_BAND_LIMIT_CH+date+":"+spId+":"+channelId;
					String dayCount = RedisUtils.hash_get(limitKey,mobile);
					dateCount = StringUtils.isNotBlank(dayCount)?Integer.parseInt(dayCount):0;
					mapCount.put(date,dateCount);
				}
				count += dateCount;
				if(count>=times){
					return true;
				}
				cal.add(Calendar.DATE, -1);
			}
		}

		return false;
	}

	private static void mapMerge(Map<String, String> mapPublic, Map<String, String> map) {
		for (Map.Entry<String,String> en:mapPublic.entrySet()) {
			String days = en.getKey();
			String times = en.getValue();
			if(map.containsKey(days)){
				if(Integer.parseInt(map.get(days))>Integer.parseInt(times)){
					map.put(days,times);
				}
			}else{
				map.put(days,times);
			}
		}
	}

	//地区控制
	private static boolean areaLimit(String mobile,String companyId,String appId){
		//屏蔽北京
		String segment = RedisUtils.hash_get(RedisUtils.HASH_MOBILE_SEGMENT,mobile.substring(0,7));
		if(StringUtils.isBlank(segment)){
			return false;
		}
		if("16".equals(appId)||"3".equals(appId)){
			return false;
		}
		if(segment.indexOf("010_")!=-1){
			return true;
		}
		return false;
	}
}
