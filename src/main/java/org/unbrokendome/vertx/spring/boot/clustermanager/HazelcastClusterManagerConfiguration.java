package org.unbrokendome.vertx.spring.boot.clustermanager;

import com.hazelcast.core.HazelcastInstance;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(HazelcastClusterManager.class)
@ConditionalOnBean(HazelcastInstance.class)
public class HazelcastClusterManagerConfiguration {

    @Bean
    public HazelcastClusterManager hazelcastClusterManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastClusterManager(hazelcastInstance);
    }


    @SuppressWarnings("unused")
    static class HazelcastClusterManagerCondition extends AnyNestedCondition {

        public HazelcastClusterManagerCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", havingValue = "hazelcast")
        static class HazelcastPropertyCondition { }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                ClusterManagerAutoConfiguration.IGNITE_CLUSTER_MANAGER,
                ClusterManagerAutoConfiguration.INFINISPAN_CLUSTER_MANAGER,
                ClusterManagerAutoConfiguration.ZOOKEEPER_CLUSTER_MANAGER
        })
        static class OnlyHazelcastOnClasspathCondition { }
    }
}
