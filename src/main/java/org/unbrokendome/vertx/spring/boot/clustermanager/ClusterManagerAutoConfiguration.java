package org.unbrokendome.vertx.spring.boot.clustermanager;

import io.vertx.core.spi.cluster.ClusterManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.unbrokendome.vertx.spring.boot.VertxAutoConfiguration;


@Configuration
@AutoConfigureBefore(VertxAutoConfiguration.class)
@AutoConfigureAfter(HazelcastAutoConfiguration.class)
@ConditionalOnMissingBean(ClusterManager.class)
@Import({
        HazelcastClusterManagerConfiguration.class,
        IgniteClusterManagerConfiguration.class,
        InfinispanClusterManagerConfiguration.class,
        ZookeeperClusterManagerConfiguration.class
})
public class ClusterManagerAutoConfiguration {

    static final String HAZELCAST_CLUSTER_MANAGER = "io.vertx.spi.cluster.hazelcast.HazelcastClusterManager";
    static final String IGNITE_CLUSTER_MANAGER = "io.vertx.spi.cluster.ignite.IgniteClusterManager";
    static final String INFINISPAN_CLUSTER_MANAGER = "io.vertx.ext.cluster.infinispan.InfinispanClusterManager";
    static final String ZOOKEEPER_CLUSTER_MANAGER = "io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager";
}
