//package com.example.testcachelib.demo.config.redis.factory;
//
//import io.lettuce.core.ClientOptions;
//import io.lettuce.core.SocketOptions;
//import io.lettuce.core.cluster.ClusterClientOptions;
//import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
//
//import java.time.Duration;
//
///**
// * Create by  zhangbaowei on 2018/7/26 8:37.
// *
// * @author zhangbaowei
// */
//
////@Configuration
////@ConditionalOnPropertyNotEmpty("spring.redis.lettuce")
//public class LettuceRedisAutoConfig {
//
//    @Value("${spring.redis.lettuce.validate-cluster-node-membership:true}")
//    private boolean validateClusterNodeMembership = true;
//    //默认超时时间,lettuce默认超时时间为60s太长了，此处默认设置为15s
//    private Long timeoutInMillis = Duration.ofSeconds(15).toMillis();
//
//    static ClusterClientOptions.Builder initDefaultClusterClientOptions(ClusterClientOptions.Builder builder) {
//        ClusterTopologyRefreshOptions defaultClusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
//                //开启集群拓扑结构周期性刷新，和默认参数保持一致
//                .enablePeriodicRefresh(Duration.ofSeconds(60))
//                //开启针对{@link RefreshTrigger}中所有类型的事件的触发器
//                .enableAllAdaptiveRefreshTriggers()
//                //和默认一样，30s超时，避免短时间大量出现刷新拓扑的事件
//                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(30))
//                //和默认一样重连5次先，然后在刷新集群拓扑
//                .refreshTriggersReconnectAttempts(5)
//                .build();
//
//        return builder
//                // 配置用于开启自适应刷新和定时刷新。如自适应刷新不开启，Redis集群变更时将会导致连接异常
//                .topologyRefreshOptions(defaultClusterTopologyRefreshOptions)
//                //默认就是重连的，显示定义一下
//                .autoReconnect(true)
//                //和默认一样最大重定向5次，避免极端情况无止境的重定向
//                .maxRedirects(5)
//                //Accept commands when auto-reconnect is enabled, reject commands when auto-reconnect is disabled.
//                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT)
//                .socketOptions(SocketOptions.builder().keepAlive(true).tcpNoDelay(true).build())
//                //取消校验集群节点的成员关系
//                .validateClusterNodeMembership(false);
//    }
//
//    public static ClusterClientOptions.Builder getDefaultClusterClientOptionBuilder() {
//        return initDefaultClusterClientOptions(ClusterClientOptions.builder());
//    }
//
//    //    @Bean
//    public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
//
//        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettucePoolingClientConfiguration.builder();
//
////        if (redisProperties.getLettuce().getPool() != null) {
////
////            lettuceClientConfigurationBuilder. (genericObjectPoolConfig(redisProperties.getLettuce().getPool()));
////        }
////                .commandTimeout(redisProperties.getTimeout())
//
//        if (!validateClusterNodeMembership) {
//            ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
//                    .validateClusterNodeMembership(false).build();
//
//            lettuceClientConfigurationBuilder.clientOptions(clusterClientOptions);
//        }
//
//        LettuceClientConfiguration clientConfig = lettuceClientConfigurationBuilder
//                .build();
//        LettuceConnectionFactory factory;
//        if (redisProperties.getCluster() != null && redisProperties.getCluster().getNodes() != null && redisProperties.getCluster().getNodes().size() > 0) {
//            factory = new LettuceConnectionFactory(RedisClusterUtils.redisClusterConfiguration(redisProperties), clientConfig);
//        } else {
//            // 单机版配置
//            factory = new LettuceConnectionFactory(RedisClusterUtils.redisStandaloneConfiguration(redisProperties), clientConfig);
//        }
//        factory.afterPropertiesSet();
//        return factory;
//    }
//
//    public GenericObjectPoolConfig genericObjectPoolConfig(RedisProperties.Pool pool) {
//        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
//        genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
//        genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
//        genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
//        if (pool.getMaxWait() != null) {
//            genericObjectPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
//        }
//        return genericObjectPoolConfig;
//    }
//}
