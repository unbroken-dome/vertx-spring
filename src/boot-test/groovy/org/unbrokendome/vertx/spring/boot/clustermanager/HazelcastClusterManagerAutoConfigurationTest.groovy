package org.unbrokendome.vertx.spring.boot.clustermanager

import com.hazelcast.config.Config
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification


@SpringBootTest
@ImportAutoConfiguration([ClusterManagerAutoConfiguration, HazelcastAutoConfiguration])
@TestPropertySource(properties = [
        'vertx.cluster-manager.type=hazelcast',
        'logging.level.org.springframework.boot.autoconfigure=DEBUG'
])
class HazelcastClusterManagerAutoConfigurationTest extends Specification {

    @Configuration
    static class TestConfig {

        @Bean
        Config hazelcastConfig() {
            return new Config()
        }
    }

    @Autowired(required = false)
    ClusterManager clusterManager

    def "Should create HazelcastClusterManager bean"() {
        expect:
            clusterManager instanceof HazelcastClusterManager
    }
}
