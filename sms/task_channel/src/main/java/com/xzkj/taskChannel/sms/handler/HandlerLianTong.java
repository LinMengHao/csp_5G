package com.xzkj.taskChannel.sms.handler;

import com.alibaba.fastjson.JSONObject;

import com.xzkj.taskChannel.redis.RedisUtils;
import com.xzkj.taskChannel.util.HttpInterface;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class HandlerLianTong implements Runnable {
    public static Logger logger = LoggerFactory.getLogger("HandlerLianTong");

    private int linkSpeed=0;
    private JSONObject jsonChannel=null;
    private String channelId=null;
    private String channelMmsId=null;
    Date date =new Date();
    SimpleDateFormat sdf =new SimpleDateFormat("HH");//只有小时
    public
    HandlerLianTong(int linkSpeed,String channelId,String channelMmsId,JSONObject jsonChannel){
        this.linkSpeed=linkSpeed;
        this.jsonChannel=jsonChannel;
        this.channelId=channelId;
        this.channelMmsId=channelMmsId;
    }
    public void run(){
        long bt = System.currentTimeMillis();
        int size=0;
        try {
            //发送列表
            List<JSONObject> orderList = new Vector<JSONObject>();
            //渠道列表key
            String channel_key = RedisUtils.FIFO_CHANNEL_REQUEST+channelId+":"+channelMmsId;
            String jsonStr = null;
            while((jsonStr = RedisUtils.fifo_pop(channel_key))!=null){
                if(StringUtils.isBlank(jsonStr)){
                    continue;
                }
                JSONObject json = JSONObject.parseObject(jsonStr);

                int count = send(json);//提交
                size+=count;
                if(size>=linkSpeed){
                    break;
                }
            }
            RedisUtils.hash_incrBy(RedisUtils.HASH_CHANNEL_REQ_COUNT+channelId, channelMmsId, 0-size);


        } catch (Exception e) {
            logger.error("视频短信渠道发送失败key：{},channelNo:{},ex:{}","notice",channelId,e.getMessage());
            e.printStackTrace();
        }
        if(size > 0){//控制提交速度
            long ut = System.currentTimeMillis() - bt;
            int sleepTime = 1000 / linkSpeed * size;
            logger.info("视频短信速度控制：channelId:{}-sleepTime:{}-ut:{}-size:{}-linkSpeed:{}-bt:{}",channelId,sleepTime,ut,size,linkSpeed,bt);
            //ut=11-84秒 sleepTime=1秒
            if (ut < sleepTime) {
                try {
                    Thread.sleep(sleepTime - ut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //处理时长计算
        //long ut = System.currentTimeMillis() - bt;
        //logger.error("时长统计：seqNo={}，itemNum={}，time={}，concurrentSize={}",seqNo,itemNum,ut,concurrentSize);
        RedisUtils.hash_incrBy(RedisUtils.HASH_CHANNEL_THREAD_COUNT,channelId,-1);
        return;
    }

    public int send(JSONObject json) {
        try {
            String apiKey="JKNEJZ202211";
            String apiSecrect="{bcrypt}$2a$10$A9Q9zmKcHIe5yBnJCWLPZezgIFgGthFDooVfL8v1bB29/0AZT7W4q";
            //默认不含变量
            String url = "http://szdxapi.wo.cn/api/send";
            String ts = System.currentTimeMillis() + "";

            String authenticator = DigestUtils.md5Hex(apiKey + ts + apiSecrect);
            String mobile=json.getString("mobile");//手机号
            Map<String,String> map=new HashMap<>();
            map.put("phone",mobile);
            String linkId=json.getString("linkId");//客户唯一id
            String channelParam=json.getString("channelParam");//通道变量模板参数
            logger.info("channelParam: {}",channelParam);
            int sendType = json.getInteger("sendType");//1-普通视频短信发送，2-变量视频短信发送

            JSONObject submitJson = new JSONObject();
            submitJson.put("appKey",apiKey);
            submitJson.put("mmsId",Long.parseLong(channelMmsId));
            submitJson.put("ts",ts);
            submitJson.put("authenticator",authenticator);
            StringBuilder stringBuilder=new StringBuilder();
            String[] split = channelParam.split(",");
            for (int i = 0; i < split.length; i++) {
                String[] split1 = split[i].split("=");
                stringBuilder.append(split1[1]);
                if(i!=split.length-1){
                    stringBuilder.append(",");
                }
            }
            if(sendType==2){
                map.put("param",stringBuilder.toString());
                url="http://szdxapi.wo.cn/api/cuParamSend";
            }
            List<Map<String,String>> list1=new ArrayList<>();
            list1.add(map);
            submitJson.put("phones",list1);

            Hashtable<String, String> formHeaders = new Hashtable<String, String>();
            formHeaders.put("Content-Type", "application/json");
            String str = HttpInterface.httpClientPost(url, submitJson.toJSONString(), 60000, "UTF-8", formHeaders);
			/*if(str.indexOf("http请求异常")==0){
				str = HttpInterface.httpClientPost(url, submitJson.toJSONString(), 300000, "UTF-8", formHeaders);
			}*/
            logger.info("视频短信北京移动提交结果：{}-{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,mobile,submitJson.toJSONString(),str);
            String code="";
            String msg="提交通道失败";
            String channneMsgId="";
            if(str.indexOf("Read timed out")!=-1){
                code = "0";
                msg = "Read timed out";
                channneMsgId = linkId;
            }else if(str.indexOf("http请求异常")==0){
                code="-1";
                msg=str;
                logger.info("视频短信北京移动提交结果超时：{}-{}-{}-{}:{}:{}",channelId,channelMmsId,linkId,mobile,submitJson.toJSONString(),str);
            }else{
                JSONObject result = JSONObject.parseObject(str);
                code = result.getString("code");
                msg = result.containsKey("msg")?result.getString("msg"):"参数解析错误";
                channneMsgId = result.getString("transId");
            }
            json.put("channneMsgId",channneMsgId);
            json.put("info",msg);
            //提交成功
            if(code.equals("0000")){
                insertMT(json,channelId,channneMsgId,msg);
            }else{
                insertFailMT(json,channelId,"mk:0010",msg);
            }
            return 1;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 提交入库
     */
    private void insertMT(JSONObject json,String channelId,String channneMsgId,String info){
        String linkId=json.getString("linkId");//订单唯一id
        String tableName=json.getString("tableName");
        String companyId=json.getString("companyId");
        String appName=json.getString("appName");
        //String appId=json.getString("appId");

        String status="submit";
        String mobile=json.getString("mobile");//手机号

        //客户消费数加一
        RedisUtils.hash_incrBy(RedisUtils.HASH_ACC_SEND, appName,1);
        //添加至回调返回状态
        RedisUtils.hash_set(RedisUtils.HASH_MMS_MT_CHANNEL+channelId+":"+channneMsgId,mobile,json.toJSONString());
        //数据库状态同步更新
        String updateSql = String.format("update %s set status='%s',data_status='%s',info='%s',channel_msg_id='%s' where link_id='%s';",
                tableName, status, "submited",info, channneMsgId,linkId);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);
    }

    private void insertFailMT(JSONObject json,String channelId,String status,String info) {
        String linkId=json.getString("linkId");//订单唯一id
        String tableName=json.getString("tableName");
        String companyId=json.getString("companyId");

        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String reportTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        //提交失败数据入库
        String updateSql = String.format("update %s set status='%s',info='%s',report_time='%s' where link_id='%s';",
                tableName,status,info,nowTime,linkId);
        RedisUtils.fifo_push(RedisUtils.FIFO_SQL_LIST+companyId,updateSql);
        RedisUtils.hash_incrBy(RedisUtils.HASH_SQL_COUNT, companyId+"", 1);

        //失败回调客户
        json.put("reportStatus",status);
        json.put("reportTime",reportTime);
        RedisUtils.fifo_push(RedisUtils.FIFO_MMS_MT_CLIENT+companyId,json.toJSONString());
        RedisUtils.hash_incrBy(RedisUtils.HASH_MMS_MT_COUNT, companyId+"", 1);
    }

    class PhoneDto {
        /**
         * 用户号码
         *
         * 子类暂不适用验证
         * @Valid
         * @NotBlank
         * [phones[2].phone不能为空,phones[0].phone不能为空,phones[1].phone不能为空]
         */
        private String phone;

        /**
         * 参数替换内容，格式 Key:Value
         * 变量内容长度不超过20个字符
         */
        private String param;

        @Override
        public String toString() {
            return new JSONObject().toJSONString(this);
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }
    }
    class AuthDto{
        /**
         *  oauth2 clientId
         */
        private String appKey;

        /**
         *  Md5(appKey + ts + secret)
         *  secret 为 oauth2 clientSecret
         */
        private String authenticator;

        /**
         *  时间戳
         */
        private String ts;

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAuthenticator() {
            return authenticator;
        }

        public void setAuthenticator(String authenticator) {
            this.authenticator = authenticator;
        }

        public String getTs() {
            return ts;
        }

        public void setTs(String ts) {
            this.ts = ts;
        }
    }
    class SendDto extends AuthDto {

        /**
         * 模板ID
         */
        private Long mmsId;
        /**
         * 扩展号码，当号码允许扩展后，接入
         * 号码（平台配置）+扩展号码总长度小
         * 于等于18
         *
         * 使用者/渠道商不可见
         * 免签者选填
         */
        private String extNum;

        /**
         * 用户号码列表，单次最大100个号码
         */
        private List<PhoneDto> phones;

        @Override
        public String toString() {
            return new JSONObject().toJSONString(this);
        }

        public Long getMmsId() {
            return mmsId;
        }

        public void setMmsId(Long mmsId) {
            this.mmsId = mmsId;
        }

        public String getExtNum() {
            return extNum;
        }

        public void setExtNum(String extNum) {
            this.extNum = extNum;
        }

        public List<PhoneDto> getPhones() {
            return phones;
        }

        public void setPhones(List<PhoneDto> phones) {
            this.phones = phones;
        }
    }

}
