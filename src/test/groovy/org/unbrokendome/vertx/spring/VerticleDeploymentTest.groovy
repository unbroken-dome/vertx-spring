package org.unbrokendome.vertx.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification


@ContextConfiguration
class VerticleDeploymentTest extends Specification {

    @Configuration
    @EnableVertx
    static class TestConfig {

        @Bean
        TestVerticle testVerticle() { new TestVerticle() }
    }

    @Autowired
    TestVerticle verticle

    def "Verticle should be deployed"() {
        expect:
            verticle.deployed
    }
}
