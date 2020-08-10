package com.example.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * @author zhangbaowei
 * Create  on 2020/7/7 18:45.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EnvUtilsTest {
    @Autowired
    Environment environment;

    @Test
    public void name() {
        Environment environment = this.environment;
        boolean b = EnvUtils.hasProperty(environment, "spring.redis");
        System.out.println(b);

        b = EnvUtils.hasProperty(environment, "spring.redis-hehe");
        System.out.println(b);

        b = EnvUtils.hasProperty(environment, "spring.redis-houhou");
        System.out.println(b);
    }

    @Test
    public void redis_ext() {
        Environment environment = this.environment;
        boolean b = EnvUtils.hasProperty(environment, "spring.redis-ext");
        Assert.assertTrue(b);
    }

    @Test
    public void redis_ext_getlist() {
        Environment environment = this.environment;
        HashMap bindResult1 = EnvUtils.getBindResult(environment, "spring.redis-ext", (new HashMap<>()).getClass());
    }

    @Test
    public void allConfigTest() {
        if (EnvUtils.hasProperty(environment, "spring.redis")) {
            RedisProperties bindResult = EnvUtils.getBindResult(environment, "spring.redis", RedisProperties.class);
            System.out.println(bindResult);

            if (EnvUtils.hasProperty(environment, "spring.redis.jedis")) {
                System.out.println("spring.redis.jedis");
            }
            if (EnvUtils.hasProperty(environment, "spring.redis.lettuce")) {
                System.out.println("spring.redis.lettuce");
            }
        }
    }
}