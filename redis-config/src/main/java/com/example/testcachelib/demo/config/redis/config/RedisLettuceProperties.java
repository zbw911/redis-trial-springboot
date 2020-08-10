package com.example.testcachelib.demo.config.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author zhangbaowei
 */
@ConfigurationProperties(prefix = "spring.redis.lettuce")
public class RedisLettuceProperties /*extends RedisProperties.Lettuce*/ {

    private SocketOptions socketOptions;

    public SocketOptions getSocketOptions() {
        return socketOptions;
    }

    public void setSocketOptions(SocketOptions socketOptions) {
        this.socketOptions = socketOptions;
    }

    public static class SocketOptions {
        public static final Duration DEFAULT_CONNECT_TIMEOUT_DURATION = Duration.ofSeconds(10);

        public static final boolean DEFAULT_SO_KEEPALIVE = false;
        public static final boolean DEFAULT_SO_NO_DELAY = false;

        private Duration connectTimeout = DEFAULT_CONNECT_TIMEOUT_DURATION;
        private boolean keepAlive = DEFAULT_SO_KEEPALIVE;
        private boolean tcpNoDelay = DEFAULT_SO_NO_DELAY;

        public Duration getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public boolean isKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
        }

        public boolean isTcpNoDelay() {
            return tcpNoDelay;
        }

        public void setTcpNoDelay(boolean tcpNoDelay) {
            this.tcpNoDelay = tcpNoDelay;
        }
    }

}
