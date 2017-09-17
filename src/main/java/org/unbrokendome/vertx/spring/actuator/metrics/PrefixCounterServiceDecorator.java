package org.unbrokendome.vertx.spring.actuator.metrics;

import org.springframework.boot.actuate.metrics.CounterService;


public class PrefixCounterServiceDecorator implements CounterService {

    private final CounterService delegate;
    private final String prefix;


    public PrefixCounterServiceDecorator(CounterService delegate, String prefix) {
        this.delegate = delegate;
        this.prefix = prefix;
    }


    @Override
    public void increment(String metricName) {
        delegate.increment(prefix + metricName);
    }


    @Override
    public void decrement(String metricName) {
        delegate.decrement(prefix + metricName);
    }


    @Override
    public void reset(String metricName) {
        delegate.reset(prefix + metricName);
    }
}
