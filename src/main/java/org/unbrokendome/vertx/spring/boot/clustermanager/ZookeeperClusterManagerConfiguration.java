package org.unbrokendome.vertx.spring.boot.clustermanager;

import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
@ConditionalOnClass(ZookeeperClusterManager.class)
@Conditional(ZookeeperClusterManagerConfiguration.ZookeeperClusterManagerCondition.class)
@EnableConfigurationProperties(ZookeeperClusterManagerProperties.class)
public class ZookeeperClusterManagerConfiguration {

    @Bean
    public ZookeeperClusterManager zookeeperClusterManager(
            ZookeeperClusterManagerProperties properties) throws IOException {

        return new ZookeeperClusterManager(properties.getConfigAsJsonObject());
    }


    @SuppressWarnings("unused")
    static class ZookeeperClusterManagerCondition extends AnyNestedCondition {

        public ZookeeperClusterManagerCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", havingValue = "zookeeper")
        static class ZookeeperPropertyCondition { }

        @ConditionalOnProperty(prefix = "vertx.cluster-manager", name = "type", matchIfMissing = true)
        @ConditionalOnMissingClass({
                ClusterManagerAutoConfiguration.HAZELCAST_CLUSTER_MANAGER,
                ClusterManagerAutoConfiguration.IGNITE_CLUSTER_MANAGER,
                ClusterManagerAutoConfiguration.INFINISPAN_CLUSTER_MANAGER
        })
        static class OnlyZookeeperOnClasspathCondition { }
    }
}
