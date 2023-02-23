package com.xzkj.apiService.redis.client;


import com.xzkj.apiService.redis.common.JedisDataSourcePool;
import com.xzkj.apiService.redis.common.SerializeUtil;
import com.xzkj.apiService.redis.entity.RedisString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * redis client
 * @author admin
 */
public class RedisStringClient {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisStringClient.class);

    /**
     * jedisPool
     */
    private static JedisPool jedisPool;
    private static RedisString redisString;
    /**
     * private construtor
     */
    public RedisStringClient(String redisKey) {
    	try {
			// redis数据源
			JedisDataSourcePool jedisDataSourcePool = new JedisDataSourcePool();
			jedisDataSourcePool.init_dependence();
			jedisPool = jedisDataSourcePool.getJedisPool(redisKey);
			redisString = new RedisString();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private Jedis getJedis() {
    	//System.out.println("-------------"+jedisPool.getNumActive());
        return jedisPool.getResource();
    }

    /**
     * single
     */
    private static class Inner {
        private static RedisStringClient instance = new RedisStringClient("common_cache");
    }

    /**
     * getRedisClient
     * @return instance
     */
    public static RedisStringClient getRedisClient() {
        return Inner.instance;
    }
    
    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void set(String key, String value) {
        Jedis jedis = RedisStringClient.getRedisClient().getJedis();
        try {
        	key = getKey(key);
        	redisString.set(jedis, key, value.getBytes());
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
    }
    
    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void set(String key, String value,String lock) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		redisString.lock(jedis, key);
    		redisString.set(jedis, key, value.getBytes());
    		redisString.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    
    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void set(String key, Object value) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		redisString.set(jedis, key, SerializeUtil.serialize(value));
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    
    /**
     * 添加数据
     * @param key   key
     * @param value value
     */
    public static void set_lock(String key, Object value) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		redisString.lock(jedis, key);
    		redisString.set(jedis, key, SerializeUtil.serialize(value));
    		redisString.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    
    /**
     * 添加数据
     * @param key   key
     * @param seconds   超时时间
     * @param value value
     */
    public static void set(String key,int seconds, String value) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		redisString.set(jedis, key,seconds, value.getBytes());
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    
    /**
     * 添加数据
     * @param key   key
     * @param seconds   超时时间
     * @param value value
     */
    public static void set(String key,int seconds, Object value) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		redisString.set(jedis, key,seconds, SerializeUtil.serialize(value));
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    
    /**
	 * 获取数据
	 * @param key 键
	 * @return
	 */
    public static String get(String key) {
        Jedis jedis = RedisStringClient.getRedisClient().getJedis();
        byte[] byre = null;
        try {
        	key = getKey(key);
        	byre = redisString.get(jedis,key);
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
        return byre==null?null:new String(byre);
    }
    
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static String get(String key,String lock) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		redisString.lock(jedis, key);
    		byre = redisString.get(jedis,key);
    		redisString.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre==null?null:new String(byre);
    }
    
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static Object getObject(String key) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		byre = redisString.get(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre==null?null:SerializeUtil.unserialize(byre);
    }
    
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static Object getObject_lock(String key) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		redisString.lock(jedis, key);
    		byre = redisString.get(jedis,key);
    		redisString.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre==null?null:SerializeUtil.unserialize(byre);
    }
    
    /**
	 * 批量添加数据
	 * @param map 数据
	 * @param seconds 超时时间
	 * @return
	 */
    public static void setStrings(Map<String, String> map, int seconds) {
        Jedis jedis = RedisStringClient.getRedisClient().getJedis();
        try {
        	Map<String, byte[]> mapObj = new HashMap<String, byte[]>();
        	for (Map.Entry<String,String> en : map.entrySet()) {
                mapObj.put(getKey(en.getKey()), en.getValue().getBytes());
            }
        	redisString.setObjects(jedis, mapObj, seconds);
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
    }
    
    /**
     * 批量添加数据
     * @param map 数据
     * @param seconds 超时时间
     * @return
     */
    public static void setObjects(Map<String, Object> map, int seconds) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	try {
    		Map<String, byte[]> mapObj = new HashMap<String, byte[]>();
    		for (Map.Entry<String,Object> en : map.entrySet()) {
    			mapObj.put(getKey(en.getKey()), SerializeUtil.serialize(en.getValue()));
    		}
    		redisString.setObjects(jedis, mapObj, seconds);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    
    /**
	 * 批量获取数据
	 * @param keys 键值列表
	 * @return
	 */
    public static Map<String, String> getStrings(List<String> keys) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	Map<String, String> mapResult = new HashMap<String, String>();
    	try {
    		List<String> keysObj = new ArrayList<String>();
    		for(String key:keys){
    			keysObj.add(getKey(key));
    		}
    		Map<String, byte[]> map = redisString.getObjects(jedis, keysObj);
    		for (Map.Entry<String,byte[]> en : map.entrySet()) {
    			for(String key:keys){
        			if(getKey(key).equals(en.getKey())){
        				mapResult.put(key, new String(en.getValue()));
        				break;
        			}
        		}
    		}
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return mapResult;
    }
    
    /**
     * 批量获取数据
     * @param keys 键值列表
     * @return
     */
    public static Map<String, Object> getObjects(List<String> keys) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	Map<String, Object> mapResult = new HashMap<String, Object>();
    	try {
    		List<String> keysObj = new ArrayList<String>();
    		for(String key:keys){
    			keysObj.add(getKey(key));
    		}
    		Map<String, byte[]> map = redisString.getObjects(jedis, keysObj);
    		for (Map.Entry<String,byte[]> en : map.entrySet()) {
    			for(String key:keys){
    				if(getKey(key).equals(en.getKey())){
    					mapResult.put(key, SerializeUtil.unserialize(en.getValue()));
    					break;
    				}
    			}
    		}
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return mapResult;
    }
    
    /**
	 * 移除数据
	 * @param key 键
	 * @return
	 */
    public static Long remove(String key,String lock) {
        Jedis jedis = RedisStringClient.getRedisClient().getJedis();
        long byre = -1;
        try {
        	key = getKey(key);
        	redisString.lock(jedis, key);
        	byre = redisString.remove(jedis,key);
    		redisString.unlock(jedis, key);
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
        return byre;
    }
    
    /**
     * 移除数据
     * @param key 键
     * @return
     */
    public static Long remove(String key) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	long byre = -1;
    	try {
    		key = getKey(key);
    		byre = redisString.remove(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
     * 批量移除数据
     * @param keys 键
     * @return
     */
    public static Long removeKeys(List<String> keys) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	long byre = -1;
    	try {
    		for(String key:keys){
    			key = getKey(key);
    		}
    		byre = redisString.removeKeys(jedis,keys);
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
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	boolean byre = false;
    	try {
    		key = getKey(key);
    		byre = redisString.exist(jedis,key);
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
    public static boolean exist(String key,String lock) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	boolean byre = false;
    	try {
    		key = getKey(key);
    		redisString.lock(jedis, key);
    		byre = redisString.exist(jedis,key);
    		redisString.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
	 * 获取长度
	 * @param key 键
	 * @return
	 */
    public static Long strlen(String key) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	Long byre = null;
    	try {
    		key = getKey(key);
    		byre = redisString.strlen(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
     * 整数递增
     * @param key 键
     * @return
     */
    public static Long incr(String key) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	Long byre = null;
    	try {
    		key = getKey(key);
    		byre = redisString.incr(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
     * 整数递增
     * @param key 键
     * @param integer 指定的整数
     * @return
     */
    public static Long incr(String key,long integer) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	Long byre = null;
    	try {
    		key = getKey(key);
    		byre = redisString.incr(jedis,key,integer);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
     * 整数递减
     * @param key 键
     * @return
     */
    public static Long decr(String key) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	Long byre = null;
    	try {
    		key = getKey(key);
    		byre = redisString.decr(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
     * 整数递减
     * @param key 键
     * @param integer 指定的整数
     * @return
     */
    public static Long decr(String key,long integer) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	Long byre = null;
    	try {
    		key = getKey(key);
    		byre = redisString.decr(jedis,key,integer);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
     * 增加指定的浮点数
     * @param key 键
     * @param integer 指定的浮点数
     * @return
     */
    public static Double incrByFloat(String key,double integer) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	Double byre = null;
    	try {
    		key = getKey(key);
    		byre = redisString.incrByFloat(jedis,key,integer);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
	 * 向尾部追加字符串值
	 * @param key 键
	 * @param value 追加的值
	 * @return
	 */
    public static Long incrByFloat(String key,String value) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	Long byre = null;
    	try {
    		key = getKey(key);
    		byre = redisString.incrByFloat(jedis,key,value.getBytes());
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
	
    /**
     * build a key
     * @param key key
     * @return key
     */
    private static String getKey(String key) {
        //return "string_" + key;
        return key;
    }

    /*public static void returnResource(Jedis jedis,boolean conectionBroken) {
    	if (null != jedis) {  
            if(conectionBroken){  
                jedisPool.returnBrokenResource(jedis);  
            }else{  
                jedisPool.returnResource(jedis);  
            }   
        }
    }
    
    //Handle jedisException, write log and return whether the connection is broken.
    public static boolean handleJedisException(Exception jedisException) {
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
    
    /****************************************************************************************/
    /**
     * 获取redis
     * @return
     */
    public static Jedis getJedisPub() {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	return jedis;
    }
    /**
     * 获取key
     * @param key 键
     * @return
     */
    public static String getKeyPub(String key) {
   		key = getKey(key);
    	return key;
    }
    /**
     * 获取锁
     * @param key 键
     * @return
     */
    public static boolean lockPub(Jedis jedis,String key) {
    	boolean flag = redisString.lock(jedis, key);
    	return flag;
    }
    public static boolean lockPub(String key) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	boolean flag = false;
    	try {
    		key = getKey(key);
    		flag = redisString.lock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return flag;
    }
    /**
     * 释放锁
     * @param key 键
     * @return
     */
    public static boolean unlockPub(Jedis jedis,String key) {
    	boolean flag = redisString.unlock(jedis, key);
    	return flag;
    }
    public static boolean unlockPub(String key) {
    	Jedis jedis = RedisStringClient.getRedisClient().getJedis();
    	boolean flag = false;
    	try {
    		key = getKey(key);
    		flag = redisString.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return flag;
    }
    /**
     * 设置数据
     * @param key 键
     * @return
     */
    public static void setPub(Jedis jedis,String key,String value) {
    	redisString.set(jedis, key, value.getBytes());
    }
    /**
     * 设置数据
     * @param key 键
     * @return
     */
    public static void setPub(Jedis jedis,String key,Object value) {
    	redisString.set(jedis, key, SerializeUtil.serialize(value));
    }
    /**
     * 设置数据
     * @param key 键
     * @return
     */
    public static void setPub(Jedis jedis,String key,int seconds,Object value) {
    	redisString.set(jedis, key,seconds,SerializeUtil.serialize(value));
    }
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static String getPub(Jedis jedis,String key) {
    	byte[] byre = redisString.get(jedis,key);
    	return byre==null?null:new String(byre);
    }
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static Object getObjPub(Jedis jedis,String key) {
    	byte[] byre = redisString.get(jedis,key);
    	return byre==null?null:SerializeUtil.unserialize(byre);
    }
    /**
     * 是否存在
     * @param key 键
     * @return
     */
    public static boolean existPub(Jedis jedis,String key) {
    	return redisString.exist(jedis,key);
    }
    
    /**
     * 移除数据
     * @param key 键
     * @return
     */
    public static Long removePub(Jedis jedis,String key) {
    	return redisString.remove(jedis,key);
    }
}
