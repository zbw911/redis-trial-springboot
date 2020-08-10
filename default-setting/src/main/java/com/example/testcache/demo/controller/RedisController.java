package com.example.testcache.demo.controller;

import com.example.testcachelib.demo.config.redis.RedisClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

/**
 * @author zhangbaowei
 * Create  on 2020/7/5 13:54.
 */
@RestController
@RequestMapping("/redis")
public class RedisController {
    private final RedisClient redisClient;

    public RedisController(RedisClient redisClient1) {
        this.redisClient = redisClient1;
    }

    @RequestMapping("/a")
    public String hehe() {
        long l = System.currentTimeMillis();
        redisClient.set(String.valueOf(l), l);

        Map<String, String> all = redisClient.getAll(Arrays.asList("1", "2", "3"));
        return redisClient.get(String.valueOf(l));
    }
}
