package com.xzkj.apiService.redis.client;


import com.xzkj.apiService.redis.common.JedisDataSourcePool;
import com.xzkj.apiService.redis.common.SerializeUtil;
import com.xzkj.apiService.redis.entity.RedisFifoList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

/**
 * redis client
 * @author admin
 */
public class RedisFifoClient {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisFifoClient.class);

    /**
     * jedisPool
     */
    private JedisPool jedisPool;
    private static RedisFifoList redisFifoList;
    /**
     * private construtor
     */
    public RedisFifoClient(String redisKey) {
    	try {
			// redis数据源
			JedisDataSourcePool jedisDataSourcePool = new JedisDataSourcePool();
			jedisDataSourcePool.init_dependence();
			jedisPool = jedisDataSourcePool.getJedisPool(redisKey);
			redisFifoList = new RedisFifoList();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * single
     */
    private static class Inner {
        private static RedisFifoClient instance = new RedisFifoClient("fifo_cache");
        private static RedisFifoClient instance_ai = new RedisFifoClient("fifo_cache_ai");
    }
    
    /**
     * getRedisClient
     *
     * @return instance
     */
    public static RedisFifoClient getRedisClient() {
        return Inner.instance;
    }
    /**
     * getRedisClient
     *
     * @return instance
     */
    public static RedisFifoClient getRedisClientAi() {
    	return Inner.instance_ai;
    }
    
    /**
     * 添加到队头
     * @param key   key
     * @param value value
     */
    public static void lpush(String key, String value) {
        Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
        try {
        	key = getKey(key);
            redisFifoList.lpush(jedis, key, value.getBytes());
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
    }
    
    /**
     * 添加到队尾
     * @param key   key
     * @param value value
     */
    public static void push(String key, String value) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		redisFifoList.push(jedis, key, value.getBytes());
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    
    /**
     * 批量添加到队尾
     * @param key   key
     * @param msgList value
     */
    public static void push(String key, List<String> msgList) {
    	byte[][] values = new byte[msgList.size()][];
		for (int i = 0; i < msgList.size(); i++) {
			values[i] = msgList.get(i).getBytes();
		}
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		redisFifoList.push(jedis, key, values);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    
    /**
     * 添加到队头
     * @param key   key
     * @param value value
     */
    public static void lpushObject(String key, Object value) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		//redisFifoList.lock(jedis, key);
    		redisFifoList.lpush(jedis, key, SerializeUtil.serialize(value));
    		//redisFifoList.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    /**
     * 添加到队头
     * @param key   key
     * @param value value
     */
    public static void lpushByteArr(String key, byte[] value) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		//redisFifoList.lock(jedis, key);
    		redisFifoList.lpush(jedis, key, value);
    		//redisFifoList.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    
    /**
     * 添加到队尾object
     * @param key   key
     * @param value value
     */
    public static void pushObject(String key, Object value) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		redisFifoList.push(jedis, key, SerializeUtil.serialize(value));
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }

    /**
     * 取一条
     * @param key KEY
     * @return string
     */
    public static String lindex(String key, int index) {
        Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
        byte[] byre = null;
        try {
        	key = getKey(key);
        	redisFifoList.lock(jedis, key);
        	byre = redisFifoList.index(jedis,key, index);
    		redisFifoList.unlock(jedis, key);
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
        return byre==null?null:new String(byre);
    }
    
    /**
     * 从队首取数据
     * @param key KEY
     * @return string
     */
    public static String pop(String key) {
        Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
        byte[] byre = null;
        try {
        	key = getKey(key);
        	byre = redisFifoList.pop(jedis,key);
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
        return byre==null?null:new String(byre);
    }
    
    /**
     * 从队首取数据-ai
     * @param key KEY
     * @return string
     */
    public static String pop_ai(String key) {
    	Jedis jedis = RedisFifoClient.getRedisClientAi().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		byre = redisFifoList.pop(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre==null?null:new String(byre);
    }
    
    /**
     * 取一条
     * @param key KEY
     * @return 
     * @return string
     */
    public static Object lindexObject(String key, int index) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		redisFifoList.lock(jedis, key);
    		byre = redisFifoList.index(jedis,key, index);
    		redisFifoList.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre==null?null:SerializeUtil.unserialize(byre);
    }
    
    /**
     * 从队首取数据
     * @param key KEY
     * @return string
     */
    public static Object popObject(String key) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		byre = redisFifoList.pop(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre==null?null:SerializeUtil.unserialize(byre);
    }
    
    /**
	 * 取一段
	 * @param key 键
	 * @param start 起始下标
	 * @param end 结束下标
	 * @return
	 */
    public static List<Object> rangeObject(String key, long start, long end) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	List<byte[]> byreList = null;
    	try {
    		key = getKey(key);
    		redisFifoList.lock(jedis, key);
    		byreList = redisFifoList.range(jedis,key,start,end);
    		redisFifoList.unlock(jedis, key);
    		
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	if(byreList==null||byreList.size()==0){
			return null;
		}
		List<Object> objList = new ArrayList<Object>();
		for (byte[] bs : byreList) {
			objList.add(SerializeUtil.unserialize(bs));
		}
		return objList;
    }
    
    /**
     * 取一段
     * @param key 键
     * @param start 起始下标
     * @param end 结束下标
     * @return
     */
    public static List<Object> range(String key, long start, long end) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	List<byte[]> byreList = null;
    	try {
    		key = getKey(key);
    		byreList = redisFifoList.range(jedis,key,start,end);
    		
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	if(byreList==null||byreList.size()==0){
    		return null;
    	}
    	List<Object> objList = new ArrayList<Object>();
    	for (byte[] bs : byreList) {
    		objList.add(new String(bs));
    	}
    	return objList;
    }
    
    /**
	 * 队列大小
	 * @param key 键
	 * @return
	 */
    public static long len(String key) {
		Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
        try {
        	key = getKey(key);
            long b = redisFifoList.len(jedis,key);
            return b;
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
        return -1;
	}

    /**
     * 移除数据
     * @param key 键
     * @return
     */
    public static Long remove(String key) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	long byre = -1;
    	try {
    		key = getKey(key);
    		byre = redisFifoList.remove(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    /**
	 * 是否存在
	 * @param key 键
	 * @return
	 */
    public static boolean exist(String key) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	boolean byre = false;
    	try {
    		key = getKey(key);
    		byre = redisFifoList.exist(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
     * 环形取值-从队头
     * @param key   key
	 */
    public static String circlePop(String key) {
    	Jedis jedis = RedisFifoClient.getRedisClient().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		redisFifoList.lock(jedis, key);
    		byre = redisFifoList.pop(jedis,key);
    		if(byre!=null)
    			redisFifoList.push(jedis, key, byre);
    		redisFifoList.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre==null?null:new String(byre);
    }
    
    /**
     * build a key
     * @param key key
     * @return key
     */
    private static String getKey(String key) {
        //return "fifo_" + key;
        return key;
    }

    /*private static void returnResource(Jedis jedis,boolean conectionBroken) {
        if (null != jedis) {  
            if(conectionBroken){  
                jedisPool.returnBrokenResource(jedis);  
            }else{  
                jedisPool.returnResource(jedis);  
            }   
        }
    }
    
    //Handle jedisException, write log and return whether the connection is broken.
    protected static boolean handleJedisException(Exception jedisException) {
        if (jedisException instanceof JedisConnectionException) {
        	LOGGER.error("Redis connection " + "RedisConfig.REDISHOST" + " lost.", jedisException);
        } else if (jedisException instanceof JedisDataException) {
            if ((jedisException.getMessage() != null) && (jedisException.getMessage().indexOf("READONLY") != -1)) {
            	LOGGER.error("Redis connection " + "RedisConfig.REDISHOST" + " are read-only slave.", jedisException);
            } else {
                // dataException, isBroken=false
                return false;
            }
        } else {
        	LOGGER.error("Jedis exception happen.", jedisException);
        }
        return true;
    }*/
}
