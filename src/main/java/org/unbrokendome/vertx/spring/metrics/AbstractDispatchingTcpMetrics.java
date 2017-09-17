package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.TCPMetrics;

import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
abstract class AbstractDispatchingTcpMetrics<M extends TCPMetrics>
        extends AbstractDispatchingNetworkMetrics<M>
        implements TCPMetrics<Map<M, ?>> {

    protected AbstractDispatchingTcpMetrics(List<? extends M> delegates) {
        super(delegates);
    }


    @Override
    public Map<M, ?> connected(SocketAddress remoteAddress, String remoteName) {
        return dispatchWithResult(m -> m.connected(remoteAddress, remoteName));
    }


    @Override
    public void disconnected(Map<M, ?> socketMetric, SocketAddress remoteAddress) {
        unmap(socketMetric, (m, c) -> m.disconnected(c, remoteAddress));
    }
}
