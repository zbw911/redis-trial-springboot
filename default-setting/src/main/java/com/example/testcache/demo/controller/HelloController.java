package com.example.testcache.demo.controller;

import com.example.testcachelib.demo.config.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhangbaowei
 * Create  on 2019/11/20 10:19.
 */
@RestController
@RequestMapping("/h")
//@EnableConfigurationProperties(Configtest.class)
public class HelloController {

    @Autowired
    RedisClient redisClient;


    @Autowired    @Qualifier("first") RedisClient redisClient_frist;

    @Autowired
    @Qualifier("second")
    RedisClient redisClient_second;

    @RequestMapping("/hehe")
    public List<Map<String, String>> hehe() {
        long l = System.currentTimeMillis();

        redisClient.setAll(Arrays.asList("1", "2", "3"), Arrays.asList("1", "2", "3"));
        Map<String, String> all = redisClient.getAll(Arrays.asList("1", "2", "3"));

        redisClient_frist.setAll(Arrays.asList("1", "2", "3"), Arrays.asList("1", "2", "3"));
        Map<String, String> all_first = redisClient_frist.getAll(Arrays.asList("1", "2", "3"));

        redisClient_second.setAll(Arrays.asList("1", "2", "3"), Arrays.asList("1", "2", "3"));
        Map<String, String> all_second = redisClient_second.getAll(Arrays.asList("1", "2", "3"));

        return Arrays.asList(all, all_first, all_second);
    }
}
