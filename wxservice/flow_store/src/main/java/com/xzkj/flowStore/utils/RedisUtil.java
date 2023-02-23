package com.xzkj.flowStore.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Component
public class RedisUtil {
    private final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private final int DEFAULT_TIMEOUT = 300;

    private final int LOCK_TIMEOUT = 3600;

    @Autowired
    private RedisTemplate redisTemplate;


    public RedisUtil() {


    }

    public RedisUtil(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public void remove(final String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean expire(final String key, Long timeoutInSecond) {
        return redisTemplate.expire(key, timeoutInSecond, TimeUnit.SECONDS);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final String key, Class<T> classT) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if (result != null) {
            if (classT.isInstance(result)) {
                return classT.cast(result);
            } else {
                try {
                    JSONObject jsonObject = JSON.parseObject(result.toString());
                    return jsonObject.toJavaObject(classT);
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            logger.error("Redis set value error(key:{},value:{}):", key, value, e);
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.error("Redis set value error(key:{},value:{}):", key, value, e);
        }
        return result;
    }

    /**
     * 获取某个key的过期时间（单位：秒）
     *
     * @param key
     * @return
     */
    public long getExpireTime(final String key) {
        try {
            Long time = redisTemplate.getExpire(key);
            return time;
        } catch (Exception e) {
        }
        return 0;
    }


    /**
     * 取到锁加锁 取不到锁一直等待直到获得锁
     */
    public boolean lock(final String lockKey) {
        //锁时间
        final Long lock_timeout = System.currentTimeMillis() + LOCK_TIMEOUT * 1000 + 1;

        Boolean result = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {

                byte[] value = redisTemplate.getStringSerializer().serialize(String.valueOf(lock_timeout));
                return connection.setNX(lockKey.getBytes(), value);
            }
        });
        //如果加锁成功
        if (result) {
            //设置超时时间，释放内存
            logger.info("lock the key :{}", lockKey);
            redisTemplate.expire(lockKey, LOCK_TIMEOUT, TimeUnit.SECONDS);
        }
        return result;
    }

    public void unlock(String lockKey) {
        logger.info("unlock the key :{}", lockKey);
        Object currt_lock_timeout_Str = redisTemplate.opsForValue().get(lockKey); // redis里的时间
        if (currt_lock_timeout_Str != null) {
            //如果是加锁者 则删除锁 如果不是则等待自动过期 重新竞争加锁
            redisTemplate.delete(lockKey); //删除键
            logger.info("unlock succeed!key:{}", lockKey);
        }
    }

    /**
     * 自增
     * 如果 step 大于 0，自增
     * 如果 step 小于 0 自减
     *
     * @param key 键
     * @return 返回值为自增后的值
     */
    public long increment(String key, long step) {
        return redisTemplate.opsForValue().increment(key, step);
    }

    public void setBit(String key, long pos, boolean isTrue) {
        redisTemplate.opsForValue().setBit(key, pos, isTrue);
    }

    public Boolean getBit(String key, long pos) {
        try {
            return redisTemplate.opsForValue().getBit(key, pos);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * set添加元素
     *
     * @param key
     * @param member
     * @return
     */
    public Long setAdd(String key, String member) {
        Long res = 0L;

        try {
            res = redisTemplate.opsForSet().add(key, member);
        } catch (Exception e) {
            logger.error("Redis set value error(key:{},member:{}):", key, member, e);
        }

        return res;
    }

    public Long setSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }


    /**
     * 查看set元素在不在集合中
     *
     * @param key
     * @param member
     * @return
     */
    public Boolean sismember(String key, String member) {
        return redisTemplate.opsForSet().isMember(key, member);
    }


    /**
     * 往hash中put键值对
     *
     * @param redisKey
     * @param key
     * @param value
     * @return
     */
    public Boolean hashAdd(String redisKey, String key, String value) {
        try {
            redisTemplate.opsForHash().put(redisKey, key, value);
        } catch (Exception e) {
            logger.error("Redis hhasAdd value error( redisKye:{},key:{},value:{})", redisKey, key, value);
            return false;
        }
        return true;
    }

    /**
     * 删除hash中的键
     *
     * @param redisKey
     * @param key
     * @return
     */
    public Boolean hashDel(String redisKey, String... key) {
        try {
            redisTemplate.opsForHash().delete(redisKey, key);
        } catch (Exception e) {
            logger.error("Redis hashDel value error( redisKye:{},key:{})", redisKey, key);
            return false;
        }
        return true;
    }


    /**
     * 获取 hash中的所有key和value
     *
     * @param redisKey
     * @return
     */
    public Map<Object, Object> haslGetAll(String redisKey) {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(redisKey);
            return entries;
        } catch (Exception e) {
            logger.error("Redis haslGetAll value error( redisKye:{})", redisKey);
        }
        return null;
    }

}
