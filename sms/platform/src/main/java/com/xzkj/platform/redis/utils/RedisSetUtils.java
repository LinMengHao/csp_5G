package com.xzkj.platform.redis.utils;


import com.xzkj.platform.redis.client.RedisSetClient;

/**
 * Redis 字符串缓存
 * @author admin
 * @since 2017/10/10.
 */
public class RedisSetUtils {
	
	/**
	 * 添加到集合 
	 * @param key 键
	 * @param member 成员
	 * @return
	 */
    public static void set(String key, String member) {
    	RedisSetClient.set(key, member);
    }
    

}
