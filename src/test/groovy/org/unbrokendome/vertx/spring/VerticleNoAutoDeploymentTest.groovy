package org.unbrokendome.vertx.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification


@ContextConfiguration
class VerticleNoAutoDeploymentTest extends Specification {

    @Configuration
    @EnableVertx(deployVerticles = false)
    static class TestConfig {

        @Bean
        TestVerticle testVerticle() { new TestVerticle() }
    }

    @Autowired
    TestVerticle verticle

    def "Verticle should not be deployed"() {
        expect:
            !verticle.deployed
    }
}
