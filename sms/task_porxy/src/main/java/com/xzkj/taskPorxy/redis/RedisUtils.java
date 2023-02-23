package com.xzkj.taskPorxy.redis;


import com.xzkj.taskPorxy.redis.client.*;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

public class RedisUtils {

    public static String FIFO_QUERY_MODEL_LIST="mt_model_query_client";
    public static String HASH_QUERY_MODEL_TOTAL="mt_model_query_count";
    //渠道信息token,key:channelId
    public static String STR_CHANNEL_TOKEN="channel_token:";
    public static String HASH_MODEL_VARS_CHANNEL="mt_model_vars_channel";
    public static String FIFO_MODEL_MT_CLIENT="mt_model_client";
    //模版回调保存信息
    public static String HASH_MODEL_MT_COUNT="mt_model_count";
    public static String HASH_MODEL_MT_CHANNEL="mt_model_channel";

    //签名回调保存信息
    public static String FIFO_SIGN_MT_CLIENT="mt_sign_client";
    public static String HASH_SIGN_MT_COUNT="mt_sign_count";
    public static String HASH_SIGN_MT_CHANNEL="mt_sign_channel";

    //客户状态报告推送队列key：companyid
    public static String FIFO_MMS_MT_CLIENT="mt_client:";
    //客户状态报告数 key,companyId
    public static String HASH_MMS_MT_COUNT="mt_count";
    //需要多长补呼的队列eg：back_more:robot,20180329155600,json
    public static String ZSET_KEY_BACK_MORE="back_more:";
    //渠道状态报告回执key：channelid
    public static String HASH_MMS_UN_MATCH="UNMATCH:";
    //客户状态报告重推
    public static String FIFO_REPORT_LIST="fifo_report_list";
    //任务是否正在执行
    public static String STR_KEY_AI_TASK="string_task_proxy_running";
    //客户发送频次限制 eg：20220822：companyid：appid,mobile,1
    public static String HASH_BAND_LIMIT="limit_band:";
    //通道发送频次限制 eg：20220822：spid：channelid,mobile,1
    public static String HASH_BAND_LIMIT_CH="limit_band_ch:";
    //客户发送频次限制 eg：key：companyid：appid,days,times
    public static String HASH_BAND_LIMIT_APP="limit_band_app:";
    //渠道发送频次限制 eg：key：spid：channelid,days,times
    public static String HASH_BAND_LIMIT_CHANNEL="limit_band_channel:";

    //通道下模板id限制 eg：20220822：channelId,channelModelId,1
    public static String HASH_CHANNEL_MODEL_LIMIT="limit_channel_model:";
    //白名单 eg：key：companyid：appid,mobile,1
    public static String HASH_MOBILE_WHITE="mobile_white:";
    //全局黑名单 eg：key:level,mobile,1 （黑名单等级level：1-高危2-中危3-低危，0+companyid表示私有黑名单）
    public static String HASH_MOBILE_BLACK="mobile_black:";
    //手机号段表 eg：key,segment,provinceid_provincename_cityid_cityname_provider
    public static String HASH_MOBILE_SEGMENT="mobile_segment";
    //黑名单过滤比例 eg：20220822：companyid：appid,total/filter,1
    public static String HASH_BLACK_FILTER_RATE="black_filter_rate:";
    //黑名单N天内重复数 eg：20220822：companyid：appid,total/repeat/black,1(当日总数/N天内重复数/当天触黑数)
    public static String HASH_BLACK_REPEAT_RATE="black_repeat_rate:";

    //渠道待发送队列表eg：key：channelId,json
    //public static String FIFO_CHANNEL_REQUEST="request_channel:";

    //用户信息 key:appid,json
    public static String STR_KEY_APP_INFO="string_app_info:";
    //帐户余额，appName
    public static final String HASH_LOGIC_ACC_BALANCE = "acc:balance";
    //帐户发送数量，appName
    public static final String HASH_ACC_SEND = "acc:consume";

    //客户模板提交数据队列计数 key,companyId_appId,100
    public static String HASH_APP_MODEL_TOTAL="hash_app_model_total";
    //客户模板提交数据队列 key:companyId:appId,json
    public static String FIFO_APP_MODEL_LIST="fifo_app_model_list:";

