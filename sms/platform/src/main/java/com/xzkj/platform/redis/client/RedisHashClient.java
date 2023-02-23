package com.xzkj.platform.redis.client;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.xzkj.platform.redis.common.JedisDataSourcePool;
import com.xzkj.platform.redis.common.SerializeUtil;
import com.xzkj.platform.redis.entity.RedisHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * redis client
 * @author admin
 */
public class RedisHashClient {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisHashClient.class);

    /**
     * jedisPool
     */
    private JedisPool jedisPool;
    private static RedisHash redisHash;
    /**
     * private construtor
     */
    public RedisHashClient(String redisKey) {
    	try {
			// redis数据源
			JedisDataSourcePool jedisDataSourcePool = new JedisDataSourcePool();
			jedisDataSourcePool.init_dependence();
			jedisPool = jedisDataSourcePool.getJedisPool(redisKey);
			redisHash = new RedisHash();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private Jedis getJedis() {
        return jedisPool.getResource();
    }

    private static class Inner {
        private static RedisHashClient instance = new RedisHashClient("hash_cache");
        private static RedisHashClient instance_ai = new RedisHashClient("hash_cache_ai");
    }
    /**
     * single
     */
   /* private static class Inner {
        private static RedisHashClient instance = new RedisHashClient("authentication_code");
    }*/
    
    /**
     * getRedisClient
     * @return instance
     */
    public static RedisHashClient getRedisClient() {
        return Inner.instance;
    }
    /**
     * getRedisClientAi
     * @return instance
     */
    public static RedisHashClient getRedisClientAi() {
    	return Inner.instance_ai;
    }
    
    /**
	 * 单条set
	 * @param key 键
	 * @param field 域
	 * @param value 值
	 * @return
	 */
	public static void set(String key, String field, String value) {
        Jedis jedis = RedisHashClient.getRedisClient().getJedis();
        try {
        	key = getKey(key);
        	redisHash.set(jedis, key, field.getBytes(),value.getBytes());
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
    }
    
	
	/**
	 * 单条set
	 * @param key 键
	 * @param field 域
	 * @param value 值
	 * @return
	 */
	public static void set(String key, String field, Object value) {
		Jedis jedis = RedisHashClient.getRedisClient().getJedis();
		try {
			key = getKey(key);
			redisHash.set(jedis, key, field.getBytes(), SerializeUtil.serialize(value));
		} catch (Exception e) {
			LOGGER.error("RedisClient error", e);
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 单条set
	 * @param key 键
	 * @param field 域
	 * @param value 值
	 * @return
	 */
	public static void set(String key, Object field, Object value) {
		Jedis jedis = RedisHashClient.getRedisClient().getJedis();
		try {
			key = getKey(key);
			redisHash.set(jedis, key, SerializeUtil.serialize(field),SerializeUtil.serialize(value));
		} catch (Exception e) {
			LOGGER.error("RedisClient error", e);
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 单条set，当不存在时才插入
	 * @param key 键
	 * @param field 域
	 * @param value 值
	 * @return
	 */
	public static void setnx(String key, String field, String value) {
		Jedis jedis = RedisHashClient.getRedisClient().getJedis();
		try {
			key = getKey(key);
			redisHash.setnx(jedis, key, field.getBytes(),value.getBytes());
		} catch (Exception e) {
			LOGGER.error("RedisClient error", e);
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 单条set，当不存在时才插入
	 * @param key 键
	 * @param field 域
	 * @param value 值
	 * @return
	 */
	public static void setnx_lock(String key, String field, String value) {
		Jedis jedis = RedisHashClient.getRedisClient().getJedis();
		try {
			key = getKey(key);
			redisHash.lock(jedis, key);
			redisHash.setnx(jedis, key, field.getBytes(),value.getBytes());
			redisHash.unlock(jedis, key);
		} catch (Exception e) {
			LOGGER.error("RedisClient error", e);
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 单条set，当不存在时才插入
	 * @param key 键
	 * @param field 域
	 * @param value 值
	 * @return
	 */
	public static void setnx(String key, String field, Object value) {
		Jedis jedis = RedisHashClient.getRedisClient().getJedis();
		try {
			key = getKey(key);
			redisHash.setnx(jedis, key, field.getBytes(),SerializeUtil.serialize(value));
		} catch (Exception e) {
			LOGGER.error("RedisClient error", e);
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 单条set，当不存在时才插入
	 * @param key 键
	 * @param field 域
	 * @param value 值
	 * @return
	 */
	public static void setnx(String key, Object field, Object value) {
		Jedis jedis = RedisHashClient.getRedisClient().getJedis();
		try {
			key = getKey(key);
			redisHash.setnx(jedis, key, SerializeUtil.serialize(field),SerializeUtil.serialize(value));
		} catch (Exception e) {
			LOGGER.error("RedisClient error", e);
		} finally {
			jedis.close();
		}
	}
    
	/**
	 * 批量set 
	 * @param key 键
	 * @param map 域值
	 * @return
	 */
    public static void setStrings(String key, Map<String, String> map) {
        Jedis jedis = RedisHashClient.getRedisClient().getJedis();
        try {
        	key = getKey(key);
        	Map<byte[], byte[]> mapObj = new HashMap<byte[], byte[]>();
        	for (Map.Entry<String,String> en : map.entrySet()) {
                mapObj.put(en.getKey().getBytes(), en.getValue().getBytes());
            }
        	redisHash.set(jedis,key, mapObj);
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
    }
    
    /**
     * 批量set 
     * @param key 键
     * @param map 域值
     * @return
     */
    public static void setObjects(String key, Map<String, Object> map) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	try {
    		key = getKey(key);
    		Map<byte[], byte[]> mapObj = new HashMap<byte[], byte[]>();
    		for (Map.Entry<String,Object> en : map.entrySet()) {
    			mapObj.put(en.getKey().getBytes(), SerializeUtil.serialize(en.getValue()));
    		}
    		redisHash.set(jedis,key, mapObj);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    }
    
    /**
	 * 单条删除 
	 * @param key 键
	 * @param field 域
	 * @return
	 */
    public static Long remove(String key,String field) {
        Jedis jedis = RedisHashClient.getRedisClient().getJedis();
        long byre = -1;
        try {
        	key = getKey(key);
        	byre = redisHash.del(jedis,key,field.getBytes());
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
        return byre;
    }
    
    /**
     * 删除全部 
     * @param key 键
     * @return
     */
    public static Long del(String key) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	long byre = -1;
    	try {
    		key = getKey(key);
    		byre = redisHash.del(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
	 * 单条删除 
	 * @param key 键
	 * @param field 域
	 * @return
	 */
    public static Long remove(String key,Object field) {
        Jedis jedis = RedisHashClient.getRedisClient().getJedis();
        long byre = -1;
        try {
        	key = getKey(key);
        	byre = redisHash.del(jedis,key,SerializeUtil.serialize(field));
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
        return byre;
    }
    
    /**
	 * 批量删除 
	 * @param key 键
	 * @param fields 域
	 * @return
	 */
    public static Long removeKeys(String key,List<String> fields) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	long byre = -1;
    	try {
    		key = getKey(key);
    		List<byte[]> fieldList = Lists.transform(fields, new Function<String, byte[]>() {
                public byte[] apply(String s) {
                    return s.getBytes();
                }
            });
    		byte[][] bytes = new byte[fieldList.size()][];
    		for(int i=0;i<fieldList.size();i++){
    			bytes[i]=fieldList.get(i);
    		}
    		byre = redisHash.del(jedis,key,bytes);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
	 * 单条查询 
	 * @param key 键
	 * @param field 域
	 * @return
	 */
    public static String get(String key,String field) {
        Jedis jedis = RedisHashClient.getRedisClient().getJedis();
        byte[] byre = null;
        try {
        	key = getKey(key);
        	byre = redisHash.get(jedis,key,field.getBytes());
        } catch (Exception e) {
            LOGGER.error("RedisClient error", e);
        } finally {
        	jedis.close();
        }
        return byre==null?null:new String(byre);
    }
    
    /**
     * 单条查询 
     * @param key 键
     * @param field 域
     * @return
     */
    public static String get(String key,Object field) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		byre = redisHash.get(jedis,key,SerializeUtil.serialize(field));
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
    public static Object getObject(String key,String field) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		byre = redisHash.get(jedis,key,field.getBytes());
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
    public static byte[] getByteArr(String key,String field) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		byre = redisHash.get(jedis,key,field.getBytes());
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static Object getObject(String key,Object field) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	byte[] byre = null;
    	try {
    		key = getKey(key);
    		byre = redisHash.get(jedis,key,SerializeUtil.serialize(field));
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre==null?null:SerializeUtil.unserialize(byre);
    }
    
    /**
	 * 批量查询 
	 * @param key 键
	 * @param fields 域
	 * @return
	 */
    public static Map<String, String> getStrings(String key,List<String> fields) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	Map<String, String> mapResult = new HashMap<String, String>();
    	try {
    		key = getKey(key);
    		List<byte[]> fieldList = Lists.transform(fields, new Function<String, byte[]>() {
                public byte[] apply(String s) {
                    return s.getBytes();
                }
            });
    		byte[][] bytes = new byte[fieldList.size()][];
    		for(int i=0;i<fieldList.size();i++){
    			bytes[i]=fieldList.get(i);
    		}
    		Map<byte[], byte[]> map = redisHash.get(jedis,key, bytes);
    		for (Map.Entry<byte[],byte[]> en : map.entrySet()) {
        		mapResult.put(new String(en.getKey()), new String(en.getValue()));
    		}
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return mapResult;
    }
    
    /**
     * 批量查询 
     * @param key 键
     * @param fields 域
     * @return
     */
    public static Map<String, Object> getObjects(String key,List<String> fields) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	Map<String, Object> mapResult = new HashMap<String, Object>();
    	try {
    		key = getKey(key);
    		List<byte[]> fieldList = Lists.transform(fields, new Function<String, byte[]>() {
    			public byte[] apply(String s) {
    				return s.getBytes();
    			}
    		});
    		byte[][] bytes = new byte[fieldList.size()][];
    		for(int i=0;i<fieldList.size();i++){
    			bytes[i]=fieldList.get(i);
    		}
    		Map<byte[], byte[]> map = redisHash.get(jedis,key, bytes);
    		for (Map.Entry<byte[],byte[]> en : map.entrySet()) {
    			mapResult.put(new String(en.getKey()), SerializeUtil.unserialize(en.getValue()));
    		}
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return mapResult;
    }
    
    /**
	 * 所以key下field列表 
	 * @param key 键
	 * @return
	 */
    public static Map<String, String> getStrings(String key) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	Map<String, String> mapResult = new HashMap<String, String>();
    	try {
    		key = getKey(key);
    		//System.out.println("-------------------------"+key);
    		Map<byte[], byte[]> map = redisHash.getAll(jedis,key);
    		for (Map.Entry<byte[],byte[]> en : map.entrySet()) {
    			mapResult.put(new String(en.getKey()), new String(en.getValue()));
    		}
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return mapResult;
    }
    
    /**
     * 所以key下field列表 
     * @param key 键
     * @return
     */
    public static Map<String, String> getStrings_ai(String key) {
    	Jedis jedis = RedisHashClient.getRedisClientAi().getJedis();
    	Map<String, String> mapResult = new HashMap<String, String>();
    	try {
    		key = getKey(key);
    		Map<byte[], byte[]> map = redisHash.getAll(jedis,key);
    		for (Map.Entry<byte[],byte[]> en : map.entrySet()) {
    			mapResult.put(new String(en.getKey()), new String(en.getValue()));
    		}
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return mapResult;
    }
    
    /**
     * 所有key下列表 
     * @param key 键
     * @return
     */
    public static Map<String, Object> getObjectMap(String key) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	Map<String, Object> mapResult = new HashMap<String, Object>();
    	try {
    		key = getKey(key);
    		Map<byte[], byte[]> map = redisHash.getAll(jedis,key);
    		for (Map.Entry<byte[],byte[]> en : map.entrySet()) {
    			mapResult.put(new String(en.getKey()),SerializeUtil.unserialize(en.getValue()));
    		}
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return mapResult;
    }
    
    /**
	 * 所有key值列表 
	 * @param key 键
	 * @return
	 */
    public static Map<String, Object> getObjects(String key) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	Map<String, Object> mapResult = new HashMap<String, Object>();
    	try {
    		key = getKey(key);
    		Map<byte[], byte[]> map = redisHash.getAll(jedis,key);
    		for (Map.Entry<byte[],byte[]> en : map.entrySet()) {
    			mapResult.put(new String(en.getKey()), SerializeUtil.unserialize(en.getValue()));
    		}
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return mapResult;
    }
    
    /**
     * 判断字段是否存在：存在返回1 ，不存在返回0
     * @param key 键
     * @param field 域值
     * @return
     */
    public static boolean exist(String key,String field) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	boolean byre = false;
    	try {
    		key = getKey(key);
    		byre = redisHash.hexists(jedis,key,field.getBytes());
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
     * 判断字段是否存在：存在返回1 ，不存在返回0
     * @param key 键
     * @param field 域值
     * @return
     */
    public static boolean exist(String key,String field,String lock) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	boolean byre = false;
    	try {
    		key = getKey(key);
    		redisHash.lock(jedis, key);
    		byre = redisHash.hexists(jedis,key,field.getBytes());
    		redisHash.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    /**
     * 判断字段是否存在：存在返回1 ，不存在返回0
     * @param key 键
     * @param lock 域值
     * @return
     */
    public static boolean exists(String key,String lock) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	boolean byre = false;
    	try {
    		key = getKey(key);
    		redisHash.lock(jedis, key);
    		byre = redisHash.hexists(jedis,key);
    		redisHash.unlock(jedis, key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
     * 判断字段是否存在：存在返回1 ，不存在返回0
     * @param key 键
     * @param field 域值
     * @return
     */
    //public boolean len(Jedis j, String key,byte[] field) {
    public static boolean exist(String key,Object field) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	boolean byre = false;
    	try {
    		key = getKey(key);
    		byre = redisHash.hexists(jedis,key,SerializeUtil.serialize(field));
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
    public static Long len(String key) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	Long byre = null;
    	try {
    		key = getKey(key);
    		byre = redisHash.len(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
	 * 自增increment 
	 * @param key 键
	 * @param field 域
	 * @param integer 增加值
	 * @return
	 */
    public static Long incrBy(String key,String field,long integer) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	Long byre = null;
    	try {
    		key = getKey(key);
    		byre = redisHash.hincrBy(jedis,key,field.getBytes(),integer);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
    
    /**
   	 * 自增increment 
   	 * @param key 键
   	 * @param field 域
   	 * @param integer 增加值
   	 * @return
   	 */
       public static Long incrByAi(String key,String field,long integer) {
       	Jedis jedis = RedisHashClient.getRedisClientAi().getJedis();
       	Long byre = null;
       	try {
       		key = getKey(key);
       		byre = redisHash.hincrBy(jedis,key,field.getBytes(),integer);
       	} catch (Exception e) {
       		LOGGER.error("RedisClient error", e);
       	} finally {
       		jedis.close();
       	}
       	return byre;
       }
       
    /**
     * 自增increment 
     * @param key 键
     * @param field 域
     * @param integer 增加值
     * @return
     */
    public static Double incrBy1(String key,String field,Double integer) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	Double byre = null;
    	try {
    		key = getKey(key);
    		byre = redisHash.hincrDoubleBy(jedis,key,field.getBytes(),integer);
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
        //return "hash_" + key;
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
    /*****************************************************************************************************/
    /**
     * 获取redis
     * @return
     */
    public static Jedis getJedisPub() {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
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
    	boolean flag = redisHash.lock(jedis, key);
    	return flag;
    }
    /**
     * 释放锁
     * @param key 键
     * @return
     */
    public static boolean unlockPub(Jedis jedis,String key) {
    	boolean flag = redisHash.unlock(jedis, key);
    	return flag;
    }
    /**
     * 设置数据
     * @param key 键
     * @return
     */
    public static void setPub(Jedis jedis,String key,String field,String value) {
    	redisHash.set(jedis, key,field.getBytes(), value.getBytes());
    }
    /**
     * 获取数据
     * @param key 键
     * @return
     */
    public static String getPub(Jedis jedis,String key,String field) {
    	byte[] byre = redisHash.get(jedis,key,field.getBytes());
    	return byre==null?null:new String(byre);
    }
    /**
     * 是否存在
     * @param key 键
     * @return
     */
    public static boolean existPub(Jedis jedis,String key,String field) {
    	return redisHash.hexists(jedis,key,field.getBytes());
    }

    /**
	 * 设置过期时间
	 * @param key 键
	 * @param seconds 有效时长（秒）
	 * @return
	 */
    public static Long setExpire(String key,int seconds) {
    	Jedis jedis = RedisHashClient.getRedisClient().getJedis();
    	Long byre = null;
    	try {
    		key = getKey(key);
    		byre = redisHash.setExpire(jedis,key,seconds);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
    }
}
