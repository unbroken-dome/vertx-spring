package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.metrics.impl.DummyVertxMetrics;
import io.vertx.core.spi.VertxMetricsFactory;
import io.vertx.core.spi.metrics.VertxMetrics;

import java.util.ArrayList;
import java.util.List;


public class DispatchingVertxMetricsFactory implements VertxMetricsFactory {

    private final List<? extends VertxMetricsFactory> delegates;


    public DispatchingVertxMetricsFactory(List<? extends VertxMetricsFactory> delegates) {
        this.delegates = delegates;
    }


    @Override
    public VertxMetrics metrics(Vertx vertx, VertxOptions options) {
        List<VertxMetrics> allMetrics = new ArrayList<>(delegates.size());
        for (VertxMetricsFactory delegate : delegates) {
            VertxMetrics metrics = delegate.metrics(vertx, options);
            if (metrics != null) {
                allMetrics.add(metrics);
            }
        }
        if (allMetrics.isEmpty()) {
            return new DummyVertxMetrics();
        } else if (allMetrics.size() == 1) {
            return allMetrics.get(0);
        } else {
            return new DispatchingVertxMetrics(allMetrics);
        }
    }
}
