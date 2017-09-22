package org.unbrokendome.vertx.spring.boot.clustermanager

import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.ext.cluster.infinispan.InfinispanClusterManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification


@SpringBootTest
@ImportAutoConfiguration(ClusterManagerAutoConfiguration)
@TestPropertySource(properties = [
        'vertx.cluster-manager.type=infinispan',
        'logging.level.org.springframework.boot.autoconfigure=DEBUG'
])
class InfinispanClusterManagerAutoConfigurationTest extends Specification {

    @Autowired(required = false)
    ClusterManager clusterManager

    def "Should create InfinispanClusterManager bean"() {
        expect:
            clusterManager instanceof InfinispanClusterManager
    }
}
