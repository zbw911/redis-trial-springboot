/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.testcachelib.demo.config.redis.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.StringUtils;

/**
 * Redis connection configuration using Lettuce.
 *
 * @author Mark Paluch
 * @author Andy Wilkinson
 */
@Configuration
@ConditionalOnClass(RedisClient.class)
class LettuceConnectionConfiguration extends RedisConnectionConfiguration {

    private final RedisProperties properties;
    private final ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers;
    private RedisLettuceProperties redisLettuceProperties;
    private RedisClusterProperties redisClusterProperties;

    LettuceConnectionConfiguration(RedisProperties properties,
                                   RedisLettuceProperties redisLettuceProperties,
                                   RedisClusterProperties redisClusterProperties,
                                   ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                   ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider,
                                   ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers) {
        super(properties, sentinelConfigurationProvider, clusterConfigurationProvider);
        this.properties = properties;
        this.redisLettuceProperties = redisLettuceProperties;
        this.redisClusterProperties = redisClusterProperties;
        this.builderCustomizers = builderCustomizers;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(ClientResources.class)
    public DefaultClientResources lettuceClientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public LettuceConnectionFactory redisConnectionFactory(ClientResources clientResources) {
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(clientResources,
                this.properties.getLettuce().getPool());

        LettuceConnectionFactory lettuceConnectionFactory = createLettuceConnectionFactory(clientConfig);
        lettuceConnectionFactory.afterPropertiesSet(); // 这行，在 bean 模式下是多余的，但是当手动实例化的时候，就是必须的了。 （ InitializingBean）
        return lettuceConnectionFactory;
    }

    private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration) {
        if (getSentinelConfig() != null) {
            return new LettuceConnectionFactory(getSentinelConfig(), clientConfiguration);
        }
        if (getClusterConfiguration() != null) {
            RedisClusterConfiguration clusterConfiguration = getClusterConfiguration();

            return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
        }
        return new LettuceConnectionFactory(getStandaloneConfig(), clientConfiguration);
    }

    private LettuceClientConfigurationBuilder createBuilder(Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return new PoolBuilderFactory().createBuilder(pool);
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(ClientResources clientResources, Pool pool) {
        LettuceClientConfigurationBuilder builder = createBuilder(pool);
        applyProperties(builder);
        if (StringUtils.hasText(this.properties.getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builder.clientResources(clientResources);
        customize(builder);
        clientOptions(builder);

        return builder.build();
    }

    private LettuceClientConfigurationBuilder applyProperties(
            LettuceClientConfigurationBuilder builder) {
        if (this.properties.isSsl()) {
            builder.useSsl();
        }
        if (this.properties.getTimeout() != null) {
            builder.commandTimeout(this.properties.getTimeout());
        }
        if (this.properties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = this.properties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(this.properties.getLettuce().getShutdownTimeout());
            }
        }

        return builder;
    }

    private void clientOptions(LettuceClientConfigurationBuilder builder) {
        ClientOptions.Builder clientOptionsBuilder = ClientOptions
                .builder();
        // socket setting
        if (this.redisLettuceProperties != null) {
            if (redisLettuceProperties.getSocketOptions() != null) {
                SocketOptions.Builder socketOptionsBuilder = SocketOptions.builder()
                        .keepAlive(redisLettuceProperties.getSocketOptions().isKeepAlive())
                        .tcpNoDelay(redisLettuceProperties.getSocketOptions().isTcpNoDelay())
                        .connectTimeout(this.redisLettuceProperties.getSocketOptions().getConnectTimeout());
                clientOptionsBuilder.socketOptions(socketOptionsBuilder.build());
            }

            builder.clientOptions(clientOptionsBuilder.build());
        }

        if (this.redisClusterProperties != null) {
            ClusterClientOptions.Builder clusterClientOptionBuilder = ClusterClientOptions.builder(clientOptionsBuilder.build());
            if (this.redisClusterProperties.getClusterTopology() != null) {
                ClusterTopologyRefreshOptions.Builder clusterTopologyRefreshOptionsBuilder = ClusterTopologyRefreshOptions.builder();
                if (this.redisClusterProperties.getClusterTopology().getRefreshPeriod().isZero()) {
                    clusterTopologyRefreshOptionsBuilder.enablePeriodicRefresh(false);
                } else {
                    clusterTopologyRefreshOptionsBuilder.enablePeriodicRefresh(this.redisClusterProperties.getClusterTopology().getRefreshPeriod());
                }

                if (this.redisClusterProperties.getClusterTopology().isEnableAllAdaptiveRefreshTriggers()) {

                    clusterTopologyRefreshOptionsBuilder.enableAllAdaptiveRefreshTriggers();
                    if (!this.redisClusterProperties.getClusterTopology().getAdaptiveRefreshTriggersTimeout().isZero()) {
                        clusterTopologyRefreshOptionsBuilder.adaptiveRefreshTriggersTimeout(this.redisClusterProperties.getClusterTopology().getAdaptiveRefreshTriggersTimeout());
                    }
                }
                clusterClientOptionBuilder.topologyRefreshOptions(clusterTopologyRefreshOptionsBuilder.build());
            }

            clusterClientOptionBuilder.validateClusterNodeMembership(this.redisClusterProperties.isValidateClusterNodeMembership());

            builder.clientOptions(clusterClientOptionBuilder.build());
        }
    }

    private void customizeConfigurationFromUrl(LettuceClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

    private void customize(LettuceClientConfigurationBuilder builder) {
        this.builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    }

    /**
     * Inner class to allow optional commons-pool2 dependency.
     */
    private static class PoolBuilderFactory {

        public LettuceClientConfigurationBuilder createBuilder(Pool properties) {
            return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(properties));
        }

        private GenericObjectPoolConfig<?> getPoolConfig(Pool properties) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getTimeBetweenEvictionRuns() != null) {
                config.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRuns().toMillis());
            }
            if (properties.getMaxWait() != null) {
                config.setMaxWaitMillis(properties.getMaxWait().toMillis());
            }
            return config;
        }
    }
}