    public static String HASH_APP_SIGN_TOTAL="hash_app_sign_total";
    //客户签名提交数据队列 key:companyId:appId,json
    public static String FIFO_APP_SIGN_LIST="fifo_app_sign_list:";

    public static String HASH_CHANNEL_SIGN_TOTAL="hash_channel_sign_total";
    //签名提交通道数据队列 key:companyId:appId,json
    public static String FIFO_CHANNEL_SIGN_LIST="fifo_channel_sign_list:";
    //模板提交通道数据队列计数 key,companyId_appId,100
    public static String HASH_CHANNEL_MODEL_TOTAL="hash_channel_model_total";
    //模板提交通道数据队列 key:companyId:appId,json
    public static String FIFO_CHANNEL_MODEL_LIST="fifo_channel_model_list:";

    //客户提交数据队列计数 key,companyId_appId,100
    public static String HASH_APP_REQ_TOTAL="hash_app_req_total";
    //客户提交数据队列 key:companyId:appId,json
    public static String FIFO_APP_REQ_LIST="fifo_app_req_list:";
    //路由redis键zset类型：route_base:company_id:appid,优先级，json串
    public static String ZSET_ROUTE_BASE="zset_route_base:";
    //补呼路由redis键zset类型：route_resend:company_id:appid,优先级，json串
    public static String ZSET_ROUTE_RESEND="zset_route_resend:";
    //商户某项目数据库提交队列表eg：key:companyId
    public static String FIFO_SQL_LIST="fifo_sql_list:";
    //商户下的数据库操作 key,companyId
    public static String HASH_SQL_COUNT="hash_sql_count";
    //路由分发计数 key：companyId：appId,id_ch1001,100
    public static String HASH_ROUTE_DISPENSE="hash_route_send_disp:";
    //补呼路由分发计数 key：companyId：appId,id_ch1001
    public static String HASH_ROUTE_RECALL_DISPENSE="hash_route_resend_disp:";
    //视频彩信模板信息 key：appId,model_id,json
    public static String HASH_APP_MODEL_SIGN="hash_app_model_sign:";
    //渠道待发送队列表eg：key：channelId
    public static String FIFO_CHANNEL_REQUEST_OLD="MMSLOG:";
    //客户状态报告推送队列key：appid
    public static String FIFO_MMS_DELIVRD_TUBE="AD:";

    //模板映射关系,key：modelId,channelId_channelModelId,json
    public static String HASH_MODEL_RELATED="model_related:";
    //渠道信息,视频短信mms|普通短信sms,通道id,json
    public static String HASH_CHANNEL_INFO="channel_info:";
    //呼叫失败补呼预处理汇总 key机器人robot,语音通知notice,验证码code，sp1000_v10003,100
    public static String HASH_RECALL_ALL="cur_recall_all:";
    //项目实体信息json，item_info:sp1001，v0008,{'':''}
    public static String HASH_ITEM_JSON="item_info:";
    //商户需要回调接口数据的列表eg：key:sp1001
    public static String FIFO_KEY_BACK_REQUEST="req_back:";
    //商户回调通知操作 key,sq1005
    public static String HASH_CUR_BACK_ALL="cur_back_all";
    //商户某项目语音通知补呼提交批次队列eg：key:sp1001:v0001
    public static String FIFO_NOTICE_RECALL="recall_notice:";
    //渠道待发送队列表eg：key:channelid:mmsid,json
    public static String FIFO_CHANNEL_REQUEST="req_channel:";
    //渠道待发送队列数 key:channelid,mmsid,100
    public static String HASH_CHANNEL_REQ_COUNT="req_channel_count:";
    //路由配置 key：sp1005：v1002,ch1001
    public static String HASH_CUR_ROUTE_ALL="cur_route_all:";
    //项目发起呼叫，正在呼叫线路数eg：sp1001_key,v0001
    public static String HASH_KEY_COUNT="_cou";

    /**
     * 添加到队头
     * @param key   key
     * @param value value
     */
    public static void fifo_lpush(String key, String value) {
        RedisFifoClient.lpush(key, value);
    }
    public static void fifo_lpushObject(String key, Object value) {
        RedisFifoClient.lpushObject(key, value);
    }
    public static void fifo_lpush_byte(String key, byte[] value) {
        RedisFifoClient.lpushByteArr(key, value);
    }

