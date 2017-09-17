package org.unbrokendome.vertx.spring.boot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.unbrokendome.vertx.spring.TestVerticle
import spock.lang.Specification


@SpringBootTest
@TestPropertySource(properties = [
    'vertx.auto-deploy-verticles=false'
])
class BootVerticleNoAutoDeploymentTest extends Specification {

    @Autowired
    TestVerticle verticle

    def "Should not auto-deploy verticles"() {
        expect:
            !verticle.deployed
    }
}
