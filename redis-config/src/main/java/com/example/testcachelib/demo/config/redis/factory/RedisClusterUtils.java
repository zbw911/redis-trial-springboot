//package com.example.testcachelib.demo.config.redis.factory;
//
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.data.redis.connection.RedisClusterConfiguration;
//import org.springframework.data.redis.connection.RedisNode;
//import org.springframework.data.redis.connection.RedisPassword;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author zhangbaowei
// * Create  on 2019/7/29 13:54.
// */
//class RedisClusterUtils {
//    static RedisClusterConfiguration redisClusterConfiguration(RedisProperties redisProperties) {
//        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
//        redisClusterConfiguration.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());
//
//        List<String> strnodes = redisProperties.getCluster().getNodes();
//
//        List<RedisNode> nodeList = new ArrayList<>();
//        for (String strnode : strnodes) {
//            String[] hp = strnode.split(":");
//            nodeList.add(new RedisNode(hp[0], Integer.parseInt(hp[1])));
//        }
//
//        redisClusterConfiguration.setClusterNodes(nodeList);
//        redisClusterConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
//
//        return redisClusterConfiguration;
//    }
//
//    static RedisStandaloneConfiguration redisStandaloneConfiguration(RedisProperties redisProperties) {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
//        redisStandaloneConfiguration.setPort(redisProperties.getPort());
//        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
//        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
//        return redisStandaloneConfiguration;
//    }
//}