    /**
     * 添加到队尾
     * @param key   key
     * @param value value
     */
    public static void fifo_push(String key, String value) {
        RedisFifoClient.push(key, value);
    }

    /**
     * 批量添加到队尾
     * @param key   key
     * @param msgList value
     */
    public static void fifo_push(String key, List<String> msgList) {
        RedisFifoClient.push(key, msgList);
    }

    /**
     * 添加到队尾object
     * @param key   key
     * @param value value
     */
    public static void fifo_pushObject(String key, Object value) {
        RedisFifoClient.pushObject(key, value);
    }

    /**
     * 队列大小
     * @param key 键
     * @return
     */
    public static long fifo_len(String key) {
        return RedisFifoClient.len(key);
    }

    /**
     * 从队首取数据
     * @param key KEY
     * @return string
     */
    public static String fifo_pop(String key) {
        return RedisFifoClient.pop(key);
    }

    /**
     * 从队首取数据-AI
     * @param key KEY
     * @return string
     */
    public static String fifo_pop_ai(String key) {
        return RedisFifoClient.pop_ai(key);
    }

    /**
     * 从队首取数据
     * @param key KEY
     * @return string
     */
    public static Object fifo_popObject(String key) {
        return RedisFifoClient.popObject(key);
    }

    /**
     * 取一段
     * @param key 键
     * @param start 起始下标
     * @param end 结束下标
     * @return
     */
    public static List<Object> fifo_range(String key, long start, long end) {
        return RedisFifoClient.range(key, start, end);
    }
    /**
     * 删除数据
     * @param key 键
     * @return
     */
    public static Long fifo_remove(String key) {
        return RedisFifoClient.remove(key);
    }
    /**
     * 是否存在
     * @param key 键
     * @return
     */
    public static boolean fifo_exist(String key) {
        return RedisFifoClient.exist(key);
    }

    /**
     * 环形队列取值
     * @param key key
     */
    public static String fifo_circle(String key) {
        return RedisFifoClient.circlePop(key);
    }
    /***************************************string-begin**************************************************/
    /**
     * 添加数据
     * @param key   key
     * @param seconds   超时时间
     * @param value value
     */
    public static void string_set(String key,int seconds, String value) {
        RedisStringClient.set(key,seconds, value);
    }

    /**
     * 添加数据
     * @param key   key
     * @param seconds   超时时间
     * @param value value
     */
    public static void string_set(String key,int seconds, Object value) {
        RedisStringClient.set(key,seconds, value);
    }

    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void string_set(String key,String value) {
        RedisStringClient.set(key, value);
    }

    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void string_set(String key,String value,String lock) {
        RedisStringClient.set(key, value,lock);
    }

    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void string_set(String key,Object value) {
        RedisStringClient.set(key, value);
    }

    /**
     * 添加数据
     * @param key   key
     * @param lock   超时时间
     * @param value value
     */
    public static void string_set(String key,Object value,String lock) {
        RedisStringClient.set_lock(key, value);
    }

    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static String string_get(String key) {
        return RedisStringClient.get(key);
    }

    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static String string_get(String key,String lock) {
        return RedisStringClient.get(key,lock);
    }

    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static Object string_get_obj(String key) {
        return RedisStringClient.getObject(key);
    }

    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static Object string_get_obj(String key,String lock) {
        return RedisStringClient.getObject_lock(key);
    }

    /**
     * 删除数据-加锁
     * @param key 键
     * @return
     */
    public static Long string_remove(String key,String lock) {
        return RedisStringClient.remove(key,lock);
    }

    /**
     * 删除数据
     * @param key 键
     * @return
     */
    public static Long string_remove(String key) {
        return RedisStringClient.remove(key);
    }

    /**
     * 是否存在
     * @param key 键
     * @return
     */
    public static boolean string_exist(String key) {
        return RedisStringClient.exist(key);
    }

    /**
     * 是否存在-加锁
     * @param key 键
     * @return
     */
    public static boolean string_exist(String key,String lock) {
        return RedisStringClient.exist(key,lock);
    }

