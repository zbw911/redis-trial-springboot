//package com.example.testcachelib.demo.config.redis;
//
//import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
//
///**
// * @author zhangbaowei
// * Create  on 2020/7/8 9:36.
// */
//@Configuration
//public class CustomConfig {
//    @Bean
//    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
//
//        return new LettuceClientConfigurationBuilderCustomizer() {
//            @Override
//            public void customize(LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigurationBuilder) {
//                clientConfigurationBuilder.clientName("test");
//            }
//        };
//    }
//}
