package org.unbrokendome.vertx.spring.actuator.metrics;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(VertxMetricsProperties.class)
public class VertxMetricsConfiguration {

    private final CounterService counterService;
    private final GaugeService gaugeService;
    private final VertxMetricsProperties metricsProperties;


    public VertxMetricsConfiguration(CounterService counterService, GaugeService gaugeService,
                                     VertxMetricsProperties metricsProperties) {
        this.counterService = counterService;
        this.gaugeService = gaugeService;
        this.metricsProperties = metricsProperties;
    }


    @Bean
    public VertxActuatorMetrics vertxActuatorMetrics() {
        return new VertxActuatorMetrics(counterService, gaugeService, metricsProperties);
    }
}
