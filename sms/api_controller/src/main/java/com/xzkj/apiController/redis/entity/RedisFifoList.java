package com.xzkj.apiController.redis.entity;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 先进先出队列的封装
 * @author admin
 */
public class RedisFifoList extends RedisLock {

	/**
	 * 添加到队头
	 * @param j 数据源连接
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public boolean lpush(Jedis j, String key, byte[] value) {
		j.lpush(key.getBytes(), value);
		return true;
	}

	/**
	 * 批量添加到队头
	 * @param j 数据源连接
	 * @param key 键
	 * @param values 值
	 * @return
	 */
	public boolean lpush(Jedis j, String key, byte[][] values) {
		j.lpush(key.getBytes(), values);
		return true;
	}

	/**
	 * 添加到队尾
	 * @param j 数据源连接
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public boolean push(Jedis j, String key, byte[] value) {
		j.rpush(key.getBytes(), value);
		return true;
	}

	/**
	 * 批量添加到队尾
	 * @param j 数据源连接
	 * @param key 键
	 * @param values 值
	 * @return
	 */
	public boolean push(Jedis j, String key, byte[][] values) {
		j.rpush(key.getBytes(), values);
		return true;
	}

	/**
	 * 从队首取数据
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public byte[] pop(Jedis j, String key) {
		return j.lpop(key.getBytes());
	}

	/**
	 * 队列大小
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public long len(Jedis j, String key) {
		return j.llen(key.getBytes());
	}

	/**
	 * 取一段
	 * @param j 数据源连接
	 * @param key 键
	 * @param start 起始下标
	 * @param end 结束下标
	 * @return
	 */
	public List<byte[]> range(Jedis j, String key, long start, long end) {
		return j.lrange(key.getBytes(), start, end);
	}

	/**
	 * 取一条
	 * @param j 数据源连接
	 * @param key 键
	 * @param index 下标
	 * @return
	 */
	public byte[] index(Jedis j, String key, int index) {
		return j.lindex(key.getBytes(), index);
	}
	
	/**
	 * 移除数据
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public Long remove(Jedis j, String key) {
        return j.del(key.getBytes());
    }
	
	/**
	 * 是否存在
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public boolean exist(Jedis j,String key) {
        return j.exists(key.getBytes());
    }
}
