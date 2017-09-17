package org.unbrokendome.vertx.spring.actuator.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "vertx.metrics")
public class VertxMetricsProperties extends AbstractPartMetricsProperties {

    private final EventBusMetricsProperties eventBus = new EventBusMetricsProperties();
    private final PoolMetricsProperties pool = new PoolMetricsProperties();
    private final NetMetricsProperties net = new NetMetricsProperties();
    private final HttpMetricsProperties http = new HttpMetricsProperties();
    private final DatagramSocketProperties datagramSocket = new DatagramSocketProperties();


    public VertxMetricsProperties() {
        super("vertx");
    }

    public EventBusMetricsProperties getEventBus() {
        return eventBus;
    }

    public PoolMetricsProperties getPool() {
        return pool;
    }

    public NetMetricsProperties getNet() {
        return net;
    }

    public HttpMetricsProperties getHttp() {
        return http;
    }

    public DatagramSocketProperties getDatagramSocket() {
        return datagramSocket;
    }


    public static class EventBusMetricsProperties extends AbstractPartMetricsProperties {

        public EventBusMetricsProperties() {
            super("eventbus");
        }
    }


    public static class PoolMetricsProperties extends AbstractPartMetricsProperties {

        public PoolMetricsProperties() {
            super("pool");
        }
    }


    public static class NetMetricsProperties {

        private final NetClientMetricsProperties client = new NetClientMetricsProperties();
        private final NetServerMetricsProperties server = new NetServerMetricsProperties();

        public NetClientMetricsProperties getClient() {
            return client;
        }

        public NetServerMetricsProperties getServer() {
            return server;
        }
    }

    public static class NetClientMetricsProperties extends AbstractPartMetricsProperties {

        public NetClientMetricsProperties() {
            super("net.client");
        }
    }

    public static class NetServerMetricsProperties extends AbstractPartMetricsProperties {

        public NetServerMetricsProperties() {
            super("net.server");
        }
    }

    public static class HttpMetricsProperties {

        private final HttpClientMetricsProperties client = new HttpClientMetricsProperties();
        private final HttpServerMetricsProperties server = new HttpServerMetricsProperties();

        public HttpClientMetricsProperties getClient() {
            return client;
        }

        public HttpServerMetricsProperties getServer() {
            return server;
        }
    }

    public static class HttpClientMetricsProperties extends AbstractPartMetricsProperties {

        public HttpClientMetricsProperties() {
            super("http.client");
        }
    }

    public static class HttpServerMetricsProperties extends AbstractPartMetricsProperties {

        public HttpServerMetricsProperties() {
            super("http.server");
        }
    }

    public static class DatagramSocketProperties extends AbstractPartMetricsProperties {

        public DatagramSocketProperties() {
            super("datagram");
        }
    }
}


