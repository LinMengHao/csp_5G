package com.xzkj.taskProxy.redis.utils;


import com.xzkj.taskProxy.redis.client.RedisHashClient;

/**
 * Redis 字符串缓存
 * @author admin
 * @since 2017/10/10.
 */
public class RedisHashUtils {
	
	/**
     * 添加数据
	 * @param key 键
	 * @param field 域
	 * @param value 值
	 * @return
	 */
    public static void set(String key,String field, String value) {
    	RedisHashClient.set(key,field, value);
    }
    
    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void set(String key,String field, Object value) {
    	RedisHashClient.set(key,field, value);
    }
    
}
