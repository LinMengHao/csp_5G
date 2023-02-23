package com.xzkj.taskChannel.sms;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskChannel.redis.RedisUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadConstants {

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
	public static JSONObject disRoutePretreat(String companyId, String appId, int provider,String signName, String province,String today,String route){
		JSONObject jsonPre = null;
		long dispratePre=0L;
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
			long disprate = routejson.getLong("disprate");//分发比例
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
			if(dispratePre==0){
				dispratePre=disprate;
				countPre = count;
				jsonPre = routejson;
				routeIdPre = routeId;
				channelIdPre = channel_id;
				continue;
			}
			if(count/disprate<countPre/dispratePre){
				dispratePre=disprate;
				countPre = count;
				jsonPre = routejson;
				routeIdPre = routeId;
				channelIdPre = channel_id;
				continue;
			}
			if(count/disprate==countPre/dispratePre&&disprate>dispratePre){
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
	
}
