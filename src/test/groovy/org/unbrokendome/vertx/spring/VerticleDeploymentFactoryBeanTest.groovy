package org.unbrokendome.vertx.spring

import io.vertx.core.Context
import io.vertx.core.Verticle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.AbstractFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicInteger


@ContextConfiguration
class VerticleDeploymentFactoryBeanTest extends Specification {

    static class TestVerticleFactoryBean extends AbstractFactoryBean<TestVerticle> {

        @Override
        Class<?> getObjectType() { TestVerticle }

        @Override
        protected TestVerticle createInstance() { new TestVerticle() }

        @Override
        boolean isSingleton() { false }
    }

    @Configuration
    @EnableVertx
    static class TestConfig implements VertxListener {

        @Bean
        AtomicInteger deploymentCounter() { new AtomicInteger(0) }

        @Bean
        @VerticleDeployment(instances = 4)
        TestVerticleFactoryBean testVerticle() { new TestVerticleFactoryBean() }

        @Override
        void verticleDeployed(Verticle verticle, Context context) {
            if (verticle instanceof TestVerticle) {
                deploymentCounter().incrementAndGet()
            }
        }
    }

    @Autowired @Qualifier('deploymentCounter')
    AtomicInteger deploymentCounter

    def "Verticle should be deployed with multiple instances"() {
        expect:
            deploymentCounter.get() == 4
    }
}
