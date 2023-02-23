package com.xzkj.taskPorxy.redis.common;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis连接资源池
 * @author admin
 */
public class JedisDataSourcePool {
	// 资源配置
	private static ConcurrentHashMap<String, String> redisSourceUrlMap;
	// 链接池
	private ConcurrentHashMap<String, JedisPool> internalPool = new ConcurrentHashMap<String, JedisPool>();

	/**
	 * 初始化
	 */
	public void init_dependence() {
		String propertyPath = "/redis.properties";
		PropertyUtil properUtil = new PropertyUtil(propertyPath);
		ConcurrentHashMap<String, String> p = properUtil.getProperties();
		if (p != null) {
			redisSourceUrlMap = p;
		}
	}

	/**
	 * 获取连接资源
	 * @param key key
	 * @return
	 */
	public JedisPool getJedisPool(String key) {
		JedisPool jedisPool = internalPool.get(key);
		if (jedisPool != null) {
			return jedisPool;
		} else {
			String sourceURL = redisSourceUrlMap.get(key);
			if (sourceURL == null) {
				return null;
			}
			jedisPool = createJedisPool(key, sourceURL);
			internalPool.put(key, jedisPool);
			return jedisPool;
		}
	}

	/**
	 * 获取连接资源
	 * @param key key
	 * @return
	 */
	public JedisPool getJedisPool(String key, String defaultKey) {
		JedisPool jedisPool = internalPool.get(key);
		if (jedisPool != null) {
			return jedisPool;
		} else {
			String sourceURL = redisSourceUrlMap.get(key);
			if (sourceURL == null) {
				sourceURL = redisSourceUrlMap.get(defaultKey);
			}
			jedisPool = createJedisPool(key, sourceURL);
			internalPool.put(key, jedisPool);
			return jedisPool;
		}
	}

	/**
	 * 创建
	 * @param host
	 * @return
	 */
	private JedisPool createJedisPool(String key, String host) {
		URI uri = URI.create(host);
		if (uri.getScheme() != null && uri.getScheme().equals("redis")) {
			String h = uri.getHost();
			int port = uri.getPort();
			String password = uri.getUserInfo().split(":", 2)[1];
			int database = Integer.parseInt(uri.getPath().split("/", 2)[1]);
			return new JedisPool(getJedisPoolConfig(), h, port, 10000, password, database, key);
		} else {
			return null;
		}
	}

	/**
	 * getJedisPoolConfig
	 * @return
	 */
	private JedisPoolConfig getJedisPoolConfig() {
		// 建立连接池配置参数
		JedisPoolConfig config = new JedisPoolConfig();
		// 设置空闲连接, 默认8个
		config.setMaxIdle(100);
		config.setMinIdle(10);
		// 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
		config.setMaxWaitMillis(60 * 1000);
		// 设置最大连接数, 默认8个
		config.setMaxTotal(2000);
		config.setBlockWhenExhausted(true);
		config.setTestWhileIdle(true);
		config.setMinEvictableIdleTimeMillis(60 * 1000);
		config.setTimeBetweenEvictionRunsMillis(30 * 1000);
		config.setNumTestsPerEvictionRun(-1);
		config.setTestWhileIdle(true);
		//在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
		config.setTestOnBorrow(true);
		return config;
	}
}
