package org.unbrokendome.vertx.spring.boot;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.unbrokendome.vertx.spring.SpringVertx;
import org.unbrokendome.vertx.spring.VerticleDeploymentConfiguration;
import org.unbrokendome.vertx.spring.VertxConfiguration;
import org.unbrokendome.vertx.spring.VertxConfigurer;
import org.unbrokendome.vertx.spring.actuator.metrics.VertxMetricsConfiguration;


@Configuration
public class VertxAutoConfiguration {

    @Configuration
    @EnableConfigurationProperties(VertxProperties.class)
    public static class PropertiesVertxConfigurer implements VertxConfigurer {

        private final VertxProperties vertxProperties;

        public PropertiesVertxConfigurer(VertxProperties vertxProperties) {
            this.vertxProperties = vertxProperties;
        }

        @Override
        public void configure(SpringVertx.Builder builder) {
            builder.options(vertxProperties.toVertxOptions());
        }
    }


    @Configuration
    @ConditionalOnMissingBean(SpringVertx.class)
    @Import(VertxConfiguration.class)
    public static class SpringVertxAutoConfiguration {
    }


    @Configuration
    @ConditionalOnProperty(prefix = "vertx", name = "auto-deploy-verticles", matchIfMissing = true)
    @Import(VerticleDeploymentConfiguration.class)
    public static class VerticleDeploymentAutoConfiguration {
    }


    @Configuration
    @ConditionalOnBean({ CounterService.class, GaugeService.class })
    @Import(VertxMetricsConfiguration.class)
    public static class VertxMetricsAutoConfiguration {
    }
}
