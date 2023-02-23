package com.xzkj.apiController.redis.utils;



import com.xzkj.apiController.redis.client.RedisFifoClient;

import java.util.List;

/**
 * Redis 队列缓存
 * @author admin
 * @since 2017/10/10.
 */
public class RedisFifoUtils {
	
	/**
     * 添加到队头
     * @param key   key
     * @param value value
     */
    public static void lpush(String key, String value) {
    	RedisFifoClient.lpush(key, value);
    }
    
    /**
     * 添加到队尾
     * @param key   key
     * @param value value
     */
    public static void push(String key, String value) {
    	RedisFifoClient.push(key, value);
    }
    
    /**
     * 添加到队头
     * @param key   key
     * @param value value
     */
    public static void lpushObject(String key, Object value) {
    	RedisFifoClient.lpushObject(key, value);
    }
    
    /**
     * 添加到队尾object
     * @param key   key
     * @param value value
     */
    public static void pushObject(String key, Object value) {
    	RedisFifoClient.pushObject(key, value);
    }

    /**
     * 取一条
     * @param key KEY
     * @return string
     */
    public static String lindex(String key, int index) {
        return RedisFifoClient.lindex(key, index);
    }
    
    /**
     * 从队首取数据
     * @param key KEY
     * @return string
     */
    public static String pop(String key) {
        return RedisFifoClient.pop(key);
    }
    
    /**
     * 取一条
     * @param key KEY
     * @return 
     * @return string
     */
    public static Object lindexObject(String key, int index) {
    	return RedisFifoClient.lindexObject(key, index);
    }
    
    /**
     * 从队首取数据
     * @param key KEY
     * @return string
     */
    public static Object popObject(String key) {
    	return RedisFifoClient.popObject(key);
    }
    
    /**
	 * 取一段
	 * @param key 键
	 * @param start 起始下标
	 * @param end 结束下标
	 * @return
	 */
    public static List<Object> rangeObject(String key, long start, long end) {
		return RedisFifoClient.rangeObject(key, start, end);
    }
    
    /**
     * 取一段
     * @param key 键
     * @param start 起始下标
     * @param end 结束下标
     * @return
     */
    public static List<Object> range(String key, long start, long end) {
    	return RedisFifoClient.range(key, start, end);
    }
    
    /**
	 * 队列大小
	 * @param key 键
	 * @return
	 */
    public static long len(String key) {
        return RedisFifoClient.len(key);
	}

}
