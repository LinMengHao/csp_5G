package com.xzkj.taskChannel.redis.entity;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * set的封装
 * @author admin
 */
public class RdisSet extends RedisLock {

	/**
	 * 添加到集合 
	 * @param j 数据源连接
	 * @param key 键
	 * @param member 成员
	 * @return
	 */
	public boolean add(Jedis j, String key, byte[] member) {
		j.sadd(key.getBytes(), member);
		return true;
	}

	/**
	 * 添加到集合 
	 * @param j 数据源连接
	 * @param key 键
	 * @param members 成员
	 * @return
	 */
	public boolean add(Jedis j, String key, byte[][] members) {
		j.sadd(key.getBytes(), members);
		return true;
	}

	/**
	 * 获取全部 
	 * @param j 数据源连接
	 * @param key 键
	 * @return 集合
	 */
	public Set<byte[]> members(Jedis j, String key) {
		return j.smembers(key.getBytes());
	}

	/**
	 * 删除 
	 * @param j 数据源连接
	 * @param key 键
	 * @param member 成员
	 * @return
	 */
	public boolean rem(Jedis j, String key, byte[] member) {
		j.srem(key.getBytes(), member);
		return true;
	}

	/**
	 * 添加到集合 
	 * @param j 数据源连接
	 * @param key 键
	 * @param members 成员
	 * @return
	 */
	public boolean rem(Jedis j, String key, byte[][] members) {
		j.srem(key.getBytes(), members);
		return true;
	}

	/**
	 * 随机弹出 
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public byte[] spop(Jedis j, String key) {
		return j.spop(key.getBytes());
	}

	/**
	 * 判断是否存在 
	 * @param j 数据源连接
	 * @param key 键
	 * @param member 成员
	 * @return
	 */
	public boolean ismember(Jedis j, String key, byte[] member) {
		return j.sismember(key.getBytes(), member);
	}
}
