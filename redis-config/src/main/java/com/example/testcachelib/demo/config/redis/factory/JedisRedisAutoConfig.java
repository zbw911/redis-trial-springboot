//package com.example.testcache.demo.config.redis.factory;
//
//import com.example.testcache.demo.utils.ConditionalOnPropertyNotEmpty;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisClusterConfiguration;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//
///**
// * Create by  zhangbaowei on 2018/7/26 8:37.
// *
// * @author zhangbaowei
// */
//
//@Configuration
//@ConditionalOnPropertyNotEmpty("spring.redis.jedis")
//public class JedisRedisAutoConfig {
//
//    @Bean
//    public JedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
//
//        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
//        jedisClientConfiguration.connectTimeout(redisProperties.getTimeout());
//        jedisClientConfiguration.usePooling().poolConfig(genericObjectPoolConfig(redisProperties.getJedis().getPool()));
//
//        JedisConnectionFactory factory;
//        if (redisProperties.getCluster() != null && redisProperties.getCluster().getNodes() != null && redisProperties.getCluster().getNodes().size() > 0) {
//            RedisClusterConfiguration redisClusterConfiguration = RedisClusterUtils.redisClusterConfiguration(redisProperties);
//            factory = new JedisConnectionFactory(redisClusterConfiguration,
//                    jedisClientConfiguration.build());
//        } else {
//            RedisStandaloneConfiguration redisStandaloneConfiguration = RedisClusterUtils.redisStandaloneConfiguration(redisProperties);
//            factory = new JedisConnectionFactory(redisStandaloneConfiguration,
//                    jedisClientConfiguration.build());
//        }
//        factory.afterPropertiesSet();
//        return factory;
//    }
//
//    public GenericObjectPoolConfig genericObjectPoolConfig(RedisProperties.Pool pool) {
//        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
//        genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
//        genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
//        genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
//        if (pool.getMaxWait() != null) {
//            genericObjectPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
//        }
//        return genericObjectPoolConfig;
//    }
//}
