//package com.example.testcache.demo.config;
//
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
///**
// * @author zhangbaowei
// * Create  on 2020/7/5 22:37.
// */
//@Configuration
//public class TestFactory {
//    @Bean
//    public RedisTemplate redisTemplateclient(RedisConnectionFactory redisConnectionFactory, RedisProperties redisProperties) {
//        RedisTemplate redisTemplate = new RedisTemplate();
//        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        redisTemplate.afterPropertiesSet();
//        RedisProperties redisProperties1 = redisProperties;
//        return redisTemplate;
//    }
//}
