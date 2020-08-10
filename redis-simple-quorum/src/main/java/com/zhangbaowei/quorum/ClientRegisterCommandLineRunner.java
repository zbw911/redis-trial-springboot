package com.zhangbaowei.quorum;

import com.example.testcachelib.demo.config.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangbaowei
 */
@Component
public class ClientRegisterCommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(ClientRegisterCommandLineRunner.class);
    private ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(1);
    @Autowired
    private RedisClient redisClient;

    @Value("${spring.application.name}")
    private String appname;

    public void run(String... args) {
        service.scheduleAtFixedRate(() -> {
            Clients clients = new Clients();
            try {
                clients.register(redisClient, appname);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("register client error", e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}