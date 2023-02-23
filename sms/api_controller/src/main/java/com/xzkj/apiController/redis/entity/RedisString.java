package com.xzkj.apiController.redis.entity;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字符串的封装
 * @author admin
 */
public class RedisString extends RedisLock {

	/**
	 * 添加数据
	 * @param j 数据源连接
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public boolean set(Jedis j, String key, byte[] value) {
		j.set(key.getBytes(), value);
		return true;
	}
	
	/**
	 * 添加数据
	 * @param j 数据源连接
	 * @param key 键
	 * @param seconds 超时时间
	 * @param value 值
	 * @return
	 */
	public boolean set(Jedis j, String key,int seconds, byte[] value) {
		j.setex(key.getBytes(),seconds, value);
		return true;
	}
	
	/**
	 * 获取数据
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public byte[] get(Jedis j, String key) {
		return j.get(key.getBytes());
	}
	
	/**
	 * 批量添加数据
	 * @param j 数据源连接
	 * @param map 数据
	 * @param seconds 超时时间
	 * @return
	 */
	public boolean setObjects(Jedis j, Map<String, byte[]> map, int seconds) {
		j.pipelined();
        for (Map.Entry<String, byte[]> en : map.entrySet()) {
            j.setex(en.getKey().getBytes(), seconds, en.getValue());
        }
        j.sync();
		return true;
	}
	
	/**
	 * 批量获取数据
	 * @param j 数据源连接
	 * @param keys 键值列表
	 * @return
	 */
	public Map<String, byte[]> getObjects(Jedis j, List<String> keys) {
		Map<String, byte[]> result = new HashMap<String, byte[]>();
		j.pipelined();
        for (String key : keys) {
            result.put(key, j.get(key.getBytes()));
        }
		j.sync();
		return result;
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
	 * 批量移除数据
	 * @param j 数据源连接
	 * @param keys 键
	 * @return
	 */
	public Long removeKeys(Jedis j, List<String> keys) {
		List<byte[]> keyList = Lists.transform(keys, new Function<String, byte[]>() {
            public byte[] apply(String s) {
                return s.getBytes();
            }
        });
        return j.del(keyList.toArray(new String[keyList.size()]));
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
	
	/**
	 * 获取长度-一个中文长度是3，redis是使用UTF-8编码中文的
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public Long strlen(Jedis j,String key) {
        return j.strlen(key.getBytes());
    }
	
	/**
	 * 整数递增： 默认值为0，所以首先执行命令得到 1 ，不是整型提示错误
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public Long incr(Jedis j,String key) {
		return j.incr(key.getBytes());
	}
	
	/**
	 * 增加指定的整数
	 * @param j 数据源连接
	 * @param key 键
	 * @param integer 指定的整数
	 * @return
	 */
	public Long incr(Jedis j,String key,long integer) {
		return j.incrBy(key.getBytes(), integer);
	}
	
	/**
	 * 整数递减： 默认值为0，所以首先执行命令得到 -1，不是整型提示错误
	 * @param j 数据源连接
	 * @param key 键
	 * @return
	 */
	public Long decr(Jedis j,String key) {
		return j.decr(key.getBytes());
	}
	
	/**
	 * 减少指定的整数
	 * @param j 数据源连接
	 * @param key 键
	 * @param integer 指定的整数
	 * @return
	 */
	public Long decr(Jedis j,String key,long integer) {
		return j.decrBy(key.getBytes(), integer);
	}
	
	/**
	 * 增加指定浮点数： 与INCR命令类似，只不过可以递增一个双精度浮点数
	 * @param j 数据源连接
	 * @param key 键
	 * @param integer 指定的双精度浮点数
	 * @return
	 */
	public Double incrByFloat(Jedis j,String key,double integer) {
		return j.incrByFloat(key.getBytes(), integer);
	}
	
	/**
	 * 向尾部追加字符串值： redis客户端并不是输出追加后的字符串，而是输出字符串总长度
	 * @param j 数据源连接
	 * @param key 键
	 * @param value 追加的值
	 * @return
	 */
	public Long incrByFloat(Jedis j,String key,byte[] value) {
		return j.append(key.getBytes(), value);
	}
	
}
