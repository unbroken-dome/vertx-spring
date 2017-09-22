package org.unbrokendome.vertx.spring.boot.clustermanager;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "vertx.cluster-manager")
public class ClusterManagerProperties {

    enum ClusterManagerType {
        HAZELCAST,
        IGNITE,
        INFINISPAN,
        ZOOKEEPER
    }

    private ClusterManagerType type;


    public ClusterManagerType getType() {
        return type;
    }


    public void setType(ClusterManagerType type) {
        this.type = type;
    }
}
