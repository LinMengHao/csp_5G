package com.xzkj.taskPorxy.sms.handler;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskPorxy.redis.RedisUtils;
import com.xzkj.taskPorxy.sms.ThreadConstants;
import com.xzkj.taskPorxy.util.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HandlerNoticeRecallPretreat implements Runnable {

	public static Logger logger = LoggerFactory.getLogger("HandlerNoticeRecallPretreat");
	
	private String seqNo;
	private String itemNum;
	private int concurrentSize=0;
	private int recallLimit=0;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("HH");//只有小时
	
	public HandlerNoticeRecallPretreat(String seqNo,String item_num,int callCount,int recallLimit){
		this.seqNo=seqNo;
		this.itemNum=item_num;
		this.concurrentSize=callCount;
		this.recallLimit=recallLimit;
	}
	public void run(){
		//long bt = System.currentTimeMillis();
		try {
			String routeKey = RedisUtils.ZSET_ROUTE_RESEND+seqNo+":"+itemNum;
			List<String> routes = ThreadConstants.getRouteList(routeKey);

			//队列key
			String recall_key =RedisUtils.FIFO_NOTICE_RECALL+seqNo+":"+itemNum;
			String value = null;
			int count=0;
			while((value = RedisUtils.fifo_pop(recall_key))!=null){
				if(StringUtils.isBlank(value)){
		    		logger.info("取出一条补呼记录失败，redis值为空:"+recall_key);
		    		continue;
		    	}
				JSONObject json = JSONObject.parseObject(value);
				String mobile=json.getString("mobile");
		    	String serialNum=json.getString("serialNum");
		    	//主叫号码
				String callerNum=json.getString("callerNum");
				//回调地址
				String callUrl = json.containsKey("callUrl")?json.getString("callUrl"):"";
				//客户数据
				String data = json.getString("data");
				String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				//用户key
				String userkey = json.containsKey("userkey")?json.getString("userkey"):"";
				String batchId=json.getString("batchId");
				String begin_time = json.getString("begin_time");
	    		String clientServer = json.getString("clientServer");
	    		String tradeId = json.getString("tradeId");
	    		String channelNo = json.getString("channelNo");
	    		//呼叫过的渠道编号，半角逗号分隔
	    		String allChannelNos = json.containsKey("allChannelNos")?json.getString("allChannelNos"):"";
	    		String sip_code = json.getString("sip_code");
	    		//System.out.println("0000000000000000000serialNum:"+serialNum+",allChannelNos="+allChannelNos);
				int recallTimes = json.containsKey("recallTimes")?json.getInteger("recallTimes"):0;
				int call_times = json.containsKey("call_times")?json.getInteger("call_times"):1;
				if(recallTimes>=recallLimit){
					//超过补呼次数，以最后一次呼叫结果,更新数据库，通知客户
					int duration = json.getInteger("duration");
					int alert_dur = json.containsKey("alert_dur")?json.getInteger("alert_dur"):0;
					int status = json.getInteger("status");
					String info = json.getString("info");
					String update_time = json.getString("update_time");
					String end_time = json.getString("end_time");
					String alert_time = json.getString("alert_time");
					String st = json.getString("st");
		    		
					StringBuffer sqlBuf = new StringBuffer("update e_order_bill set intention=0,fs_server='");
					sqlBuf.append(clientServer).append("',begin_time='").append(begin_time).append("',trade_id='").append(tradeId)
					.append("',duration=").append(duration).append(",caller_num='").append(callerNum).append("',status=").append(status);
					/*if(StringUtils.isNotBlank(alert_time)){
						sqlBuf.append("");//添加振铃时间
					}*/
					sqlBuf.append(",info='").append(info).append("',order_type='未分类',sip_code='").append(sip_code)
					.append("',update_time='").append(update_time).append("',end_time='").append(end_time).append("',channel_his='")
					.append(allChannelNos.replaceAll("egg","")).append("' where serial_num='").append(serialNum).append("'; ");
					
					String fifo_sql_key =RedisUtils.FIFO_SQL_LIST+seqNo;
					String hash_sql_key = RedisUtils.HASH_SQL_COUNT;
					RedisUtils.fifo_push(fifo_sql_key,sqlBuf.toString());
					RedisUtils.hash_incrBy(hash_sql_key, seqNo, 1);
					
					if(StringUtils.isNotBlank(callUrl)){
		    			json.put("type",4);
						json.put("alert_time", alert_time);
						json.put("alert_dur",alert_dur);//振铃时长
						json.put("answer_time","");
						json.put("end_time", end_time);
						json.put("call_times", 1);
						json.put("dtmf", "");
						json.put("result_signs", 2);
						json.put("st", st);
						json.put("msg", info);
		    			String fifo_back_key =RedisUtils.FIFO_KEY_BACK_REQUEST+seqNo;
		    			String hash_back_key = RedisUtils.HASH_CUR_BACK_ALL;
		    			RedisUtils.fifo_push(fifo_back_key,json.toString());
		    			RedisUtils.hash_incrBy(hash_back_key, seqNo, 1);
		    		}
					continue;
				}
				
				//被叫前缀
		    	String prefixCalled="";
		    	String channelRecall="";
		    	String caller="";//主叫
		    	String vosGw="";
		    	int provider = json.getInteger("provider");
		    	String province = json.getString("province");
		    	String spd=json.containsKey("speed")?json.getString("speed"):50+"";
		    	String vol=json.containsKey("vol")?json.getString("vol"):50+"";
		    	//声模选择
				String voice = json.containsKey("voice")?json.getString("voice"):"siyue";
		    	for(String route:routes){
		    		//按照分发比例处理优先级相同的
		    		JSONObject routejson = ThreadConstants.disRecallRoutePretreat(seqNo,itemNum,allChannelNos,channelNo,sip_code,provider,province, route);
		    		if(routejson==null){
		    			continue;
		    		}
		    		prefixCalled = routejson.containsKey("prefixCalled")?routejson.getString("prefixCalled"):"";//分省被叫前缀
		    		channelRecall=routejson.getString("channelRecall");//补呼渠道编号
		    		caller = routejson.getString("caller");//渠道主叫号码
		    		vosGw = routejson.getString("vosGw");//外呼网关
		    		break;
		    	}
		    	if(StringUtils.isNotBlank(caller)){
					callerNum=caller;
				}
		    	json.put("callerNum", callerNum);
		    	//无路由，以最后一次呼叫结果通知客户
		    	if(StringUtils.isBlank(channelRecall)){
		    		int duration = json.getInteger("duration");
					int alert_dur = json.containsKey("alert_dur")?json.getInteger("alert_dur"):0;
					int status = json.getInteger("status");
					String info = json.getString("info");
					String update_time = json.getString("update_time");
					String end_time = json.getString("end_time");
					String alert_time = json.getString("alert_time");
					String st = json.getString("st");
		    		
					StringBuffer sqlBuf = new StringBuffer("update e_order_bill set intention=0,fs_server='");
					sqlBuf.append(clientServer).append("',begin_time='").append(begin_time).append("',trade_id='").append(tradeId)
					.append("',duration=").append(duration).append(",caller_num='").append(callerNum).append("',status=").append(status);
					/*if(StringUtils.isNotBlank(alert_time)){
						sqlBuf.append("");//添加振铃时间
					}*/
					sqlBuf.append(",info='").append(info).append("',order_type='未分类',sip_code='").append(sip_code)
					.append("',update_time='").append(update_time).append("',end_time='").append(end_time).append("',channel_his='")
					.append(allChannelNos.replaceAll("egg","")).append("' where serial_num='").append(serialNum).append("'; ");
					
					String fifo_sql_key =RedisUtils.FIFO_SQL_LIST+seqNo;
					String hash_sql_key = RedisUtils.HASH_SQL_COUNT;
					RedisUtils.fifo_push(fifo_sql_key,sqlBuf.toString());
					RedisUtils.hash_incrBy(hash_sql_key, seqNo, 1);
					
					if(StringUtils.isNotBlank(callUrl)){
		    			json.put("type",4);
						json.put("alert_time", alert_time);
						json.put("alert_dur",alert_dur);//振铃时长
						json.put("answer_time","");
						json.put("end_time", end_time);
						json.put("call_times", 1);
						json.put("dtmf", "");
						json.put("result_signs", 2);
						json.put("st", st);
						json.put("msg", info);
		    			String fifo_back_key =RedisUtils.FIFO_KEY_BACK_REQUEST+seqNo;
		    			String hash_back_key = RedisUtils.HASH_CUR_BACK_ALL;
		    			RedisUtils.fifo_push(fifo_back_key,json.toString());
		    			RedisUtils.hash_incrBy(hash_back_key, seqNo, 1);
		    		}
					continue;
		    	}
		    	json.put("vosGw", vosGw);
		    	json.put("prefixCalled", prefixCalled);
		    	json.put("caller", caller);
				
		    	String mediaWave= json.containsKey("mediaWave")?json.getString("mediaWave"):"";
		    	if(StringUtils.isBlank(mediaWave)){
			    	//TTS标志 1-开启 2-关闭
			    	int tts_flag=2;
			    	//媒体类型（0表示语音文件，1表示文字）
    				int mediaType = json.getInteger("mediaType");
    				//延迟挂机时间（默认0）单位毫秒，语音按键必须设置
    				int delay = json.getInteger("delay");
    				//媒体类型要播放的语音文件名（wav）或者文字 文字不可超过200汉字（utf-8编码UrlEncoded）
    				String media = json.getString("media");
    				//要播放的挂机语音文件名（wav）或者文字，文字不可超过200汉字
    				String end = json.containsKey("end")?json.getString("end"):"";
    				String endWave="";
    				if(mediaType==0){
    					if(media.endsWith(".wav")){
    						mediaWave="/data/noticefile/"+itemNum+"/"+media;
    						endWave="/data/noticefile/"+itemNum+"/"+end;
    					}else{
    						mediaWave="/data/noticefile/"+itemNum+"/"+media+".wav";
    						endWave="/data/noticefile/"+itemNum+"/"+end+".wav";
    					}
    				}else{
    					if(tts_flag==1){
    						String beginMd5 = MD5Utils.MD5Encode(media);
        					//tts转换失败
        					if(mediaWave.indexOf("请求异常")!=-1){
        			    		String sql = " update e_order_bill set status=-1,info='tts失败',sip_code='994',channel_his='"+allChannelNos.replaceAll("egg","")+"',update_time=now() where serial_num='"+serialNum+"'; ";
            					String fifo_sql_key =RedisUtils.FIFO_SQL_LIST+seqNo;
            					String hash_sql_key = RedisUtils.HASH_SQL_COUNT;
            					RedisUtils.fifo_push(fifo_sql_key,sql);
            					RedisUtils.hash_incrBy(hash_sql_key, seqNo, 1);
            					
            					//回调
            					if(StringUtils.isNotBlank(callUrl)){
            						JSONObject json1 = new JSONObject();
            	    				json1.put("seqNo", seqNo);
            	    				json1.put("itemNum", itemNum);
            	    				json1.put("batchId", batchId);
            	    				json1.put("callerNum",callerNum);
            	    				json1.put("mobile", mobile);
            	    				json1.put("duration","0");
            	    				json1.put("type",4);
            	    				json1.put("begin_time", nowTime);
            	    				json1.put("alert_time", "");
            	    				json1.put("answer_time","");
            	    				json1.put("end_time", nowTime);
            	    				json1.put("call_times", 1);
            	    				json1.put("dtmf", "");
            	    				json1.put("user_data", data);
            	    				json1.put("result_signs", 2);
            	    				json1.put("userkey", userkey);
            	    				json1.put("callUrl", callUrl);
                					json1.put("st", "2");
                					json1.put("msg", "tts请求异常");
            	    				String fifo_back_key =RedisUtils.FIFO_KEY_BACK_REQUEST+seqNo;
            	    				String hash_back_key = RedisUtils.HASH_CUR_BACK_ALL;
            	    				RedisUtils.fifo_push(fifo_back_key,json1.toString());
            	    				RedisUtils.hash_incrBy(hash_back_key, seqNo, 1);
            		    		}
            					continue;
        					}
        					RedisUtils.string_set("tempKey", 24*60*60, mediaWave);
    					}
    				}
    				int delayCount = delay/1000;
    				for(int i=0;i<delayCount;i++){
    					mediaWave+="!/data/noticefile/blank1.wav";
    					endWave+="!/data/noticefile/blank1.wav";
    				}
    				json.put("mediaWave", mediaWave);
    				json.put("endWave", endWave);
		    	}
		    	//此条记录补呼次数
		    	json.put("recallTimes",recallTimes+1);
		    	json.put("call_times",call_times+1);
		    	json.put("channelNo", channelRecall);
		    	json.put("allChannelNos",allChannelNos+",egg"+caller+channelRecall);
		    	
				RedisUtils.fifo_push(RedisUtils.FIFO_CHANNEL_REQUEST+channelRecall, json.toString());
				RedisUtils.hash_incrBy(RedisUtils.HASH_CHANNEL_REQ_COUNT, channelRecall, 1);
				count++;
				// 如果超过X条
				if (count >= concurrentSize) {
					break;
				}
			}
			String conf_key = RedisUtils.HASH_RECALL_ALL+"notice";
			RedisUtils.hash_incrBy(conf_key,seqNo+"_"+itemNum, count==0?concurrentSize:(0-count));
		} catch (Exception e) {
			logger.error("语音通知预处理分发渠道失败key：{},itemNum:{},ex:{}",RedisUtils.HASH_ITEM_JSON+seqNo,itemNum,e.getMessage());
			e.printStackTrace();
		}
		//处理时长计算
		//long ut = System.currentTimeMillis() - bt;
		//logger.error("时长统计：seqNo={}，itemNum={}，time={}，concurrentSize={}",seqNo,itemNum,ut,concurrentSize);
		return;
    }
	 
}