package org.unbrokendome.vertx.spring.actuator.metrics;

import org.springframework.boot.actuate.metrics.GaugeService;


public class PrefixGaugeServiceDecorator implements GaugeService {

    private final GaugeService delegate;
    private final String prefix;


    public PrefixGaugeServiceDecorator(GaugeService delegate, String prefix) {
        this.delegate = delegate;
        this.prefix = prefix;
    }


    @Override
    public void submit(String metricName, double value) {

    }
}
