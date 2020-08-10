//package com.example.testcache.demo.config;
//
//import java.time.Duration;
//import java.util.List;
//
///**
// * @author zhangbaowei
// * Create  on 2020/7/1 17:07.
// */
//
////@ConfigurationProperties(prefix = "spring.redis")
////@Component
//public class  Configtest {
//    private final Jedis jedis = new Jedis();
//    private final Lettuce lettuce = new Lettuce();
//    /**
//     * Database index used by the connection factory.
//     */
//    private int database = 0;
//    /**
//     * Connection URL. Overrides host, port, and password. User is ignored. Example:
//     * redis://user:password@example.com:6379
//     */
//    private String url;
//    /**
//     * Redis server host.
//     */
//    private String host = "localhost";
//    /**
//     * Login password of the redis server.
//     */
//    private String password;
//    /**
//     * Redis server port.
//     */
//    private int port = 6379;
//    /**
//     * Whether to enable SSL support.
//     */
//    private boolean ssl;
//    /**
//     * Connection timeout.
//     */
//    private Duration timeout;
//    private Sentinel sentinel;
//    private Cluster cluster;
//
//    public int getDatabase() {
//        return this.database;
//    }
//
//    public void setDatabase(int database) {
//        this.database = database;
//    }
//
//    public String getUrl() {
//        return this.url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public String getHost() {
//        return this.host;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    public String getPassword() {
//        return this.password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public int getPort() {
//        return this.port;
//    }
//
//    public void setPort(int port) {
//        this.port = port;
//    }
//
//    public boolean isSsl() {
//        return this.ssl;
//    }
//
//    public void setSsl(boolean ssl) {
//        this.ssl = ssl;
//    }
//
//    public Duration getTimeout() {
//        return this.timeout;
//    }
//
//    public void setTimeout(Duration timeout) {
//        this.timeout = timeout;
//    }
//
//    public Sentinel getSentinel() {
//        return this.sentinel;
//    }
//
//    public void setSentinel(Sentinel sentinel) {
//        this.sentinel = sentinel;
//    }
//
//    public Cluster getCluster() {
//        return this.cluster;
//    }
//
//    public void setCluster(Cluster cluster) {
//        this.cluster = cluster;
//    }
//
//    public Jedis getJedis() {
//        return this.jedis;
//    }
//
//    public Lettuce getLettuce() {
//        return this.lettuce;
//    }
//
//    /**
//     * Pool properties.
//     */
//    public static class Pool {
//
//        /**
//         * Maximum number of "idle" connections in the pool. Use a negative value to
//         * indicate an unlimited number of idle connections.
//         */
//        private int maxIdle = 8;
//
//        /**
//         * Target for the minimum number of idle connections to maintain in the pool. This
//         * setting only has an effect if it is positive.
//         */
//        private int minIdle = 0;
//
//        /**
//         * Maximum number of connections that can be allocated by the pool at a given
//         * time. Use a negative value for no limit.
//         */
//        private int maxActive = 8;
//
//        /**
//         * Maximum amount of time a connection allocation should block before throwing an
//         * exception when the pool is exhausted. Use a negative value to block
//         * indefinitely.
//         */
//        private Duration maxWait = Duration.ofMillis(-1);
//
//        public int getMaxIdle() {
//            return this.maxIdle;
//        }
//
//        public void setMaxIdle(int maxIdle) {
//            this.maxIdle = maxIdle;
//        }
//
//        public int getMinIdle() {
//            return this.minIdle;
//        }
//
//        public void setMinIdle(int minIdle) {
//            this.minIdle = minIdle;
//        }
//
//        public int getMaxActive() {
//            return this.maxActive;
//        }
//
//        public void setMaxActive(int maxActive) {
//            this.maxActive = maxActive;
//        }
//
//        public Duration getMaxWait() {
//            return this.maxWait;
//        }
//
//        public void setMaxWait(Duration maxWait) {
//            this.maxWait = maxWait;
//        }
//    }
//
//    /**
//     * Cluster properties.
//     */
//    public static class Cluster {
//
//        /**
//         * Comma-separated list of "host:port" pairs to bootstrap from. This represents an
//         * "initial" list of cluster nodes and is required to have at least one entry.
//         */
//        private List<String> nodes;
//
//        /**
//         * Maximum number of redirects to follow when executing commands across the
//         * cluster.
//         */
//        private Integer maxRedirects;
//
//        public List<String> getNodes() {
//            return this.nodes;
//        }
//
//        public void setNodes(List<String> nodes) {
//            this.nodes = nodes;
//        }
//
//        public Integer getMaxRedirects() {
//            return this.maxRedirects;
//        }
//
//        public void setMaxRedirects(Integer maxRedirects) {
//            this.maxRedirects = maxRedirects;
//        }
//    }
//
//    /**
//     * Redis sentinel properties.
//     */
//    public static class Sentinel {
//
//        /**
//         * Name of the Redis server.
//         */
//        private String master;
//
//        /**
//         * Comma-separated list of "host:port" pairs.
//         */
//        private List<String> nodes;
//
//        public String getMaster() {
//            return this.master;
//        }
//
//        public void setMaster(String master) {
//            this.master = master;
//        }
//
//        public List<String> getNodes() {
//            return this.nodes;
//        }
//
//        public void setNodes(List<String> nodes) {
//            this.nodes = nodes;
//        }
//    }
//
//    /**
//     * Jedis client properties.
//     */
//    public static class Jedis {
//
//        /**
//         * Jedis pool configuration.
//         */
//        private Pool pool;
//
//        public Pool getPool() {
//            return this.pool;
//        }
//
//        public void setPool(Pool pool) {
//            this.pool = pool;
//        }
//    }
//
//    /**
//     * Lettuce client properties.
//     */
//    public static class Lettuce {
//
//        /**
//         * Shutdown timeout.
//         */
//        private Duration shutdownTimeout = Duration.ofMillis(100);
//
//        /**
//         * Lettuce pool configuration.
//         */
//        private Pool pool;
//
//        public Duration getShutdownTimeout() {
//            return this.shutdownTimeout;
//        }
//
//        public void setShutdownTimeout(Duration shutdownTimeout) {
//            this.shutdownTimeout = shutdownTimeout;
//        }
//
//        public Pool getPool() {
//            return this.pool;
//        }
//
//        public void setPool(Pool pool) {
//            this.pool = pool;
//        }
//    }
//}
