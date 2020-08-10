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

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for Redis.
 *
 * @author Dave Syer
 * @author Christoph Strobl
 * @author Eddú Meléndez
 * @author Marco Aust
 * @author Mark Paluch
 * @author Stephane Nicoll
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class RedisClusterProperties /*extends RedisProperties.Cluster*/ {
    private boolean validateClusterNodeMembership = ClusterClientOptions.DEFAULT_VALIDATE_CLUSTER_MEMBERSHIP;
    private ClusterTopology clusterTopology = new ClusterTopology();

    public boolean isValidateClusterNodeMembership() {
        return validateClusterNodeMembership;
    }

    public void setValidateClusterNodeMembership(boolean validateClusterNodeMembership) {
        this.validateClusterNodeMembership = validateClusterNodeMembership;
    }

    public ClusterTopology getClusterTopology() {
        return clusterTopology;
    }

    public void setClusterTopology(ClusterTopology clusterTopology) {
        this.clusterTopology = clusterTopology;
    }

    public static class ClusterTopology {
        private Duration refreshPeriod = ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD_DURATION;

        private boolean enableAllAdaptiveRefreshTriggers = false;
        private Duration adaptiveRefreshTriggersTimeout = Duration.ofSeconds(30);

        public boolean isEnableAllAdaptiveRefreshTriggers() {
            return enableAllAdaptiveRefreshTriggers;
        }

        public void setEnableAllAdaptiveRefreshTriggers(boolean enableAllAdaptiveRefreshTriggers) {
            this.enableAllAdaptiveRefreshTriggers = enableAllAdaptiveRefreshTriggers;
        }

        public Duration getAdaptiveRefreshTriggersTimeout() {
            return adaptiveRefreshTriggersTimeout;
        }

        public void setAdaptiveRefreshTriggersTimeout(Duration adaptiveRefreshTriggersTimeout) {
            this.adaptiveRefreshTriggersTimeout = adaptiveRefreshTriggersTimeout;
        }

        public Duration getRefreshPeriod() {
            return refreshPeriod;
        }

        public void setRefreshPeriod(Duration refreshPeriod) {
            this.refreshPeriod = refreshPeriod;
        }
    }
}

