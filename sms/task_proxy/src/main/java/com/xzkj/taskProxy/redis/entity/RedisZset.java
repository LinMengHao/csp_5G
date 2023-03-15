package com.xzkj.taskProxy.redis.entity;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * set集合的封装
 * @author admin
 */
public class RedisZset extends RedisLock {

	/**
	 * 单条add 
	 * @param j 数据源连接
	 * @param key 键
	 * @param score 排序
	 * @param member 成员
	 * @return
	 */
	public boolean add(Jedis j, String key, double score, byte[] member) {
		j.zadd(key.getBytes(), score, member);
		return true;
	}

	/**
	 * 批量add 
	 * @param j 数据源连接
	 * @param key 键
	 * @param scores 排序
	 * @param members 成员
	 * @return
	 */
	public boolean add(Jedis j, String key, double[] scores, byte[][] members) {
		final Map<byte[], Double> scoreMembers = new HashMap<byte[], Double>();
		for (int i = 0; i < scores.length; i++) {
			scoreMembers.put(members[i], scores[i]);
		}
		j.zadd(key.getBytes(), scoreMembers);
		return true;
	}

	/**
	 * 单条删除 
	 * @param j 数据源连接
	 * @param key 键
	 * @param member 成员
	 * @return
	 */
	public boolean rem(Jedis j, String key, byte[] member) {
		j.zrem(key.getBytes(), member);
		return true;
	}

	/**
	 * 批量删除 
	 * @param j 数据源连接
	 * @param key  键
	 * @param members 成员
	 * @return
	 */
	public boolean rem(Jedis j, String key, byte[][] members) {
		j.zrem(key.getBytes(), members);
		return true;
	}
	/**
	 * 删除全部 
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public Long del(Jedis j, String key) {
		return j.del(key.getBytes());
	}
	/**
	 * 集合大小 
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public long len(Jedis j, String key) {
		return j.zcard(key.getBytes());
	}

	/**
	 * 根据set倒序取 
	 * @param j 数据源连接
	 * @param key 键
	 * @param start 开始位置
	 * @param end 结束位置
	 * @return
	 */
	public Set<byte[]> zrevrange(Jedis j, String key, long start, long end) {
		return j.zrevrange(key.getBytes(), start, end);
	}
	
	/**
	 * 根据set顺序取 
	 * @param j 数据源连接
	 * @param key 键
	 * @param start 开始位置
	 * @param end 结束位置
	 * @return
	 */
	public Set<byte[]> range(Jedis j, String key, long start, long end) {
		return j.zrange(key.getBytes(), start, end);
	}

	/**
	 * 返回有序集 key 中，指定区间内的成员 
	 * @param j 数据源连接
	 * @param key 键
	 * @param start 开始位置
	 * @param end 结束位置
	 * @param scores score
	 * @param members 成员
	 * @return
	 */
	public int rangeWithScore(Jedis j, String key, long start, long end, final List<Double> scores, final List<byte[]> members) {
		Set<Tuple> res = j.zrangeWithScores(key, start, end);
		if (res != null && res.size() > 0) {
			for (Tuple r : res) {
				scores.add(r.getScore());
				members.add(r.getBinaryElement());
			}
			return res.size();
		}
		return 0;
	}
	/**
	 * 倒序返回有序集 key 中，指定区间内的成员
	 * @param j 数据源连接
	 * @param key 键
	 * @param start 开始位置
	 * @param end 结束位置
	 * @param scores score
	 * @param members 成员
	 * @return
	 */
	public int revrangeWithScore(Jedis j, String key, long start, long end, final List<Double> scores, final List<String> members) {
		Set<Tuple> res = j.zrevrangeWithScores(key, start, end);
		if (res != null && res.size() > 0) {
			for (Tuple r : res) {
				scores.add(r.getScore());
				members.add(new String(r.getBinaryElement()));
			}
			return res.size();
		}
		return 0;
	}
	/**
	 * 根据set的score顺序取 
	 * @param j 数据源连接
	 * @param key 键
	 * @param min score最小值
	 * @param max score最大值
	 * @param offset 开始
	 * @param count 条数
	 * @return
	 */
	public Set<byte[]> rangeByScore(Jedis j, String key, double min, double max, int offset, int count) {
		return j.zrangeByScore(key.getBytes(), min, max, offset, count);
	}

	/**
	 * 根据set的score顺序取 
	 * @param j 数据源连接
	 * @param key  键
	 * @param min score最小值
	 * @param max score最大值
	 * @return
	 */
	public Set<byte[]> rangeByScore(Jedis j, String key, double min, double max) {
		return j.zrangeByScore(key.getBytes(), min, max);
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员 
	 * @param j 数据源连接
	 * @param key 键
	 * @param min score最小值
	 * @param max score最大值
	 * @param offset 开始
	 * @param count 条数
	 * @param scores score
	 * @param members 成员
	 * @return
	 */
	public int rangeByScoreWithScores(Jedis j, String key, double min, double max, int offset, int count, final List<Double> scores, final List<byte[]> members) {
		Set<Tuple> res = j.zrangeByScoreWithScores(key, min, max, offset, count);
		if (res != null && res.size() > 0) {
			for (Tuple r : res) {
				scores.add(r.getScore());
				members.add(r.getBinaryElement());
			}
			return res.size();
		}
		return 0;
	}

	/**
	 * 取score 
	 * @param j 数据源连接
	 * @param key 键
	 * @param member 成员
	 * @return
	 */
	public Double getScore(Jedis j, String key, byte[] member) {
		return j.zscore(key.getBytes(), member);
	}

	/**
	 * 模糊匹配key 
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public Set<String> keys(Jedis j, String key) {
		return j.keys(key);
	}

}
