package com.xzkj.taskProxy.sms.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.sms.ThreadConstants;
import com.xzkj.taskProxy.util.DateUtils;
import com.xzkj.taskProxy.util.MmsUtils;
import com.xzkj.taskProxy.util.MobileUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class HandlerSendPretreat implements Runnable {
	public static Logger logger = LoggerFactory.getLogger("HandlerSendPretreat");
	
	private String companyId;
	private String appId;
	private int concurrentSize=0;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");//年月日
	
	public HandlerSendPretreat(String companyId, String appId, int curCount) {
		this.companyId=companyId;
		this.appId=appId;
		this.concurrentSize=curCount;
	}
	public void run(){
		//long bt = System.currentTimeMillis();
		try {
			String today = sdf.format(new Date());
			String routeKey = RedisUtils.ZSET_ROUTE_BASE+companyId+":"+appId;
			List<String> routes = ThreadConstants.getRouteList(routeKey);

			//队列key
			String fifo_key =RedisUtils.FIFO_APP_REQ_LIST+companyId+":"+appId;
			String jsonStr = null;
			int count=0;
			while((jsonStr = RedisUtils.fifo_pop(fifo_key))!=null){
				JSONObject json = JSONObject.parseObject(jsonStr);
				//客户账号
				String appName = json.containsKey("appName")?json.getString("appName"):"";
				//客户密码
				String password = json.containsKey("password")?json.getString("password"):"";
				//接入号，可以为空
				String src = json.containsKey("src")?json.getString("src"):"";
				//视频短信模板ID
				String mmsId = json.getString("mid");
				//手机号码,多个以”,”隔开，单次提交号码不能超过200个
				String mobs = json.getString("mobs");
				//提交响应的messageId
				String batchId = json.getString("batchId");
				//提交的时间戳
				String timestamp = json.getString("ts");
				//变量参数组(组内成员变量以半角逗号分隔，组与组之间以|分隔，格式（变量1=a1,变量2=b1,变量3=c1|变量1=a2,变量2=b2,变量3=c2|变量1=a3,变量2=b3,变量3=c3) （urlencode编码 utf-8）
				String params = json.containsKey("params")?json.getString("params"):"";
				String data = json.containsKey("data")?json.getString("data"):"";
				String url = json.containsKey("url")?json.getString("url"):"";
				//1-普通视频短信发送，2-变量视频短信发送
				int sendType = json.getInteger("sendType");
				//String groupId = json.getString("groupId");//黑名单组ID
				JSONObject groupJson = json.getJSONObject("groupJson");//黑名单组
				JSONArray blackGroup = json.getJSONArray("blackGroup");//黑名单组关系

				//签名
				String tempStr=RedisUtils.hash_get(RedisUtils.HASH_APP_MODEL_SIGN+appId, mmsId);
				JSONObject tempJson = JSONObject.parseObject(tempStr);
				String signName = tempJson!=null&&tempJson.containsKey("sign_name")?tempJson.getString("sign_name"):"";
				String signId = tempJson!=null&&tempJson.containsKey("sign_id")?tempJson.getString("sign_id"):"";
				String title = tempJson!=null&&tempJson.containsKey("title")?tempJson.getString("title"):"";

				String submitTime = DateUtils.timeStampToString(Long.parseLong(timestamp));
				String channelId="";//渠道id
				String[] paramsArr=null;
				if(sendType==2){
					paramsArr=params.split("\\|");
				}
				//模板id与通道侧模板id映射关系
				Map<String, String> relatedMap = RedisUtils.hash_getFields(RedisUtils.HASH_MODEL_RELATED+mmsId);
				String[] phonesArr = mobs.split(",");
				for (int i=0;i<phonesArr.length;i++){
					String caller="";//接入号
					String mobile = phonesArr[i];
					int provider= MobileUtil.getProvider(mobile);//运营商
					String province = json.getString("province");
					String linkId = MmsUtils.getMmsLinkID();
					String tableName = MmsUtils.getSenderTableName();
					if(StringUtils.isNotEmpty(linkId)){
						tableName = MmsUtils.parseLinkID(linkId);
					}

					String param = sendType==2?paramsArr[i]:"";
					JSONObject channelJson = new JSONObject();
					channelJson.put("linkId",linkId);
					channelJson.put("tableName",tableName);
					channelJson.put("sendType",sendType);//1-普通视频短信发送，2-变量视频短信发送
					channelJson.put("mobile",mobile);
					channelJson.put("param",param);
					channelJson.put("companyId",companyId);
					channelJson.put("appId",appId);
					channelJson.put("appName",appName);//客户账号
					channelJson.put("password",password);//客户密码
					channelJson.put("signName",signName);//短信签名内容
					channelJson.put("signId",signId);//短信签名
					channelJson.put("srcNum",src);//接入号
					channelJson.put("mmsId",mmsId);//模板id
					channelJson.put("data",data);//
					channelJson.put("url",url);//
					channelJson.put("batchId",batchId);//客户提交相应 messageId
					channelJson.put("allChannelNos", "egg"+channelId);//该批次发送过的渠道，半角逗号分隔
					String channelMmsId="";
					String channelParam="";

					//手机号校验
					JSONObject mobJson = ThreadConstants.mobileCheck(today,mobile,companyId,appId,blackGroup,groupJson);
					int check = mobJson.getInteger("check");
					if(check==2){//1-白名单 2-受限 3-未受限
						String status = mobJson.getString("status");
						String statusName = mobJson.getString("statusName");
						failureMMS(channelJson,tableName,mmsId,"-1",linkId,title,submitTime,status,signName,statusName,batchId,mobile,appName);
						continue;
					}

					String channelIdHis="";
					//路由校验
					for(String route:routes){
						//按照分发比例处理优先级相同的
						JSONObject routejson = ThreadConstants.disRoutePretreat(companyId,appId,provider,signName,province,today,route,relatedMap,param);
						if(routejson==null){
							continue;
						}
						channelId = routejson.getString("channel_id");//渠道id

						if(check!=1) {//1-白名单 2-受限 3-未受限
							//手机号渠道侧校验
							JSONObject mobChJson = ThreadConstants.mobileChannelCheck(today,mobile,"0",channelId);
							int channelCheck = mobChJson.getInteger("check");
							if(channelCheck==2){
								channelIdHis = channelId;
								mobJson.put("status",mobChJson.getString("status"));
								mobJson.put("statusName",mobChJson.getString("statusName"));
								channelId="";
								continue;
							}
						}

						caller = routejson.getString("channel_caller");//渠道接入号
						channelMmsId = routejson.getString("channel_model_id");//渠道模板id
						channelParam = routejson.getString("channel_param");//渠道模板参数
						break;
					}

					if(StringUtils.isBlank(channelId)&&!StringUtils.isBlank(channelIdHis)){
						String status = mobJson.getString("status");
						String statusName = mobJson.getString("statusName");
						failureMMS(channelJson,tableName,mmsId,channelIdHis,linkId,title,submitTime,status,signName,statusName,batchId,mobile,appName);
						continue;
					}

					if(StringUtils.isNotBlank(caller)){
						src=caller;
					}
					channelMmsId = StringUtils.isNotBlank(channelMmsId)?channelMmsId:mmsId;
					channelParam = StringUtils.isNotBlank(channelParam)?channelParam:param;
					channelJson.put("channelModelId",channelMmsId);//道模板id
					channelJson.put("channelParam",channelParam);//道模板参数
					channelJson.put("srcNum",src);//接入号
					channelJson.put("allChannelNos", "egg"+channelId);//该批次发送过的渠道，半角逗号分隔
					if(StringUtils.isBlank(channelId)){
						failureMMS(channelJson,tableName,mmsId,"-1",linkId,title,submitTime,"NOROUTE",signName,"无路由",batchId,mobile,appName);
						continue;
					}
					successMMS(channelJson,channelId,tableName,mmsId,channelMmsId,linkId,title,submitTime,"submit",signName,"客户提交成功",batchId,mobile,appName);

					if(check!=1){//1-白名单 2-受限 3-未受限
						//非白名单数据，客户侧频次加1
						String limitKey = RedisUtils.HASH_BAND_LIMIT+today+":"+companyId+":"+appId;
						RedisUtils.hash_incrBy(limitKey,mobile,1);
						RedisUtils.hash_setExpire(limitKey,7*24*3600);
						//非白名单数据，通道频次加1
						String channelKey = RedisUtils.HASH_BAND_LIMIT_CH+today+":0:"+channelId;
						RedisUtils.hash_incrBy(channelKey,mobile,1);
						RedisUtils.hash_setExpire(channelKey,7*24*3600);
					}
					//通道下模板的日发送数加一
					String channelModelKey = RedisUtils.HASH_CHANNEL_MODEL_LIMIT+today+":"+channelId;
					RedisUtils.hash_incrBy(channelModelKey,channelMmsId,1);
				}
				count++;
				// 如果超过X条
				if (count >= concurrentSize) {
					break;
				}
			}
			String conf_key = RedisUtils.HASH_APP_REQ_TOTAL;
			RedisUtils.hash_incrBy(conf_key,companyId+"_"+appId,0-count);
		} catch (Exception e) {
			logger.error("视频短信预处理分发渠道失败key：{},ex:{}",RedisUtils.FIFO_APP_REQ_LIST+companyId+":"+appId,e.getMessage());
			e.printStackTrace();
		}
		//处理时长计算
		//long ut = System.currentTimeMillis() - bt;
		//logger.error("时长统计：seqNo={}，itemNum={}，time={}，concurrentSize={}",seqNo,itemNum,ut,concurrentSize);
		return;
    }

    //校验失败操作
	private void failureMMS(JSONObject channelJson,String tableName,String mmsId,String channelId,String linkId,String title,String submitTime,String status,String signName,String statusName,String batchId,String mobile,String appName){
		String save_path="";//附件保存路径
		String service_code="model";//服务号
		String mm7version="";//彩信版本号
		String updateSql = String.format(" INSERT INTO %s"
						+ "(batch_id,link_id,subject,submit_time,status,sign_name,info,channel_id,message_id,dest_number,create_time,save_path,service_code,"
						+ "mm7version,app_name,data_status,channel_msg_id,report_time,app_id,company_id,chan_task_id) "
						+ "VALUES('%s','%s','%s','%s','%s','%s','%s',%s,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',%s,%s,'%s');",tableName,mmsId,linkId,title,submitTime,status
				,signName,statusName,channelId,batchId,mobile,submitTime,save_path,service_code,mm7version,appName,"matched","-1",submitTime,appId,companyId,"");

		RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
		RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

		//失败回调客户
		channelJson.put("reportStatus",status);
		channelJson.put("reportTime",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		channelJson.put("info",statusName);
		RedisUtils.fifo_push(RedisUtils.FIFO_MMS_MT_CLIENT+companyId,channelJson.toJSONString());
		RedisUtils.hash_incrBy(RedisUtils.HASH_MMS_MT_COUNT, companyId+"", 1);
	}
	//校验成功操作
	private void successMMS(JSONObject channelJson,String channelId,String tableName,String mmsId,String channelMmsId,String linkId,String title,String submitTime,String status,String signName,String statusName,String batchId,String mobile,String appName){
		String save_path="";//附件保存路径
		String service_code="model";//服务号
		String mm7version="";//彩信版本号
		String updateSql = String.format(" INSERT INTO %s"
						+ "(batch_id,link_id,subject,submit_time,status,sign_name,info,channel_id,message_id,dest_number,create_time,save_path,service_code,"
						+ "mm7version,app_name,data_status,report_time,app_id,company_id,chan_task_id) "
						+ "VALUES('%s','%s','%s','%s','%s','%s','%s',%s,'%s','%s','%s','%s','%s','%s','%s','%s','%s',%s,%s,'%s');",tableName,mmsId,linkId,title,submitTime,status
				,signName,statusName,channelId,batchId,mobile,submitTime,save_path,service_code,mm7version,appName,"matched",submitTime,appId,companyId,"");

		RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
		RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
		logger.info("channelId: {}, channelMmsId: {}",channelId,channelMmsId);
		//提交到渠道队列
		RedisUtils.fifo_push(RedisUtils.FIFO_CHANNEL_REQUEST+channelId+":"+channelMmsId, channelJson.toString());
		RedisUtils.hash_incrBy(RedisUtils.HASH_CHANNEL_REQ_COUNT+channelId, channelMmsId, 1);
	}
}