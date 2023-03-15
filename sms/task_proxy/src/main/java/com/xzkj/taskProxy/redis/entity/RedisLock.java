package com.xzkj.taskProxy.redis.entity;

import redis.clients.jedis.Jedis;

/**
 * 加锁的实现
 * @author admin
 */
public abstract class RedisLock {

	/**
	 * 锁表
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public synchronized boolean lock(Jedis j, String key) {
		while (true) {
			boolean isExis = j.exists(getLockKey(key).getBytes());
			if (isExis) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}
		j.setex(getLockKey(key).getBytes(), 30, key.getBytes());
		return true;
	}

	/**
	 * 释放锁定
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public boolean unlock(Jedis j, String key) {
		j.del(getLockKey(key).getBytes());
		return true;
	}

	private String getLockKey(String key) {
		return "_lock_" + key;
	}
}
