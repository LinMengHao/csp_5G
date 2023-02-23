package com.xzkj.taskChannel.redis.entity;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hash表的封装
 * @author admin
 */
public class RedisHash extends RedisLock {

	/**
	 * 单条set
	 * @param j 数据源连接
	 * @param key 键
	 * @param field 域
	 * @param value 值
	 * @return
	 */
	public boolean set(Jedis j, String key, byte[] field, byte[] value) {
		j.hset(key.getBytes(), field, value);
		return true;
	}

	/**
	 * 单条set，当不存在时才插入 
	 * @param j 数据源连接
	 * @param key 键
	 * @param field 域
	 * @param value 值
	 * @return
	 */
	public boolean setnx(Jedis j, String key, byte[] field, byte[] value) {
		j.hsetnx(key.getBytes(), field, value);
		return true;
	}

	/**
	 * 批量set 
	 * @param j 数据源连接
	 * @param key 键
	 * @param fieldvalues 域值
	 * @return
	 */
	public boolean set(Jedis j, String key, Map<byte[], byte[]> fieldvalues) {
		j.hmset(key.getBytes(), fieldvalues);
		return true;
	}

	/**
	 * 单条删除 
	 * @param j 数据源连接
	 * @param key 键
	 * @param field 域
	 * @return
	 */
	public Long del(Jedis j, String key, byte[] field) {
		return j.hdel(key.getBytes(), field);
	}
	
	/**
	 * 删除全部 
	 * @param j 数据源连接
	 * @param key 键
	 * @param field 域
	 * @return
	 */
	public Long del(Jedis j, String key) {
		return j.del(key.getBytes());
	}

	/**
	 * 批量删除 
	 * @param j 数据源连接
	 * @param key 键
	 * @param fields 域
	 * @return
	 */
	public Long del(Jedis j, String key, byte[][] fields) {
		return j.hdel(key.getBytes(), fields);
	}

	/**
	 * 单条查询 
	 * @param j 数据源连接
	 * @param key 键
	 * @param field 域
	 * @return
	 */
	public byte[] get(Jedis j, String key, byte[] field) {
		return j.hget(key.getBytes(), field);
	}

	/**
	 * 批量查询 
	 * @param j 数据源连接
	 * @param key 键
	 * @param fields 域
	 * @return
	 */
	public Map<byte[], byte[]> get(Jedis j, String key, byte[][] fields) {
		List<byte[]> res = j.hmget(key.getBytes(), fields);
		if (res != null && res.size() > 0) {
			Map<byte[], byte[]> ret = new HashMap<byte[], byte[]>();
			for (int i = 0; i < fields.length; i++) {
				ret.put(fields[i], ret.get(i));
			}
			return ret;
		}
		return null;
	}

	/**
	 * 列表 
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public Map<byte[], byte[]> getAll(Jedis j, String key) {
		return j.hgetAll(key.getBytes());
	}

	/**
	 * 迭代方式查询 
	 * @param s 数据源连接
	 * @param hmap 结果集
	 * @param key 键
	 * @param cursor 游标
	 * @param match 给定模式
	 * @param count 结果集条数
	 * @return
	 */
	public byte[] scan(Jedis j, final Map<byte[], byte[]> hmap, String key, byte[] cursor, String match, int count) {
		final ScanParams params = new ScanParams();
		if (match != null && !"".equals(match)) {
			params.match(match.getBytes());
		}
		if (count != 0) {
			params.count(count);
		}
		ScanResult<Map.Entry<byte[], byte[]>> orgResult = j.hscan(key.getBytes(), cursor, params);
		cursor = orgResult.getCursorAsBytes();
		//
		List<Map.Entry<byte[], byte[]>> res = orgResult.getResult();
		if (res != null) {
			for (Map.Entry<byte[], byte[]> line : res) {
				hmap.put(line.getKey(), line.getValue());
			}
		}
		//
		return cursor;
	}

	/**
	 * 判断字段是否存在：存在返回1 ，不存在返回0
	 * @param j 数据源连接
	 * @param key 键
	 * @param field 域值
	 * @return
	 */
	public boolean hexists(Jedis j, String key,byte[] field) {
		return j.hexists(key.getBytes(), field);
	}
	/**
	 * 判断字段是否存在：存在返回1 ，不存在返回0
	 * @param j 数据源连接
	 * @param key 键
	 * @param field 域值
	 * @return
	 */
	public boolean hexists(Jedis j, String key) {
		return j.exists(key.getBytes());
	}
	
	/**
	 * 集合大小 
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public long len(Jedis j, String key) {
		return j.hlen(key.getBytes());
	}

	/**
	 * 自增increment 
	 * @param j 数据源连接
	 * @param key 键
	 * @param field 域
	 * @param increment 增加值
	 * @return
	 */
	public long hincrBy(Jedis j, String key, byte[] field, long increment) {
		return j.hincrBy(key.getBytes(), field, increment);
	}
	
	/**
	 * 自增increment 
	 * @param j 数据源连接
	 * @param key 键
	 * @param field 域
	 * @param increment 增加值
	 * @return
	 */
	public Double hincrDoubleBy(Jedis j, String key, byte[] field, Double increment) {
		return j.hincrByFloat(key.getBytes(), field, increment);
	}

	/**
	 * 设置过期时间
	 * @param j 数据源连接
	 * @param key 键
	 * @param seconds 有效时长（秒）
	 * @return
	 */
	public long setExpire(Jedis j, String key,int seconds) {
		return j.expire(key.getBytes(), seconds);
	}

}
