package com.example.testcachelib.demo.config.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhangbaowei
 * Create  on 2020/7/8 18:08.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PerformanceTest {
    @Autowired
    RedisClient redisClient;

    @Autowired
    @Qualifier("first")
    RedisClient redisClient_frist;

    @Autowired
    @Qualifier("second")
    RedisClient redisClient_second;

    @Test
    public void compair_0_1() {

        get_test(1000);
        set_test(1000);
        int cnt = 10000;
        getandset(redisClient, cnt);
        System.out.println("****************************");
        getandset(redisClient_frist, cnt);
    }

    public void getandset(RedisClient redisClient, int cnt) {
        long begin = System.currentTimeMillis();
        set_test(cnt);
        long end = System.currentTimeMillis();

        System.out.println(end - begin);

        begin = System.currentTimeMillis();
        get_test(cnt);
        end = System.currentTimeMillis();

        System.out.println(end - begin);
    }

    public void get_test(int count) {
        for (int i = 0; i < count; i++) {
            redisClient.get(String.valueOf(i));
        }
    }

    public void set_test(int count) {
        for (int i = 0; i < count; i++) {
            redisClient.set(String.valueOf(i), String.valueOf(i));
        }
    }
}
