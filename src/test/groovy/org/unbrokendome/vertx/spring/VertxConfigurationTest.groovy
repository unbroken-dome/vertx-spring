package org.unbrokendome.vertx.spring

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification


class VertxConfigurationTest extends Specification {

    def "Spring configuration should load"() {
        given:
            def applicationContext = new AnnotationConfigApplicationContext()
            applicationContext.register(VertxConfiguration)
        when:
            applicationContext.refresh()
        then:
            noExceptionThrown()
    }
}
