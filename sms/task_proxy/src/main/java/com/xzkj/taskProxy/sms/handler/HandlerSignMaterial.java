package com.xzkj.taskProxy.sms.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskProxy.redis.RedisUtils;
import com.xzkj.taskProxy.util.Base64Util;
import com.xzkj.taskProxy.util.MmsUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class HandlerSignMaterial implements Runnable {
	public static Logger logger = LoggerFactory.getLogger("HandlerSignMaterial");

	private String companyId;
	private String appId;
	private int concurrentSize=0;
	Date date =new Date();
	SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");//年月日

	public HandlerSignMaterial(String companyId, String appId, int curCount) {
		this.companyId=companyId;
		this.appId=appId;
		this.concurrentSize=curCount;
	}
	public void run(){
		//long bt = System.currentTimeMillis();
		try {
			//队列key
			String fifo_key = RedisUtils.FIFO_APP_SIGN_LIST+companyId+":"+appId;
			String jsonStr = null;
			int count=0;
			while((jsonStr = RedisUtils.fifo_pop(fifo_key))!=null){
				JSONObject json = JSONObject.parseObject(jsonStr);
				//模板id
				String signId = json.containsKey("signId")?json.getString("signId"):"";
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
				String signFilePath = json.containsKey("signFilePath")?json.getString("signFilePath"): MmsUtils.signFilePath; // signFile
				//模板文件相对路径
				String signFilePaths = json.containsKey("signFilePaths")?json.getString("signFilePaths"):MmsUtils.signFilePaths; // http://103.29.16.3:9100/profile/
				String signProfile = json.containsKey("signProfile")?json.getString("signProfile"):MmsUtils.signProfile; // /home/platform/mmsUploadPath/
				//模板媒体的文件内容(json 数组格式字符串) [{"type":1,"ext":"png","body":"…"},{"type":2,"ext":"pdf","body":"…"}]
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
					String ext = content.containsKey("ext")?content.getString("ext"):"png";
					String body = content.containsKey("body")?content.getString("body"):"";
					int fileSize=0;

					String filePath=signProfile+signFilePath;
					String fileName= "/"+DateFormatUtils.format(new Date(), "yyyyMMdd")+"/F"+signId.substring(2)+type+index;
					fileAllPaths = filePath+fileName+"."+ext;

					fileAllPath = signFilePaths+signFilePath+fileName+"."+ext;
					fileSize = decoderBase64File(body,fileAllPaths);


					//修改签名素材表
					String updateSql = String.format("update e_model_sign set upLoad_file='%s',filepath='%s' where sign_id='%s';",fileAllPath,fileAllPaths,signId);
					RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
					RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
				}
				count++;
				// 如果超过X条
				if (count >= concurrentSize) {
					break;
				}
			}
			String conf_key = RedisUtils.HASH_APP_SIGN_TOTAL;
			RedisUtils.hash_incrBy(conf_key,companyId+"_"+appId,0-count);
		} catch (Exception e) {
			logger.error("视频短信模板素材处理失败key：{},ex:{}",RedisUtils.FIFO_APP_SIGN_LIST+companyId+":"+appId,e.getMessage());
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

}