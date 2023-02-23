package com.xzkj.apiController.redis.client;


import com.xzkj.apiController.redis.common.JedisDataSourcePool;
import com.xzkj.apiController.redis.entity.RdisSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis client
 * @author admin
 */
public class RedisSetClient {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSetClient.class);

    /**
     * jedisPool
     */
    private static JedisPool jedisPool;
    private static RdisSet redisSet;
    /**
     * private construtor
     */
    public RedisSetClient(String redisKey) {
    	try {
			// redis数据源
			JedisDataSourcePool jedisDataSourcePool = new JedisDataSourcePool();
			jedisDataSourcePool.init_dependence();
			jedisPool = jedisDataSourcePool.getJedisPool(redisKey);
			redisSet = new RdisSet();
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
        private static RedisSetClient instance = new RedisSetClient("set_cache");
    }

    /**
     * getRedisClient
     * @return instance
     */
    public static RedisSetClient getRedisClient() {
        return Inner.instance;
    }
    
    /**
	 * 添加到集合 
	 * @param key 键
	 * @param member 成员
	 * @return
	 */
    public static void set(String key, String member) {
        Jedis jedis = RedisSetClient.getRedisClient().getJedis();
        try {
        	key = getKey(key);
            //redisString.lock(jedis, key);
        	redisSet.add(jedis, key, member.getBytes());
            //redisString.unlock(jedis, key);
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
    }
    
    /**
     * build a key
     * @param key key
     * @return key
     */
    private static String getKey(String key) {
        //return "set_" + key;
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
