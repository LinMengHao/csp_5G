package com.xzkj.taskChannel.redis.utils;



import com.xzkj.taskChannel.redis.client.RedisStringClient;

import java.util.List;
import java.util.Map;

/**
 * Redis 字符串缓存
 * @author admin
 * @since 2017/10/10.
 */
public class RedisStringUtils {
	
	/**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void set(String key, String value) {
    	RedisStringClient.set(key, value);
    }
    
    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void set(String key, Object value) {
    	RedisStringClient.set(key, value);
    }
    
    /**
     * 添加数据
     * @param key   key
     * @param seconds   超时时间
     * @param value value
     */
    public static void set(String key,int seconds, String value) {
    	RedisStringClient.set(key,seconds, value);
    }
    
    /**
     * 添加数据
     * @param key   key
     * @param seconds   超时时间
     * @param value value
     */
    public static void set(String key,int seconds, Object value) {
    	RedisStringClient.set(key,seconds, value);
    }
    
    /**
	 * 获取数据
	 * @param key 键
	 * @return
	 */
    public static String get(String key) {
    	return RedisStringClient.get(key);
    }
    
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static Object getObject(String key) {
    	return RedisStringClient.getObject(key);
    }
    
    /**
	 * 批量添加数据
	 * @param map 数据
	 * @param seconds 超时时间
	 * @return
	 */
    public static void setStrings(Map<String, String> map, int seconds) {
    	RedisStringClient.setStrings(map,seconds);
    }
    
    /**
     * 批量添加数据
     * @param map 数据
     * @param seconds 超时时间
     * @return
     */
    public static void setObjects(Map<String, Object> map, int seconds) {
    	RedisStringClient.setObjects(map,seconds);
    }
    
    /**
	 * 批量获取数据
	 * @param keys 键值列表
	 * @return
	 */
    public static Map<String, String> getStrings(List<String> keys) {
    	return RedisStringClient.getStrings(keys);
    }
    
    /**
     * 批量获取数据
     * @param keys 键值列表
     * @return
     */
    public static Map<String, Object> getObjects(List<String> keys) {
    	return RedisStringClient.getObjects(keys);
    }
    
    /**
	 * 移除数据
	 * @param key 键
	 * @return
	 */
    public static Long remove(String key) {
        return RedisStringClient.remove(key);
	}
    
    /**
     * 批量移除数据
     * @param key 键
     * @return
     */
    public static Long removeKeys(List<String> keys) {
    	return RedisStringClient.removeKeys(keys);
    }
    
    /**
	 * 是否存在
	 * @param key 键
	 * @return
	 */
    public static boolean exist(String key,String lock) {
    	return RedisStringClient.exist(key,lock);
    }
    
    /**
	 * 获取长度
	 * @param key 键
	 * @return
	 */
    public static Long strlen(String key) {
    	return RedisStringClient.strlen(key);
    }
    
    /**
     * 整数递增
     * @param key 键
     * @return
     */
    public static Long incr(String key) {
    	return RedisStringClient.incr(key);
    }
    
    /**
     * 整数递增
     * @param key 键
     * @param integer 指定的整数
     * @return
     */
    public static Long incr(String key,long integer) {
    	return RedisStringClient.incr(key,integer);
    }
    
    /**
     * 整数递减
     * @param key 键
     * @return
     */
    public static Long decr(String key) {
    	return RedisStringClient.decr(key);
    }
    
    /**
     * 整数递减
     * @param key 键
     * @param integer 指定的整数
     * @return
     */
    public static Long decr(String key,long integer) {
    	return RedisStringClient.decr(key,integer);
    }
    
    /**
     * 增加指定的浮点数
     * @param key 键
     * @param integer 指定的浮点数
     * @return
     */
    public static Double incrByFloat(String key,double integer) {
    	return RedisStringClient.incrByFloat(key,integer);
    }
    
    /**
	 * 向尾部追加字符串值
	 * @param key 键
	 * @param value 追加的值
	 * @return
	 */
    public static Long incrByFloat(String key,String value) {
    	return RedisStringClient.incrByFloat(key,value);
    }

}
