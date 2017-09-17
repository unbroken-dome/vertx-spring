package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.spi.metrics.TCPMetrics;

import java.util.List;


class DispatchingTcpMetrics extends AbstractDispatchingTcpMetrics<TCPMetrics> {

    public DispatchingTcpMetrics(List<? extends TCPMetrics> delegates) {
        super(delegates);
    }
}
