package com.example.testcachelib.demo.config.redis;

import com.example.testcachelib.demo.config.redis.config.LettuceCreater;
import com.example.utils.EnvUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangbaowei
 * <p>
 * 假设取多个redis 实例，
 */
@Configuration
@AutoConfigureAfter(value = {RedisClientAutoConfig.class})
class MultiRedisClientConfig {
    @Bean
    public BeanDefinitionRegistryPostProcessor redisBeanDefinitionRegistryPostProcessor(Environment env) {
        return new RedisBeanDefinitionRegistryPostProcessor(env);
    }

    class RedisBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
        protected final Log logger = LogFactory.getLog(getClass());
        String propertPrefix = "spring.redis-ext";
        private Environment env;

        public RedisBeanDefinitionRegistryPostProcessor(Environment env) {
            this.env = env;
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

            BeanDefinition redisClientBeanDefine = registry.getBeanDefinition("redisClient");
            if (redisClientBeanDefine != null) {
                registry.removeBeanDefinition("redisClient");
                redisClientBeanDefine.setPrimary(true);
                registry.registerBeanDefinition("redisClient", redisClientBeanDefine);
                logger.info("remove redisClient then registerBeanDefinition redisClient setPrimary");
            }

            HashMap<String, Object> redisExtConfig = EnvUtils.getBindResult(this.env, propertPrefix, (new HashMap<String, Object>(1)).getClass());

            if (redisExtConfig == null) {
                return;
            }
            for (Map.Entry<String, Object> stringObjectEntry : redisExtConfig.entrySet()) {

                GenericBeanDefinition bd = new GenericBeanDefinition();
                bd.setBeanClass(RedisClient.class);

                bd.setInstanceSupplier(() -> {
                    RedisConnectionFactory redisConnectionFactory = LettuceCreater.create(env, propertPrefix + "." + stringObjectEntry.getKey());

                    RedisClient redisClient = new RedisClientImpl(redisConnectionFactory);
                    logger.info("register redisClient(" + stringObjectEntry.getKey() + ")");
                    return redisClient;
                });

                registry.registerBeanDefinition(stringObjectEntry.getKey(), bd);
            }
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//不干事

        }
    }
}