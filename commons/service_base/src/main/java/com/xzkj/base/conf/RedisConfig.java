package com.xzkj.base.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    /*
        redisTemplate配置自定义序列化器，注入IOC
        1，string key使用StringRedisSerializer
        2，value 对象 默认使用jdk的序列化器会乱码，使用jackson2 可以解决
     */
    @Bean
    public RedisTemplate<String, Object> getRedisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> redisTemplate=new RedisTemplate<>();
        //创建string特定序列化器械
        RedisSerializer<String> stringRedisSerializer=new StringRedisSerializer();
        //创建jackson序列化器
        /*
            构造器源码
            public Jackson2JsonRedisSerializer(Class<T> type) {
                this.javaType = this.getJavaType(type);
            }
         */
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer=new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om=new ObjectMapper();
        //全覆盖检测
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate.setConnectionFactory(factory);
        //1.字符形key 使用string序列化
        redisTemplate.setKeySerializer(stringRedisSerializer);
        //2.value 对象，使用jackson2
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //3.value hashmap 序列化
        redisTemplate.setHashKeySerializer(jackson2JsonRedisSerializer);

        return redisTemplate;
    }

    //缓存配置
    @Bean
    public CacheManager getCacheManager(RedisConnectionFactory factory){
        //创建string特定序列化器械
        RedisSerializer<String> stringRedisSerializer=new StringRedisSerializer();
        //创建jackson序列化器
        /*
            构造器源码
            public Jackson2JsonRedisSerializer(Class<T> type) {
                this.javaType = this.getJavaType(type);
            }
         */
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer=new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om=new ObjectMapper();
        //全覆盖检测
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        //缓存配置
        RedisCacheConfiguration cacheConfiguration= RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600))//缓存过期时间
                //key 序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                //value序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                //不缓存null值
                .disableCachingNullValues();
        //缓存管理器
        RedisCacheManager cacheManager=RedisCacheManager.builder(factory)
                .cacheDefaults(cacheConfiguration)
                .build();
        return cacheManager;

    }
}
