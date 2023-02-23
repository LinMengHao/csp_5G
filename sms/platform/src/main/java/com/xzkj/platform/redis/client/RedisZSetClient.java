package com.xzkj.platform.redis.client;


import com.xzkj.platform.redis.common.JedisDataSourcePool;
import com.xzkj.platform.redis.entity.RedisZset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * redis client
 * @author admin
 */
public class RedisZSetClient {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisZSetClient.class);

    /**
     * jedisPool
     */
    private static JedisPool jedisPool;
    private static RedisZset redisZSet;
    /**
     * private construtor
     */
    public RedisZSetClient(String redisKey) {
    	try {
			// redis数据源
			JedisDataSourcePool jedisDataSourcePool = new JedisDataSourcePool();
			jedisDataSourcePool.init_dependence();
			jedisPool = jedisDataSourcePool.getJedisPool(redisKey);
			redisZSet = new RedisZset();
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
        private static RedisZSetClient instance = new RedisZSetClient("zset_cache");
    }

    /**
     * getRedisClient
     * @return instance
     */
    public static RedisZSetClient getRedisClient() {
        return Inner.instance;
    }
    
    /**
   	 * 单条add 
   	 * @param key 键
   	 * @param score 排序
   	 * @param member 成员
   	 * @return
   	 */
       public static void set(String key,double score, String member) {
           Jedis jedis = RedisZSetClient.getRedisClient().getJedis();
           try {
           	key = getKey(key);
           	redisZSet.add(jedis, key, score,member.getBytes());
           } catch (Exception e) {
               LOGGER.error("RedisClient error", e);
           } finally {
           	jedis.close();
           }
       }
       /**
        * 单条add 
        * @param key 键
        * @param score 排序
        * @param member 成员
        * @return
        */
       public static void set(String key,double score,String member,String lock) {
       	Jedis jedis = RedisZSetClient.getRedisClient().getJedis();
       	try {
       		key = getKey(key);
       		redisZSet.lock(jedis, key);
       		redisZSet.add(jedis, key, score,member.getBytes());
       		redisZSet.unlock(jedis, key);
       	} catch (Exception e) {
       		LOGGER.error("RedisClient error", e);
       	} finally {
       		jedis.close();
       	}
       }
       
     /**
   	 * 单条删除 
   	 * @param key 键
     * @param member 成员
   	 * @return
   	 */
   	public static boolean remove(String key,String member,String lock) {
   		Jedis jedis = RedisZSetClient.getRedisClient().getJedis();
   		boolean byre = false;
       	try {
       		key = getKey(key);
       		redisZSet.lock(jedis, key);
       		byre = redisZSet.rem(jedis, key,member.getBytes());
       		redisZSet.unlock(jedis, key);
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
   	 * @param member 成员
   	 * @return
   	 */
   	public static boolean remove(String key,String member) {
   		Jedis jedis = RedisZSetClient.getRedisClient().getJedis();
   		boolean byre = false;
   		try {
   			key = getKey(key);
   			byre = redisZSet.rem(jedis, key,member.getBytes());
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
    	Jedis jedis = RedisZSetClient.getRedisClient().getJedis();
    	long byre = -1;
    	try {
    		key = getKey(key);
    		byre = redisZSet.del(jedis,key);
    	} catch (Exception e) {
    		LOGGER.error("RedisClient error", e);
    	} finally {
    		jedis.close();
    	}
    	return byre;
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
	public static int revrangeWithScore(Jedis j, String key, long start, long end, final List<Double> scores, final List<String> members) {
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
	 * 倒序取得特定范围内的排序元素,0代表第一个元素,1代表第二个以此类推。-1代表最后一个,-2代表倒数第二个.
	 * @param key 键
	 * @param start 元素开始位置
	 * @param end 元素结束位置
	 * @param scores score
	 * @param members 成员
	 * @return
	 */
	public static int revrangeWithScore(String key,long start, long end, final List<Double> scores, final List<String> members) {
		int result = 0;
		Jedis jedis = RedisZSetClient.getRedisClient().getJedis();
		try {
			key = getKey(key);
			result = redisZSet.revrangeWithScore(jedis, key, start, end,scores,members);
		} catch (Exception e) {
			LOGGER.error("RedisClient error", e);
		} finally {
			jedis.close();
		}
		return result;
	}
   	/**
   	 * 取得特定范围内的倒序排序元素,0代表第一个元素,1代表第二个以此类推。-1代表最后一个,-2代表倒数第二个.
   	 * @param key 键
   	 * @param start 元素开始位置
   	 * @param end 元素结束位置
   	 * @return
   	 */
   	public static List<String> zrevrange(String key,long start, long end) {
   		List<String> resultList = new ArrayList<String>();
   		Jedis jedis = RedisZSetClient.getRedisClient().getJedis();
   		try {
   			key = getKey(key);
   			Set<byte[]> setByte = redisZSet.zrevrange(jedis, key, start, end);
   			for (byte[] bs : setByte) {
   				resultList.add(new String(bs));
   			}
   		} catch (Exception e) {
   			LOGGER.error("RedisClient error", e);
   		} finally {
   			jedis.close();
   		}
   		return resultList;
   	}
   	/**
   	 * 取得特定范围内的排序元素,0代表第一个元素,1代表第二个以此类推。-1代表最后一个,-2代表倒数第二个.
   	 * @param key 键
   	 * @param start 元素开始位置
   	 * @param end 元素结束位置
   	 * @return
   	 */
   	public static List<String> range(String key,long start, long end) {
   		List<String> resultList = new ArrayList<String>();
   		Jedis jedis = RedisZSetClient.getRedisClient().getJedis();
   		try {
   			key = getKey(key);
   			Set<byte[]> setByte = redisZSet.range(jedis, key, start, end);
   			for (byte[] bs : setByte) {
   				resultList.add(new String(bs));
   			}
   		} catch (Exception e) {
   			LOGGER.error("RedisClient error", e);
   		} finally {
   			jedis.close();
   		}
   		return resultList;
   	}
   	
   	/**
   	 * 根据set的score顺序取 
   	 * @param key 键
   	 * @param min score最小值
   	 * @param max score最大值
   	 * @return
   	 */
   	public static List<String> rangeByScore(String key,double min, double max,String lock) {
   		List<String> resultList = new ArrayList<String>();
   		Jedis jedis = RedisZSetClient.getRedisClient().getJedis();
   		try {
   			key = getKey(key);
   			redisZSet.lock(jedis, key);
   			Set<byte[]> setByte = redisZSet.rangeByScore(jedis, key, min, max);
   			for (byte[] bs : setByte) {
   				resultList.add(new String(bs));
   			}
   			redisZSet.unlock(jedis, key);
   		} catch (Exception e) {
   			LOGGER.error("RedisClient error", e);
   		} finally {
   			jedis.close();
   		}
   		return resultList;
   	}
   	
   	/**
   	 * 根据set的score顺序取 
   	 * @param key 键
   	 * @param min score最小值
   	 * @param max score最大值
   	 * @return
   	 */
   	public static List<String> rangeByScore(String key,double min, double max) {
   		List<String> resultList = new ArrayList<String>();
   		Jedis jedis = RedisZSetClient.getRedisClient().getJedis();
   		try {
   			key = getKey(key);
   			Set<byte[]> setByte = redisZSet.rangeByScore(jedis, key, min, max);
   			for (byte[] bs : setByte) {
   				resultList.add(new String(bs));
   			}
   		} catch (Exception e) {
   			LOGGER.error("RedisClient error", e);
   		} finally {
   			jedis.close();
   		}
   		return resultList;
   	}
	
    /**
     * build a key
     * @param key key
     * @return key
     */
    private static String getKey(String key) {
        //return "zset_" + key;
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
