//package com.example.testcache.demo.config;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.beans.factory.support.GenericBeanDefinition;
//import org.springframework.boot.context.properties.bind.Binder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.util.CollectionUtils;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author zhangbaowei
// * <p>
// * 假设取多个redis 实例，
// */
//@Configuration
//public class MultiRedisConfig {
//    @Bean
//    public BeanDefinitionRegistryPostProcessor redisBeanDefinitionRegistryPostProcessor(Environment env) {
//        return new RedisBeanDefinitionRegistryPostProcessor(env);
//    }
//
//
//
//    class RedisBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
//        final String prefix = "redis-";
//        private Environment env;
//
//        public RedisBeanDefinitionRegistryPostProcessor(Environment env) {
//            this.env = env;
//        }
//
//        private List<String> getRedisConfigs() {
//            Binder binder = Binder.get(env);
//            HashMap<String, Object> springNode = binder.bind("spring", new HashMap<>().getClass()).orElse(null);
//            List<String> listRedis = new ArrayList<>();
//            for (Map.Entry<String, Object> stringObjectEntry : springNode.entrySet()) {
//                if (stringObjectEntry.getKey().startsWith(prefix)) {
//                    listRedis.add(stringObjectEntry.getKey());
//                }
//            }
//            return listRedis;
//        }
//
//        @Override
//        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
//
//            List<String> redisConfigs = getRedisConfigs();
//            if (CollectionUtils.isEmpty(redisConfigs)) {
//                return;
//            }
//            //设置 setPrimary
//            GenericBeanDefinition primarybd = new GenericBeanDefinition();
//            primarybd.setBeanClass(Configtest.class);
//
//            primarybd.setInstanceSupplier(() -> {
//                Binder binder = Binder.get(env);
//                Configtest configtest = new Configtest();
//                configtest.setDatabase(12345);
//                return configtest;
//            });
//
//            primarybd.setPrimary(true);
//
//            registry.registerBeanDefinition("default", primarybd);
//
//            for (String redisConfig : redisConfigs) {
//                GenericBeanDefinition bd = new GenericBeanDefinition();
//                bd.setBeanClass(Configtest.class);
//
//                bd.setInstanceSupplier(() -> {
//                    Binder binder = Binder.get(env);
//                    Configtest configtest = binder.bind("spring." + redisConfig, Configtest.class).orElse(null);
//                    return configtest;
//                });
//                int beginIndex = prefix.length();
//                registry.registerBeanDefinition(redisConfig.substring(beginIndex), bd);
//            }
//        }
//
//        @Override
//        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
////不干事
//
//        }
//    }
//}