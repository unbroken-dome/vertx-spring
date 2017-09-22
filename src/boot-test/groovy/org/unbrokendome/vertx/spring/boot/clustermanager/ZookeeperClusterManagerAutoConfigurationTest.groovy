package org.unbrokendome.vertx.spring.boot.clustermanager

import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification


@SpringBootTest
@ImportAutoConfiguration(ClusterManagerAutoConfiguration)
@TestPropertySource(properties = [
        'vertx.cluster-manager.type=zookeeper',
        'logging.level.org.springframework.boot.autoconfigure=DEBUG'
])
class ZookeeperClusterManagerAutoConfigurationTest extends Specification {

    @Autowired(required = false)
    ClusterManager clusterManager

    def "Should create ZookeeperClusterManager bean"() {
        expect:
            clusterManager instanceof ZookeeperClusterManager
    }
}