    /**
     * 整数递增
     * @param key 键
     * @return
     */
    public static long string_incr(String key) {
        return RedisStringClient.incr(key);
    }

    /**
     * 整数递减
     * @param key 键
     * @return
     */
    public static long string_decr(String key) {
        return RedisStringClient.decr(key);
    }
    /**********************************************/
    /**
     * 获取redis
     * @return
     */
    public static Jedis string_getJedis() {
        Jedis jedis = RedisStringClient.getJedisPub();
        return jedis;
    }
    /**
     * 获取key
     * @param key 键
     * @return
     */
    public static String string_getKey(String key) {
        return RedisStringClient.getKeyPub(key);
    }
    /**
     * 获取锁
     * @param key 键
     * @return
     */
    public static boolean string_lock(Jedis jedis,String key) {
        return RedisStringClient.lockPub(jedis, key);
    }
    /**
     * 释放锁
     * @param key 键
     * @return
     */
    public static boolean string_unlock(Jedis jedis,String key) {
        return RedisStringClient.unlockPub(jedis, key);
    }
    /**
     * 设置数据
     * @param key 键
     * @param value 值
     * @return
     */
    public static void string_set(Jedis jedis,String key,String value) {
        RedisStringClient.setPub(jedis, key, value);
    }
    /**
     * 设置数据
     * @param key 键
     * @param value 值
     * @return
     */
    public static void string_set(Jedis jedis,String key,Object value) {
        RedisStringClient.setPub(jedis, key, value);
    }
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static String string_get(Jedis jedis,String key) {
        return RedisStringClient.getPub(jedis, key);
    }
    /**
     * 设置数据
     * @param key 键
     * @param value 值
     * @return
     */
    public static void string_set(Jedis jedis,String key,int seconds,Object value) {
        RedisStringClient.setPub(jedis, key,seconds,value);
    }
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static Object string_get_obj(Jedis jedis,String key) {
        return RedisStringClient.getObjPub(jedis, key);
    }
    /**
     * 是否存在
     * @param key 键
     * @return
     */
    public static boolean string_exist(Jedis jedis,String key) {
        return RedisStringClient.existPub(jedis, key);
    }
    /**
     * 是否存在
     * @param key 键
     * @return
     */
    public static Long string_remove(Jedis jedis,String key) {
        return RedisStringClient.removePub(jedis, key);
    }

    /***************************************string-end**************************************************/
    /**
     * 单条get
     * @param key 键
     * @param field 域
     */
    public static String hash_get(String key, String field) {
        return RedisHashClient.get(key, field);
    }
    public static Object hash_getObject(String key, String field) {
        return RedisHashClient.getObject(key, field);
    }
    public static byte[] hash_get_byte(String key, String field) {
        return RedisHashClient.getByteArr(key, field);
    }

    /**
     * 单条set
     * @param key 键
     * @param field 域
     * @param value 值
     */
    public static void hash_set(String key, String field, Object value) {
        RedisHashClient.set(key, field,value);
    }
    /**
     * 单条set
     * @param key 键
     * @param field 域
     * @param value 值
     */
    public static void hash_set(String key, String field, String value) {
        RedisHashClient.set(key, field,value);
    }

    /**
     * 设置过期时间
     * @param key 键
     * @param seconds 有效时长（秒）
     * @return
     */
    public static Long hash_setExpire(String key,int seconds) {
        return RedisHashClient.setExpire(key,seconds);
    }

    /**
     * 单条set，当不存在时才插入
     * @param key 键
     * @param field 域
     * @param value 值
     */
    public static void hash_setnx(String key, String field, Object value) {
        RedisHashClient.setnx(key, field,value);
    }
    /**
     * 单条set，当不存在时才插入
     * @param key 键
     * @param field 域
     * @param value 值
     */
    public static void hash_setnx(String key, String field, String value) {
        RedisHashClient.setnx(key, field,value);
    }

    /**
     * 单条set，当不存在时才插入
     * @param key 键
     * @param field 域
     * @param value 值
     */
    public static void hash_setnx_lock(String key, String field, String value) {
        RedisHashClient.setnx_lock(key, field,value);
    }

