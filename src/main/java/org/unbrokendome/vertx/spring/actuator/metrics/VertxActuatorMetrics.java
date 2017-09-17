package org.unbrokendome.vertx.spring.actuator.metrics;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.VertxMetricsFactory;
import io.vertx.core.spi.metrics.DatagramSocketMetrics;
import io.vertx.core.spi.metrics.EventBusMetrics;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import io.vertx.core.spi.metrics.HttpServerMetrics;
import io.vertx.core.spi.metrics.Metrics;
import io.vertx.core.spi.metrics.NetworkMetrics;
import io.vertx.core.spi.metrics.PoolMetrics;
import io.vertx.core.spi.metrics.TCPMetrics;
import io.vertx.core.spi.metrics.VertxMetrics;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.util.StopWatch;


public class VertxActuatorMetrics implements VertxMetricsFactory {

    private final CounterService counterService;
    private final GaugeService gaugeService;
    private final VertxMetricsProperties properties;

    public VertxActuatorMetrics(CounterService counterService, GaugeService gaugeService,
                                VertxMetricsProperties properties) {
        this.properties = properties;
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }


    @Override
    public VertxMetrics metrics(Vertx vertx, VertxOptions options) {
        return new VertxMetricsImpl(counterService, gaugeService, properties);
    }


    private static abstract class AbstractPartMetrics<P extends AbstractPartMetricsProperties> implements Metrics {
        protected final P properties;
        protected final CounterService counterService;
        protected final GaugeService gaugeService;
        private final String prefix;

        public AbstractPartMetrics(CounterService counterService, GaugeService gaugeService, P properties) {
            this.properties = properties;
            this.prefix = properties.getPrefix() + ".";
            this.counterService = new PrefixCounterServiceDecorator(counterService, prefix);
            this.gaugeService = new PrefixGaugeServiceDecorator(gaugeService, prefix);

            this.counterService.increment("instances.active");
            this.counterService.increment("instances.total");
        }


        @Override
        public boolean isEnabled() {
            return properties.isEnabled();
        }

        @Override
        public void close() {
            counterService.decrement("instances.active");
        }
    }


    private static class VertxMetricsImpl extends AbstractPartMetrics<VertxMetricsProperties> implements VertxMetrics {

        public VertxMetricsImpl(CounterService counterService, GaugeService gaugeService,
                                VertxMetricsProperties properties) {
            super(counterService, gaugeService, properties);
        }

        @Override
        public void verticleDeployed(Verticle verticle) {
            counterService.increment("verticles.deployed");
            counterService.increment("verticles.deployed.total");
        }

        @Override
        public void verticleUndeployed(Verticle verticle) {
            counterService.decrement("verticles.deployed");
        }

        @Override
        public void timerCreated(long id) {
            counterService.increment("timers.active");
            counterService.increment("timers.created.total");
        }

        @Override
        public void timerEnded(long id, boolean cancelled) {
            counterService.decrement("timers.active");
        }

        @Override
        public EventBusMetrics createMetrics(EventBus eventBus) {
            return new EventBusMetricsImpl(counterService, gaugeService, properties.getEventBus());
        }

        @Override
        public HttpServerMetrics<?, ?, ?> createMetrics(HttpServer server, SocketAddress localAddress,
                                                        HttpServerOptions options) {
            return new HttpServerMetricsImpl(counterService, gaugeService, properties.getHttp().getServer());
        }

        @Override
        public HttpClientMetrics<?, ?, ?, ?, ?> createMetrics(HttpClient client, HttpClientOptions options) {
            return new HttpClientMetricsImpl(counterService, gaugeService, properties.getHttp().getClient());
        }

        @Override
        public TCPMetrics<?> createMetrics(SocketAddress localAddress, NetServerOptions options) {
            return new NetServerMetricsImpl(counterService, gaugeService, properties.getNet().getServer());
        }

        @Override
        public TCPMetrics<?> createMetrics(NetClientOptions options) {
            return new NetClientMetricsImpl(counterService, gaugeService, properties.getNet().getClient());
        }

        @Override
        public DatagramSocketMetrics createMetrics(DatagramSocket socket, DatagramSocketOptions options) {
            return new DatagramSocketMetricsImpl(counterService, gaugeService, properties.getDatagramSocket());
        }

        @Override
        public <P> PoolMetrics<?> createMetrics(P pool, String poolType, String poolName, int maxPoolSize) {
            return new PoolMetricsImpl(counterService, gaugeService, properties.getPool());
        }

        @Override
        public boolean isMetricsEnabled() {
            return true;
        }
    }


