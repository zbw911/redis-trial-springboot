package com.example.testcachelib.demo.config.redis;

import com.example.testcachelib.demo.config.redis.config.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author zhangbaowei
 * Create  on 2020/7/6 14:58.
 */
@Configuration
@AutoConfigureAfter(value = {RedisAutoConfiguration.class})
public class RedisClientAutoConfig {

    @Bean
    public RedisClient redisClient(RedisConnectionFactory redisConnectionFactory) {
        RedisClientImpl redisClient = new RedisClientImpl(redisConnectionFactory);
        return redisClient;
    }
}
