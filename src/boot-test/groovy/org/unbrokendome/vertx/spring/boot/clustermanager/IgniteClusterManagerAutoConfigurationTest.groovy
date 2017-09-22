package org.unbrokendome.vertx.spring.boot.clustermanager

import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.spi.cluster.ignite.IgniteClusterManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification


@SpringBootTest
@ImportAutoConfiguration(ClusterManagerAutoConfiguration)
@TestPropertySource(properties = [
        'vertx.cluster-manager.type=ignite',
        'logging.level.org.springframework.boot.autoconfigure=DEBUG'
])
class IgniteClusterManagerAutoConfigurationTest extends Specification {

    @Autowired(required = false)
    ClusterManager clusterManager

    def "Should create IgniteClusterManager bean"() {
        expect:
            clusterManager instanceof IgniteClusterManager
    }
}