    private static class EventBusMetricsImpl
            extends AbstractPartMetrics<VertxMetricsProperties.EventBusMetricsProperties>
            implements EventBusMetrics<Object> {

        public EventBusMetricsImpl(CounterService counterService, GaugeService gaugeService,
                                   VertxMetricsProperties.EventBusMetricsProperties properties) {
            super(counterService, gaugeService, properties);
        }

        @Override
        public Object handlerRegistered(String address, String repliedAddress) {
            return null;
        }

        @Override
        public void handlerUnregistered(Object handler) {
        }

        @Override
        public void scheduleMessage(Object handler, boolean local) {
            counterService.increment("messages.scheduled.total");
            if (local) {
                counterService.increment("messages.scheduled.local");
            } else {
                counterService.increment("messages.scheduled.remote");
            }
        }

        @Override
        public void beginHandleMessage(Object handler, boolean local) {
            counterService.increment("messages.handled.total");
            if (local) {
                counterService.increment("messages.handled.local");
            } else {
                counterService.increment("messages.handled.remote");
            }
        }

        @Override
        public void endHandleMessage(Object handler, Throwable failure) {
            counterService.increment("messages.completed.total");
            if (failure != null) {
                counterService.increment("messages.completed.failure");
            } else {
                counterService.increment("messages.completed.success");
            }
        }

        @Override
        public void messageSent(String address, boolean publish, boolean local, boolean remote) {
            if (publish) {
                counterService.increment("messages.published.total");
                if (local) {
                    counterService.increment("messages.published.local");
                }
                if (remote) {
                    counterService.increment("messages.published.remote");
                }
            } else {
                counterService.increment("messages.sent.total");
                if (local) {
                    counterService.increment("messages.sent.local");
                }
                if (remote) {
                    counterService.increment("messages.sent.remote");
                }
            }
        }

        @Override
        public void messageReceived(String address, boolean publish, boolean local, int handlers) {
            if (publish) {
                counterService.increment("messages.receivedPublished.total");
                if (local) {
                    counterService.increment("messages.receivedPublished.local");
                } else {
                    counterService.increment("messages.receivedPublished.remote");
                }
            } else {
                counterService.increment("messages.receivedSent.total");
                if (local) {
                    counterService.increment("messages.receivedSent.local");
                } else {
                    counterService.increment("messages.receivedSent.remote");
                }
            }
        }

        @Override
        public void messageWritten(String address, int numberOfBytes) {
        }

        @Override
        public void messageRead(String address, int numberOfBytes) {
        }

        @Override
        public void replyFailure(String address, ReplyFailure failure) {
            switch (failure) {
                case TIMEOUT:
                    counterService.increment("reply.failures.timeout");
                    break;
                case NO_HANDLERS:
                    counterService.increment("reply.failures.noHandlers");
                    break;
                case RECIPIENT_FAILURE:
                    counterService.increment("reply.failures.recipientFailure");
                    break;
            }
        }
    }


    private static class PoolMetricsImpl
            extends AbstractPartMetrics<VertxMetricsProperties.PoolMetricsProperties>
            implements PoolMetrics<StopWatch> {

        public PoolMetricsImpl(CounterService counterService, GaugeService gaugeService,
                               VertxMetricsProperties.PoolMetricsProperties properties) {
            super(counterService, gaugeService, properties);
        }

        @Override
        public StopWatch submitted() {
            counterService.increment("tasks.submitted");
            StopWatch stopWatch = new StopWatch();
            stopWatch.setKeepTaskList(false);
            stopWatch.start("submit");
            return stopWatch;
        }

        @Override
        public StopWatch begin(StopWatch stopWatch) {
            stopWatch.stop();
            counterService.increment("tasks.started");
            gaugeService.submit("tasks.waitTimeMs", stopWatch.getLastTaskTimeMillis());
            stopWatch.start("task");
            return stopWatch;
        }

        @Override
        public void rejected(StopWatch stopWatch) {
            stopWatch.stop();
            counterService.increment("tasks.rejected");
            gaugeService.submit("tasks.waitTimeMs", stopWatch.getLastTaskTimeMillis());
        }

        @Override
        public void end(StopWatch stopWatch, boolean succeeded) {
            stopWatch.stop();
            if (succeeded) {
                counterService.increment("tasks.succeeded");
            } else {
                counterService.increment("tasks.failed");
            }
            gaugeService.submit("tasks.durationMs", stopWatch.getLastTaskTimeMillis());
        }
    }


    private static abstract class AbstractNetworkPartMetrics<P extends AbstractPartMetricsProperties>
            extends AbstractPartMetrics<P> implements NetworkMetrics<StopWatch> {

        public AbstractNetworkPartMetrics(CounterService counterService, GaugeService gaugeService,
                                          P properties) {
            super(counterService, gaugeService, properties);
        }

        @Override
        public void bytesRead(StopWatch socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        }

        @Override
        public void bytesWritten(StopWatch socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        }

        @Override
        public void exceptionOccurred(StopWatch socketMetric, SocketAddress remoteAddress, Throwable t) {
        }
    }


