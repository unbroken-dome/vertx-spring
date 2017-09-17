package org.unbrokendome.vertx.spring.actuator

import org.springframework.boot.SpringApplication
import spock.lang.Specification


class VertxAutoConfigurationTest extends Specification {

    def "Application should start"() {
        when:
            SpringApplication.run(TestApplication)
        then:
            noExceptionThrown()
    }
}
