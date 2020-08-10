package com.example.testcachelib.demo.config.redis.config;

import com.example.utils.EnvUtils;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * @author zhangbaowei
 * Create  on 2020/7/8 16:03.
 */
public class LettuceCreater {

    public static RedisConnectionFactory create(Environment env, String propertybase) {

        RedisProperties properties = EnvUtils.getBindResult(env, propertybase, RedisProperties.class);
        RedisLettuceProperties redisLettuceProperties = EnvUtils.getBindResult(env, propertybase + "." + "lettuce", RedisLettuceProperties.class);
        RedisClusterProperties redisClusterProperties = EnvUtils.getBindResult(env, propertybase + "." + "cluster", RedisClusterProperties.class);

        LettuceConnectionConfiguration lettuceConnectionConfiguration = new LettuceConnectionConfiguration(properties,
                redisLettuceProperties,
                redisClusterProperties,
                nullObjectProvider(RedisSentinelConfiguration.class),
                nullObjectProvider(RedisClusterConfiguration.class),
                nullObjectProvider(LettuceClientConfigurationBuilderCustomizer.class));

        DefaultClientResources defaultClientResources = lettuceConnectionConfiguration.lettuceClientResources();
        LettuceConnectionFactory lettuceConnectionFactory = lettuceConnectionConfiguration.redisConnectionFactory(defaultClientResources);

        return lettuceConnectionFactory;
    }

    private static <T> ObjectProvider<T> nullObjectProvider(Class<T> tClass) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(LettuceCreater.class);
        ObjectProvider<T> beanProvider = context.getBeanProvider(tClass);

        return beanProvider;
    }
}
