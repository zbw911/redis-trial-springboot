package com.example.testcache.demo.config;

import org.junit.Test;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;

/**
 * @author zhangbaowei
 * Create  on 2020/7/6 15:50.
 */
public class MultiRedisConfigTest {
    @Test
    public void t() {
        ConfigurationPropertyName of = ConfigurationPropertyName.of("spring.redis");
    }
}