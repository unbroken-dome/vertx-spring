package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.Verticle;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.DatagramSocketMetrics;
import io.vertx.core.spi.metrics.EventBusMetrics;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import io.vertx.core.spi.metrics.HttpServerMetrics;
import io.vertx.core.spi.metrics.PoolMetrics;
import io.vertx.core.spi.metrics.TCPMetrics;
import io.vertx.core.spi.metrics.VertxMetrics;

import java.util.List;


class DispatchingVertxMetrics
        extends AbstractDispatchingMetrics<VertxMetrics>
        implements VertxMetrics {

    public DispatchingVertxMetrics(List<? extends VertxMetrics> delegates) {
        super(delegates);
    }

    @Override
    public void verticleDeployed(Verticle verticle) {
        dispatch(m -> m.verticleDeployed(verticle));
    }


    @Override
    public void verticleUndeployed(Verticle verticle) {
        dispatch(m -> m.verticleUndeployed(verticle));
    }


    @Override
    public void timerCreated(long id) {
        dispatch(m -> m.timerCreated(id));
    }


    @Override
    public void timerEnded(long id, boolean cancelled) {
        dispatch(m -> m.timerEnded(id, cancelled));
    }


    @Override
    public EventBusMetrics<?> createMetrics(EventBus eventBus) {
        return createSubMetrics(v -> v.createMetrics(eventBus),
                d -> new DispatchingEventBusMetrics(d));
    }


    @Override
    public HttpServerMetrics<?, ?, ?> createMetrics(HttpServer server, SocketAddress localAddress, HttpServerOptions options) {
        return this.<HttpServerMetrics<?, ?, ?>> createSubMetrics(v -> v.createMetrics(server, localAddress, options),
                d -> new DispatchingHttpServerMetrics(d));
    }


    @Override
    public HttpClientMetrics<?, ?, ?, ?, ?> createMetrics(HttpClient client, HttpClientOptions options) {
        return this.<HttpClientMetrics<?, ?, ?, ?, ?>> createSubMetrics(v -> v.createMetrics(client, options),
                d -> new DispatchingHttpClientMetrics(d));
    }


    @Override
    public TCPMetrics<?> createMetrics(SocketAddress localAddress, NetServerOptions options) {
        return this.<TCPMetrics<?>> createSubMetrics(v -> v.createMetrics(localAddress, options),
                d -> new DispatchingTcpMetrics(d));
    }


    @Override
    public TCPMetrics<?> createMetrics(NetClientOptions options) {
        return this.<TCPMetrics<?>> createSubMetrics(v -> v.createMetrics(options),
                d -> new DispatchingTcpMetrics(d));
    }


    @Override
    public DatagramSocketMetrics createMetrics(DatagramSocket socket, DatagramSocketOptions options) {
        return createSubMetrics(v -> v.createMetrics(socket, options),
                d -> new DispatchingDatagramSocketMetrics(d));
    }


    @Override
    public <P> PoolMetrics<?> createMetrics(P pool, String poolType, String poolName, int maxPoolSize) {
        return this.<PoolMetrics<?>> createSubMetrics(v -> v.createMetrics(pool, poolType, poolName, maxPoolSize),
                d -> new DispatchingPoolMetrics(d));
    }


    @Override
    public boolean isMetricsEnabled() {
        return true;
    }
}