    /**
     * 判断字段是否存在：存在返回1 ，不存在返回0
     * @param key 键
     * @param field 域值
     * @return
     */
    public static boolean hash_exist(String key,String field,String lock) {
        return RedisHashClient.exist(key,field,lock);
    }
    /**
     * 判断字段是否存在：存在返回1 ，不存在返回0
     * @param key 键
     * @param field 域值
     * @return
     */
    public static boolean hash_exists(String key,String field) {
        return RedisHashClient.exist(key,field);
    }
    /**
     * 判断字段是否存在：存在返回1 ，不存在返回0
     * @param key 键
     * @param lock 域值
     * @return
     */
    public static boolean hash_exist(String key,String lock) {
        return RedisHashClient.exists(key,lock);
    }

    /**
     * 自增increment
     * @param key 键
     * @param field 域
     * @param integer 增加值
     * @return
     */
    public static Long hash_incrBy(String key,String field,long integer) {
        return RedisHashClient.incrBy(key,field,integer);
    }
    /**
     * 自增increment
     * @param key 键
     * @param field 域
     * @param integer 增加值
     * @return
     */
    public static Long hash_incrByAi(String key,String field,long integer) {
        return RedisHashClient.incrByAi(key,field,integer);
    }

    /**
     * 自增increment
     * @param key 键
     * @param field 域
     * @param integer 增加值
     * @return
     */
    public static Double hash_incrByDou(String key,String field,Double integer) {
        return RedisHashClient.incrBy1(key,field,integer);
    }

    /**
     * 单条删除
     * @param key 键
     * @param field 域
     * @return
     */
    public static Long hash_remove(String key,String field) {
        return RedisHashClient.remove(key,field);
    }
    /**
     * 删除key下全部数据
     * @param key 键
     * @return
     */
    public static Long hash_del(String key) {
        return RedisHashClient.del(key);
    }
    /**
     * 所以key下field列表
     * @param key 键
     * @return
     */
    public static Map<String, String> hash_getFields(String key) {
        return RedisHashClient.getStrings(key);
    }
    /**
     * 所以key下field列表
     * @param key 键
     * @return
     */
    public static Map<String, String> hash_getFields_ai(String key) {
        return RedisHashClient.getStrings_ai(key);
    }
    /**
     * 获取key下所有列表
     * @param key 键
     * @return
     */
    public static Map<String, Object> hash_getObjectMap(String key) {
        return RedisHashClient.getObjectMap(key);
    }
    /**********************************************/
    /**
     * 获取redis
     * @return
     */
    public static Jedis hash_getJedis() {
        Jedis jedis = RedisHashClient.getJedisPub();
        return jedis;
    }
    /**
     * 获取key
     * @param key 键
     * @return
     */
    public static String hash_getKey(String key) {
        return RedisHashClient.getKeyPub(key);
    }
    /**
     * 获取锁
     * @param key 键
     * @return
     */
    public static boolean hash_lock(Jedis jedis,String key) {
        return RedisHashClient.lockPub(jedis, key);
    }
    /**
     * 释放锁
     * @param key 键
     * @return
     */
    public static boolean hash_unlock(Jedis jedis,String key) {
        return RedisHashClient.unlockPub(jedis, key);
    }
    /**
     * 设置数据
     * @param key 键
     * @param value 值
     * @return
     */
    public static void hash_set(Jedis jedis,String key,String field,String value) {
        RedisHashClient.setPub(jedis, key,field, value);
    }
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static String hash_get(Jedis jedis,String key,String field) {
        return RedisHashClient.getPub(jedis, key,field);
    }
    /**
     * 是否存在
     * @param key 键
     * @return
     */
    public static boolean hash_exist(Jedis jedis,String key,String field) {
        return RedisHashClient.existPub(jedis, key,field);
    }

