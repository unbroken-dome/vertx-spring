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


public interface VertxMetricsAdapter extends VertxMetrics {

    @Override
    default void verticleDeployed(Verticle verticle) {
    }

    @Override
    default void verticleUndeployed(Verticle verticle) {
    }

    @Override
    default void timerCreated(long id) {
    }

    @Override
    default void timerEnded(long id, boolean cancelled) {
    }

    @Override
    default EventBusMetrics createMetrics(EventBus eventBus) {
        return null;
    }

    @Override
    default HttpServerMetrics<?, ?, ?> createMetrics(HttpServer server, SocketAddress localAddress, HttpServerOptions options) {
        return null;
    }

    @Override
    default HttpClientMetrics<?, ?, ?, ?, ?> createMetrics(HttpClient client, HttpClientOptions options) {
        return null;
    }

    @Override
    default TCPMetrics<?> createMetrics(SocketAddress localAddress, NetServerOptions options) {
        return null;
    }

    @Override
    default TCPMetrics<?> createMetrics(NetClientOptions options) {
        return null;
    }

    @Override
    default DatagramSocketMetrics createMetrics(DatagramSocket socket, DatagramSocketOptions options) {
        return null;
    }

    @Override
    default <P> PoolMetrics<?> createMetrics(P pool, String poolType, String poolName, int maxPoolSize) {
        return null;
    }

    @Override
    default boolean isMetricsEnabled() {
        return true;
    }

    @Override
    default boolean isEnabled() {
        return true;
    }

    @Override
    default void close() {
    }
}
