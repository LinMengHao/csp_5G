//package com.xzkj.flowStore.config;
//
//
//import com.xzkj.flowStore.utils.RedisUtil;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowire;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisPassword;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import redis.clients.jedis.JedisPoolConfig;
//
///**
// * Redis实现类
// */
//@SuppressWarnings(value = "all")
//@Configuration
//@EnableCaching
//public class RedisUtilConfig {
//
//    @Value("${spring.redis.host}")
//    private String firstHost;
//
//    @Value("${spring.redis.port}")
//    private int firstPort;
//
//    @Value("${spring.redis.password}")
//    private String firstPassword;
//
//    @Value("${spring.redis.database}")
//    private int firstDatabase;
//
//
//    @Value("${spring.redis.timeout}")
//    private long timeout;
//
//    @Value("${spring.redis.jedis.pool.max-active}")
//    private int maxTotal;
//
//    @Value("${spring.redis.jedis.pool.max-idle}")
//    private int maxIdle;
//
//    @Value("${spring.redis.jedis.pool.min-idle}")
//    private int minIdle;
//
//    @Value("${spring.redis.jedis.pool.max-wait}")
//    private long maxWait;
//
//    public JedisPoolConfig jedisPoolConfigs() {
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        //最大连接数
//        jedisPoolConfig.setMaxTotal(maxTotal);
//        //最小空闲连接数
//        jedisPoolConfig.setMinIdle(minIdle);
//        jedisPoolConfig.setMaxIdle(maxIdle);
//        //当池内没有可用连接时，最大等待时间
//        jedisPoolConfig.setMaxWaitMillis(maxWait);
//        jedisPoolConfig.setMinEvictableIdleTimeMillis(timeout);
//
//        //其他属性可以自行添加
//        return jedisPoolConfig;
//    }
//
//    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig, String hostName, int port, RedisPassword redisPassword, int database) {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        //设置redis服务器的host或者ip地址
//        redisStandaloneConfiguration.setHostName(hostName);
//        redisStandaloneConfiguration.setPort(port);
//
//        if (redisPassword != null) {
//            redisStandaloneConfiguration.setPassword(redisPassword);
//        }
//        redisStandaloneConfiguration.setDatabase(database);
//
//
//        //获得默认的连接池构造
//        //这里需要注意的是，redisConnectionFactoryJ对于Standalone模式的没有（RedisStandaloneConfiguration，JedisPoolConfig）的构造函数，对此
//        //我们用JedisClientConfiguration接口的builder方法实例化一个构造器，还得类型转换
//        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jpcf = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
//        //修改我们的连接池配置
//        jpcf.poolConfig(jedisPoolConfig);
//        //通过构造器来构造jedis客户端配置
//        JedisClientConfiguration jedisClientConfiguration = jpcf.build();
//
//        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
//    }
//
//
//    @Bean(autowire = Autowire.BY_NAME, value = "baseRedisConnectionFactory")
//    public RedisConnectionFactory baseRedisConnectionFactory() {
//
//        RedisPassword redisPassword = null;
//
//        if (StringUtils.isNotEmpty(firstPassword)) {
//
//            redisPassword = RedisPassword.of(firstPassword);
//        }
//
//        return redisConnectionFactory(jedisPoolConfigs(), firstHost, firstPort, redisPassword, firstDatabase);
//    }
//
//
//    @Bean(autowire = Autowire.BY_NAME, value = "baseRedisTemplate")
//    public RedisTemplate baseRedisTemplate() {
//        RedisTemplate redisTemplate = new RedisTemplate();
//        redisTemplate.setConnectionFactory(baseRedisConnectionFactory());
//        //设置序列化Key的实例化对象
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        //设置序列化Value的实例化对象
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        return redisTemplate;
//    }
//
//
//    @Bean
//    public RedisUtil baseRedisUtil() {
//        RedisUtil redisUtil = new RedisUtil(baseRedisTemplate());
//        return redisUtil;
//    }
//}
//
