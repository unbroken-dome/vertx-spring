package org.unbrokendome.vertx.spring.boot.clustermanager;

import io.vertx.ext.cluster.infinispan.InfinispanClusterManager;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(InfinispanClusterManager.class)
@Conditional(InfinispanClusterManagerConfiguration.InfinispanClusterManagerCondition.class)
public class InfinispanClusterManagerConfiguration {

    @Bean
    public InfinispanClusterManager infinispanClusterManager() {
        return new InfinispanClusterManager();
    }


    @SuppressWarnings("unused")
    static class InfinispanClusterManagerCondition extends AnyNestedCondition {

        public InfinispanClusterManagerCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", havingValue = "infinispan")
        static class InfinispanPropertyCondition { }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                ClusterManagerAutoConfiguration.HAZELCAST_CLUSTER_MANAGER,
                ClusterManagerAutoConfiguration.IGNITE_CLUSTER_MANAGER,
                ClusterManagerAutoConfiguration.ZOOKEEPER_CLUSTER_MANAGER
        })
        static class OnlyInfinispanOnClasspathCondition { }
    }
}
