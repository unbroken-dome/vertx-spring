package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.NetworkMetrics;

import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
abstract class AbstractDispatchingNetworkMetrics<M extends NetworkMetrics>
        extends AbstractDispatchingMetrics<M>
        implements NetworkMetrics<Map<M, ?>> {

    protected AbstractDispatchingNetworkMetrics(List<? extends M> delegates) {
        super(delegates);
    }


    @Override
    public final void bytesRead(Map<M, ?> socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        unmap(socketMetric, (m, c) -> m.bytesRead(c, remoteAddress, numberOfBytes));
    }


    @Override
    public final void bytesWritten(Map<M, ?> socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        unmap(socketMetric, (m, c) -> m.bytesWritten(c, remoteAddress, numberOfBytes));
    }


    @Override
    public final void exceptionOccurred(Map<M, ?> socketMetric, SocketAddress remoteAddress, Throwable t) {
        unmap(socketMetric, (m, c) -> m.exceptionOccurred(c, remoteAddress, t));
    }
}