    /***************************************zset-begin**************************************************/
    /**
     * 单条add
     * @param key 键
     * @param score 排序
     * @param member 成员
     * @return
     */
    public static void zset_set(String key,double score, String member) {
        RedisZSetClient.set(key,score, member);
    }
    /**
     * 取得特定范围内的倒序排序元素,0代表第一个元素,1代表第二个以此类推。-1代表最后一个,-2代表倒数第二个.
     * @param key 键
     * @param start score最小值
     * @param end score最大值
     * @return
     */
    public static List<String> zset_zrevrange(String key,long start, long end) {
        return RedisZSetClient.zrevrange(key,start, end);
    }
    /**
     * 取得特定范围内的排序元素,0代表第一个元素,1代表第二个以此类推。-1代表最后一个,-2代表倒数第二个.
     * @param key 键
     * @param start score最小值
     * @param end score最大值
     * @return
     */
    public static List<String> zset_range(String key,long start, long end) {
        return RedisZSetClient.range(key,start, end);
    }
    /**
     * 倒序取得特定范围内的排序元素,0代表第一个元素,1代表第二个以此类推。-1代表最后一个,-2代表倒数第二个.
     * @param key 键
     * @param start 元素开始位置
     * @param end 元素结束位置
     * @param scores score
     * @param members 成员
     * @return
     */
    public static int zset_revrangeWithScore(String key,long start, long end, final List<Double> scores, final List<String> members) {
        return RedisZSetClient.revrangeWithScore(key,start, end,scores,members);
    }
    /**
     * 根据set的score顺序取
     * @param key 键
     * @param min score最小值
     * @param max score最大值
     * @return
     */
    public static List<String> zset_rangeByScore(String key,double min, double max) {
        return RedisZSetClient.rangeByScore(key,min, max);
    }
    /**
     * 单条删除
     * @param key 键
     * @param member 成员
     * @return
     */
    public static boolean zset_remove(String key,String member) {
        return RedisZSetClient.remove(key,member);
    }
    /**
     * 删除key下全部数据
     * @param key 键
     * @return
     */
    public static Long zset_del(String key) {
        return RedisZSetClient.del(key);
    }

    /***************************************platform-string-begin**************************************************/
    /**
     * 添加数据
     * @param key   key
     * @param seconds   超时时间
     * @param value value
     */
    public static void p_string_set(String key,int seconds, Object value) {
        RedisStringPClient.set(key,seconds, value);
    }

    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void p_string_set(String key,String value) {
        RedisStringPClient.set(key, value);
    }

    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void p_string_set(String key,String value,String lock) {
        RedisStringPClient.set(key, value,lock);
    }

    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void p_string_set(String key,Object value) {
        RedisStringPClient.set(key, value);
    }

    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void p_string_set(String key,Object value,String lock) {
        RedisStringPClient.set_lock(key, value);
    }

    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static String p_string_get(String key) {
        return RedisStringPClient.get(key);
    }

    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static String p_string_get(String key,String lock) {
        return RedisStringPClient.get(key,lock);
    }

    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static Object p_string_get_obj(String key) {
        return RedisStringPClient.getObject(key);
    }

    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static Object p_string_get_obj(String key,String lock) {
        return RedisStringPClient.getObject_lock(key);
    }

    /**
     * 删除数据-加锁
     * @param key 键
     * @return
     */
    public static Long p_string_remove(String key,String lock) {
        return RedisStringPClient.remove(key,lock);
    }

    /**
     * 删除数据
     * @param key 键
     * @return
     */
    public static Long p_string_remove(String key) {
        return RedisStringPClient.remove(key);
    }

    /**
     * 是否存在
     * @param key 键
     * @return
     */
    public static boolean p_string_exist(String key) {
        return RedisStringPClient.exist(key);
    }

    /**
     * 是否存在-加锁
     * @param key 键
     * @return
     */
    public static boolean p_string_exist(String key,String lock) {
        return RedisStringPClient.exist(key,lock);
    }

    /**
     * 整数递增
     * @param key 键
     * @return
     */
    public static long p_string_incr(String key) {
        return RedisStringPClient.incr(key);
    }

    /**
     * 整数递增
     * @param key 键
     * @param integer 指定的整数
     * @return
     */
    public static long p_string_incr(String key,long integer) {
        return RedisStringPClient.incr(key,integer);
    }

    /**
     * 整数递减
     * @param key 键
     * @return
     */
    public static long p_string_decr(String key) {
        return RedisStringPClient.decr(key);
    }
}