    private static abstract class AbstractTcpPartMetrics<P extends AbstractPartMetricsProperties>
            extends AbstractNetworkPartMetrics<P> implements TCPMetrics<StopWatch> {

        public AbstractTcpPartMetrics(CounterService counterService, GaugeService gaugeService, P properties) {
            super(counterService, gaugeService, properties);
        }

        @Override
        public StopWatch connected(SocketAddress remoteAddress, String remoteName) {
            counterService.increment("socket.numConnected");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            return stopWatch;
        }

        @Override
        public void disconnected(StopWatch stopWatch, SocketAddress remoteAddress) {
            stopWatch.stop();
            counterService.decrement("socket.numConnected");
            gaugeService.submit("socket.connectionDurationMs", stopWatch.getTotalTimeMillis());
        }
    }


    private static class HttpServerMetricsImpl
            extends AbstractTcpPartMetrics<VertxMetricsProperties.HttpServerMetricsProperties>
            implements HttpServerMetrics<StopWatch, StopWatch, StopWatch> {

        public HttpServerMetricsImpl(CounterService counterService, GaugeService gaugeService,
                                     VertxMetricsProperties.HttpServerMetricsProperties properties) {
            super(counterService, gaugeService, properties);
        }

        @Override
        public StopWatch requestBegin(StopWatch socketWatch, HttpServerRequest request) {
            counterService.increment("requests.count.total");
            counterService.increment("requests.count." + request.rawMethod());
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("request");
            return stopWatch;
        }

        @Override
        public void requestReset(StopWatch requestWatch) {
            requestWatch.stop();
            counterService.increment("requests.reset");
        }

        @Override
        public StopWatch responsePushed(StopWatch socketWatch, HttpMethod method, String uri,
                                        HttpServerResponse response) {
            counterService.increment("responses.pushed");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            return stopWatch;
        }


        @Override
        public void responseEnd(StopWatch requestWatch, HttpServerResponse response) {
            requestWatch.stop();
            counterService.increment("responses.count.total");
            int statusCode = response.getStatusCode();
            long totalTimeMillis = requestWatch.getTotalTimeMillis();
            gaugeService.submit("responses.totalTime.all", totalTimeMillis);
            if (statusCode > 400) {
                gaugeService.submit("responses.totalTime.error.all", totalTimeMillis);
                counterService.increment("responses.count.error.total");
                if (statusCode > 500) {
                    counterService.increment("responses.count.error.server");
                    gaugeService.submit("responses.totalTime.error.server", totalTimeMillis);
                } else {
                    counterService.increment("responses.count.error.client");
                    gaugeService.submit("responses.totalTime.error.client", totalTimeMillis);
                }
            } else if (statusCode > 300) {
                counterService.increment("responses.count.redirect");
                gaugeService.submit("responses.totalTime.redirect", totalTimeMillis);
            } else if (statusCode > 200) {
                counterService.increment("responses.count.success");
                gaugeService.submit("responses.totalTime.success", totalTimeMillis);
            }
        }


        @Override
        public StopWatch upgrade(StopWatch requestWatch, ServerWebSocket serverWebSocket) {
            requestWatch.stop();
            counterService.increment("requests.upgraded");
            requestWatch.start("websocket");
            return requestWatch;
        }


        @Override
        public StopWatch connected(StopWatch socketMetric, ServerWebSocket serverWebSocket) {
            counterService.increment("websockets.connected");
            StopWatch websocketWatch = new StopWatch();
            websocketWatch.start("websocket");
            return websocketWatch;
        }


        @Override
        public void disconnected(StopWatch websocketWatch) {
            websocketWatch.stop();
            counterService.increment("websockets.disconnected");
            gaugeService.submit("websockets.connectTimeMs", websocketWatch.getLastTaskTimeMillis());
        }
    }


