package com.xzkj.taskProxy.sms.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.util.Base64Util;
import com.xzkj.taskProxy.util.HttpInterface;
import com.xzkj.taskProxy.util.MD5Utils;
import com.xzkj.taskProxy.util.MmsUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class HandlerModelMaterial implements Runnable {
	public static Logger logger = LoggerFactory.getLogger("HandlerModelMaterial");

	private String companyId;
	private String appId;
	private int concurrentSize=0;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");//年月日

	public HandlerModelMaterial(String companyId, String appId, int curCount) {
		this.companyId=companyId;
		this.appId=appId;
		this.concurrentSize=curCount;
	}
	public void run(){
		//long bt = System.currentTimeMillis();
		try {
			//队列key
			String fifo_key = RedisUtils.FIFO_APP_MODEL_LIST+companyId+":"+appId;
			String jsonStr = null;
			int count=0;
			while((jsonStr = RedisUtils.fifo_pop(fifo_key))!=null){
				JSONObject json = JSONObject.parseObject(jsonStr);
				//模板id
				String modelId = json.containsKey("modelId")?json.getString("modelId"):"modelId";
				//提交时间
				String submitTime = json.containsKey("submitTime")?json.getString("submitTime"):"";
				if(StringUtils.isBlank(submitTime)){
					submitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				}
				/*//公司id
				String companyId = json.containsKey("companyId")?json.getString("companyId"):"";*/
				//账号id
				String appId = json.containsKey("appId")?json.getString("appId"):"0";
				//用户账号
				String appName = json.containsKey("appName")?json.getString("appName"):"appName";
				//模板文件实际路径
				String modelFilePath = json.containsKey("modelFilePath")?json.getString("modelFilePath"): MmsUtils.modelFilePath;
				//模板文件相对路径
				String modelFilePaths = json.containsKey("modelFilePaths")?json.getString("modelFilePaths"):MmsUtils.modelFilePaths;
				//模板媒体的文件内容(json 数组格式字符串) [{"type":1,"ext":"txt","body":"…"},{"type":2,"ext":"jpg","body":"…"}]
				//文件内容（文本类型按 urlencode编码 utf-8；媒体文件按二进制内容读取后的 base64 编码 ）
				JSONArray contentArr = json.getJSONArray("content");
				Iterator<Object> it   = contentArr.iterator();
				int index =0;
				while (it.hasNext()) {
					index++;
					String fileAllPath="";
					String fileAllPaths="";
					JSONObject content = (JSONObject) it.next();
					int type = content.containsKey("type")?content.getInteger("type"):1;
					String ext = content.containsKey("ext")?content.getString("ext"):"txt";
					String body = content.containsKey("body")?content.getString("body"):"";
					int fileSize=0;
					if(type==1){//1-文本，2-图片，3-视频，4-音频
						logger.info("解码前文字内容：{}",body);
						try {
							body= URLDecoder.decode(body,"UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						logger.info("解码后文字内容：{}",body);
						fileAllPath=body;
						fileSize=body.length();
					}else {
//						String filePath=modelFilePath+appName;
						String filePath=modelFilePath;
						String fileName= "/"+DateFormatUtils.format(new Date(), "yyyyMMdd")+"/F"+modelId.substring(2)+type+index;
						fileAllPaths = filePath+fileName+"."+ext;
//						fileAllPath = modelFilePaths+appName+fileName+"."+ext;
						fileAllPath = modelFilePaths+fileName+"."+ext;
						fileSize = decoderBase64File(body,fileAllPaths);
					}
					/*
					INSERT INTO e_model_material(model_id,media_type,media_index,ext,content,file_path,file_size,user_id,update_time,create_time) VALUES('M1131024',2,1,'jpg','/profile/modelFile/xiuzhi/20230105/F13102421.jpg','/home/platform/mmsUploadPath/modelFile/xiuzhi/20230105/F13102421.jpg',1584,0,'2023-01-05 14:35:03','2023-01-05 14:35:03');
					INSERT INTO e_model_material(model_id,media_type,media_index,ext,content,file_path,file_size,user_id,update_time,create_time) VALUES('M1131024',1,2,'txt','','',0,0,'2023-01-05 14:35:03','2023-01-05 14:35:03');
					 */

					//写入模板素材表
					String updateSql = String.format(" INSERT INTO e_model_material(model_id,media_type,frame_index,frame_sort,ext,content,file_path,file_size,user_id,update_time,create_time) "
							+ "VALUES('%s',%s,%s,%s,'%s','%s','%s',%s,%s,'%s','%s');",modelId,type,1,index,ext,fileAllPath,fileAllPaths,fileSize,0,submitTime,submitTime);
					RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
					RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
				}
				count++;
				// 如果超过X条
				if (count >= concurrentSize) {
					break;
				}
			}
			String conf_key = RedisUtils.HASH_APP_MODEL_TOTAL;
			RedisUtils.hash_incrBy(conf_key,companyId+"_"+appId,0-count);
		} catch (Exception e) {
			logger.error("视频短信模板素材处理失败key：{},ex:{}",RedisUtils.FIFO_APP_MODEL_LIST+companyId+":"+appId,e.getMessage());
			e.printStackTrace();
		}
		//处理时长计算
		//long ut = System.currentTimeMillis() - bt;
		//logger.error("时长统计：seqNo={}，itemNum={}，time={}，concurrentSize={}",seqNo,itemNum,ut,concurrentSize);
		return;
    }

	/**
	 * 将base64字符解码保存文件
	 * @param base64Code
	 * @param fileAllPath
	 * @throws Exception
	 */
	private int decoderBase64File(String base64Code,String fileAllPath) throws Exception {
		Map<String,Integer> map = new HashMap<String,Integer>();
		byte[] buffer = Base64Util.decodeBASE64(base64Code);
		int size = buffer.length/1024;
		File file = new File(fileAllPath);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
		}

		FileOutputStream out = new FileOutputStream(fileAllPath);//覆盖已存在文件，FileOutputStream(targetPath,true)在已存在文件后面追加内容
		out.write(buffer);
		out.close();
		return size;
	}

	/**
	 * 5.1.1 上海电信素材提交接口，跟修治视频短信接口文档的模版提交接口进行了适配
	 */
	public static String testMaterialUpload(JSONObject jsonObject) {
		String url="http://124.126.120.102:8896/sapi/material";
		String CLIENT_SIID="";
		String CLIENT_KEY="";
		JSONObject reqElementsJsonObject = new JSONObject();
		String method = "material";
		reqElementsJsonObject.put("SiID", CLIENT_SIID);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(new Date());
		String authenticator = CLIENT_SIID + date + CLIENT_KEY;
		authenticator = MD5Utils.MD5Encode(CLIENT_SIID+date+CLIENT_KEY).toUpperCase();
		authenticator = authenticator.toUpperCase();
		reqElementsJsonObject.put("Authenticator", authenticator);
		reqElementsJsonObject.put("Date", date);
		reqElementsJsonObject.put("Method", method);
		//扩展号码
		reqElementsJsonObject.put("ExtNum", "1");

		String title = jsonObject.containsKey("title") ? jsonObject.getString("title") : "";
		try {
			title= URLDecoder.decode(title,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//内容主题
		reqElementsJsonObject.put("Subject", title);

		Hashtable<String, String> formHeaders = new Hashtable<String, String>();
		formHeaders.put("Content-Type", "multipart/form-data");
		Map<String,String>map=new HashMap<>();

		JSONObject json=new JSONObject();
		//模版素材内容
		JSONArray content = jsonObject.getJSONArray("content");
		int size = content.size();
		JSONArray conents=new JSONArray();
		for (int i = 0; i < size; i++) {
			JSONObject jsonObject1=new JSONObject();

			//文件拓展名
			String ext = JSONObject.parseObject(content.getString(i)).getString("ext");
			//文件类型
			Integer type = JSONObject.parseObject(content.getString(i)).getInteger("type");
			String body = JSONObject.parseObject(content.getString(i)).getString("body");
			if(type==1){
				//文本解码
				try {
					body= URLDecoder.decode(body,"UTF-8");
					jsonObject1.put("Frame","001-"+i+1);
					jsonObject1.put("Text",body);
					conents.add(jsonObject1);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else {
				//媒体解码
				body=Base64Util.decodeBASE64AsString(body);
				jsonObject1.put("Frame","001-"+i+1);
				jsonObject1.put("FileName",i+"."+ext);
				conents.add(jsonObject1);
				map.put(i+"."+ext,body);
			}

		}
		reqElementsJsonObject.put("Content", conents);
		map.put("Data",reqElementsJsonObject.toString());
		String str = HttpInterface.httpClientPost(url, map, 30000, "UTF-8", formHeaders);
//		logger.info("视频短信模版上海电信提交结果：{}-{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,mobile,submitJson.toJSONString(),str);
		String code="";
		String msg="提交通道失败";
		String channneMsgId="";
		String mmsid="";
		if(str.indexOf("Read timed out")!=-1){
			code = "0";
			msg = "Read timed out";
//			channneMsgId = linkId;
		}else if(str.indexOf("http请求异常")==0){
			code="-1";
			msg=str;
//			logger.info("视频短信北京移动提交结果超时：{}-{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,mobile,submitJson.toJSONString(),str);
		}else{
			JSONObject result = JSONObject.parseObject(str);
			code = result.getString("ResCode");
			msg = result.getString("ResMsg");
			channneMsgId = result.getString("TransID");
			mmsid = result.getString("MsgID");
		}
		json.put("channneMsgId",channneMsgId);
		json.put("info",msg);
		json.put("mmsId",mmsid);
		return json.toJSONString();
	}



	/**
	 * 拼接请求要素：
	 * 以json形式上传数据，附加 key为”Data”的请求要素（JSON字符串）
	 * @return
	 */
	public static String getJsonReqElements(JSONObject jsonObject) {
		String CLIENT_SIID="";
		String CLIENT_KEY="";
		JSONObject reqElementsJsonObject = new JSONObject();
		String method = "material";
		reqElementsJsonObject.put("SiID", CLIENT_SIID);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(new Date());
		String authenticator = CLIENT_SIID + date + CLIENT_KEY;
		authenticator = MD5Utils.MD5Encode(CLIENT_SIID+date+CLIENT_KEY).toUpperCase();
		authenticator = authenticator.toUpperCase();
		reqElementsJsonObject.put("Authenticator", authenticator);
		reqElementsJsonObject.put("Date", date);
		reqElementsJsonObject.put("Method", method);
		reqElementsJsonObject.put("ExtNum", "1");
		String title = jsonObject.containsKey("title") ? jsonObject.getString("title") : "";
		try {
			title= URLDecoder.decode(title,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		reqElementsJsonObject.put("Subject", title);

		JSONArray contentJsonArray = new JSONArray();

		JSONObject frameJsonObject1_1 = new JSONObject();
		frameJsonObject1_1.put("Frame", "1-1");
		frameJsonObject1_1.put("FileName", "1.jpg");
		contentJsonArray.add(frameJsonObject1_1);

		reqElementsJsonObject.put("Content", contentJsonArray);

		System.out.println("请求参数：" + reqElementsJsonObject.toString());
		return reqElementsJsonObject.toString();
	}
}