package com.xzkj.apiService.redis;


import com.xzkj.apiService.redis.client.*;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

public class RedisUtils {

    //数据表的主键最大索引值 key,model/sign,100
    public static String HASH_TABLE_MAX_INDEX="hash_table_max_index";

    //渠道状态报告回执key:channel_id:message,mobile,json
    public static String HASH_MMS_MT_CHANNEL="mt_channel:";
    //客户状态报告推送队列key：companyid
    public static String FIFO_MMS_MT_CLIENT="mt_client:";
    //客户状态报告数 key,companyId
    public static String HASH_MMS_MT_COUNT="mt_count";
    //客户状态报告重推
    public static String FIFO_REPORT_LIST="fifo_report_list";

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
    //客户签名提交数据队列计数 key,companyId_appId,100
    public static String HASH_APP_SIGN_TOTAL="hash_app_sign_total";
    //客户签名提交数据队列 key:companyId:appId,json
    public static String FIFO_APP_SIGN_LIST="fifo_app_sign_list:";

    //模板提交通道数据队列计数 key,companyId_appId,100
    public static String HASH_CHANNEL_MODEL_TOTAL="hash_channel_model_total";

    //模板提交通道数据队列 key:companyId:appId,json
    public static String FIFO_CHANNEL_MODEL_LIST="fifo_channel_model_list:";

    public static String HASH_CHANNEL_SIGN_TOTAL="hash_channel_sign_total";
    //签名提交通道数据队列 key:companyId:appId,json
    public static String FIFO_CHANNEL_SIGN_LIST="fifo_channel_sign_list:";

    //客户视频发送提交数据队列计数 key,companyId_appId,100
    public static String HASH_APP_REQ_TOTAL="hash_app_req_total";
    //客户视频发送提交数据队列 key:companyId:appId,json
    public static String FIFO_APP_REQ_LIST="fifo_app_req_list:";
    //上行数据队列计数 key,companyId_appId,100
    public static String HASH_APP_MO_TOTAL="hash_app_mo_total";
    //上行数据队列 key:companyId:appId,json
    public static String FIFO_APP_MO_LIST="fifo_app_mo_list:";
    //渠道状态报告回执key：channelid
    public static String HASH_MMS_UN_MATCH="UNMATCH:";
    //客户状态报告推送队列key：appid
    public static String FIFO_MMS_DELIVRD_TUBE="AD:";
    //商户某项目数据库提交队列表eg：key:companyId
    public static String FIFO_SQL_LIST="fifo_sql_list:";
    //商户下的数据库操作 key,companyId
    public static String HASH_SQL_COUNT="hash_sql_count";
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