    private static class HttpClientMetricsImpl
            extends AbstractTcpPartMetrics<VertxMetricsProperties.HttpClientMetricsProperties>
            implements HttpClientMetrics<StopWatch, StopWatch, StopWatch, StopWatch, StopWatch> {

        public HttpClientMetricsImpl(CounterService counterService, GaugeService gaugeService,
                                     VertxMetricsProperties.HttpClientMetricsProperties properties) {
            super(counterService, gaugeService, properties);
        }

        @Override
        public StopWatch createEndpoint(String host, int port, int maxPoolSize) {
            counterService.increment("endpoints.active");
            counterService.increment("endpoints.total");
            StopWatch endpointWatch = new StopWatch();
            endpointWatch.start();
            return endpointWatch;
        }

        @Override
        public void closeEndpoint(String host, int port, StopWatch endpointWatch) {
            endpointWatch.stop();
            counterService.decrement("endpoints.active");
        }

        @Override
        public StopWatch enqueueRequest(StopWatch endpointWatch) {
            counterService.increment("requests.queued.active");
            counterService.increment("requests.queued.total");
            StopWatch taskWatch = new StopWatch();
            taskWatch.start();
            return taskWatch;
        }

        @Override
        public void dequeueRequest(StopWatch endpointWatch, StopWatch taskWatch) {
            taskWatch.stop();
            counterService.decrement("requests.queued.active");
            gaugeService.submit("requests.queueTime", taskWatch.getTotalTimeMillis());
        }

        @Override
        public void endpointConnected(StopWatch endpointWatch, StopWatch socketWatch) {
            counterService.increment("endpoints.connected");
        }

        @Override
        public void endpointDisconnected(StopWatch endpointWatch, StopWatch socketWatch) {
            counterService.decrement("endpoints.connected");
        }

        @Override
        public StopWatch requestBegin(StopWatch endpointWatch, StopWatch socketMetric, SocketAddress localAddress,
                                      SocketAddress remoteAddress, HttpClientRequest request) {
            counterService.increment("requests.sent");
            StopWatch requestWatch = new StopWatch();
            requestWatch.start();
            return requestWatch;
        }

        @Override
        public void requestEnd(StopWatch requestWatch) {
            requestWatch.stop();
            gaugeService.submit("requests.sendTime", requestWatch.getLastTaskTimeMillis());
            requestWatch.start("awaitResponse");
        }

        @Override
        public void responseBegin(StopWatch requestWatch, HttpClientResponse response) {
            requestWatch.stop();
            gaugeService.submit("requests.waitTime", requestWatch.getLastTaskTimeMillis());
            requestWatch.start("readResponse");
        }

        @Override
        public StopWatch responsePushed(StopWatch endpointWatch, StopWatch socketWatch, SocketAddress localAddress,
                                        SocketAddress remoteAddress, HttpClientRequest request) {
            return null;
        }

        @Override
        public void requestReset(StopWatch requestWatch) {
            requestWatch.stop();
            counterService.increment("requests.reset");
        }

        @Override
        public void responseEnd(StopWatch requestWatch, HttpClientResponse response) {

        }

        @Override
        public StopWatch connected(StopWatch endpointWatch, StopWatch socketWatch, WebSocket webSocket) {
            counterService.increment("websockets.connected");
            StopWatch websocketWatch = new StopWatch();
            websocketWatch.start("websocket");
            return websocketWatch;
        }

        @Override
        public void disconnected(StopWatch websocketWatch) {
            websocketWatch.stop();
            counterService.increment("websockets.disconnected");
            gaugeService.submit("websockets.connectTimeMs", websocketWatch.getLastTaskTimeMillis());
        }
    }


    private static class NetServerMetricsImpl
            extends AbstractTcpPartMetrics<VertxMetricsProperties.NetServerMetricsProperties>
            implements TCPMetrics<StopWatch> {

        public NetServerMetricsImpl(CounterService counterService, GaugeService gaugeService,
                                    VertxMetricsProperties.NetServerMetricsProperties properties) {
            super(counterService, gaugeService, properties);
        }
    }


    private static class NetClientMetricsImpl
            extends AbstractTcpPartMetrics<VertxMetricsProperties.NetClientMetricsProperties>
            implements TCPMetrics<StopWatch> {

        public NetClientMetricsImpl(CounterService counterService, GaugeService gaugeService,
                                    VertxMetricsProperties.NetClientMetricsProperties properties) {
            super(counterService, gaugeService, properties);
        }
    }


    private static class DatagramSocketMetricsImpl
            extends AbstractPartMetrics<VertxMetricsProperties.DatagramSocketProperties>
            implements DatagramSocketMetrics {

        public DatagramSocketMetricsImpl(CounterService counterService, GaugeService gaugeService,
                                         VertxMetricsProperties.DatagramSocketProperties properties) {
            super(counterService, gaugeService, properties);
        }

        @Override
        public void listening(String localName, SocketAddress localAddress) {
            counterService.increment("datagram.sockets.listening");
        }

        @Override
        public void bytesRead(Void socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        }

        @Override
        public void bytesWritten(Void socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        }

        @Override
        public void exceptionOccurred(Void socketMetric, SocketAddress remoteAddress, Throwable t) {
        }
    }
}
