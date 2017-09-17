package org.unbrokendome.vertx.spring.metrics;


import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.WebSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;

import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
class DispatchingHttpClientMetrics
        extends AbstractDispatchingTcpMetrics<HttpClientMetrics>
        implements HttpClientMetrics<Map<HttpClientMetrics, ?>, Map<HttpClientMetrics, ?>, Map<HttpClientMetrics, ?>,
                Map<HttpClientMetrics, ?>, Map<HttpClientMetrics, ?>> {

    public DispatchingHttpClientMetrics(List<? extends HttpClientMetrics> delegates) {
        super(delegates);
    }


    @Override
    public Map<HttpClientMetrics, ?> createEndpoint(String host, int port, int maxPoolSize) {
        return dispatchWithResult(m -> m.createEndpoint(host, port, maxPoolSize));
    }


    @Override
    public void closeEndpoint(String host, int port, Map<HttpClientMetrics, ?> endpointMetric) {
        unmap(endpointMetric, (m, c) -> m.closeEndpoint(host, port, c));
    }


    @Override
    public Map<HttpClientMetrics, ?> enqueueRequest(Map<HttpClientMetrics, ?> endpointMetric) {
        return unmapWithResult(endpointMetric, (m, c) -> m.enqueueRequest(c));
    }


    @Override
    public void dequeueRequest(Map<HttpClientMetrics, ?> endpointMetric,
                               Map<HttpClientMetrics, ?> taskMetric) {
        unmap2(endpointMetric, taskMetric, (m, ec, tc) -> m.endpointDisconnected(ec, tc));
    }


    @Override
    public void endpointConnected(Map<HttpClientMetrics, ?> endpointMetric,
                                  Map<HttpClientMetrics, ?> socketMetric) {
        unmap2(endpointMetric, socketMetric, (m, ec, sc) -> m.endpointConnected(ec, sc));
    }


    @Override
    public void endpointDisconnected(Map<HttpClientMetrics, ?> endpointMetric,
                                     Map<HttpClientMetrics, ?> socketMetric) {
        unmap2(endpointMetric, socketMetric, (m, ec, sc) -> m.endpointDisconnected(ec, sc));
    }


    @Override
    public Map<HttpClientMetrics, ?> requestBegin(Map<HttpClientMetrics, ?> endpointMetric,
                                                  Map<HttpClientMetrics, ?> socketMetric,
                                                  SocketAddress localAddress, SocketAddress remoteAddress,
                                                  HttpClientRequest request) {
        return null;
    }


    @Override
    public void requestEnd(Map<HttpClientMetrics, ?> requestMetric) {
        unmap(requestMetric, (m, c) -> m.requestEnd(c));
    }


    @Override
    public void responseBegin(Map<HttpClientMetrics, ?> requestMetric, HttpClientResponse response) {
        unmap(requestMetric, (m, c) -> m.responseBegin(c, response));
    }


    @Override
    public Map<HttpClientMetrics, ?> responsePushed(Map<HttpClientMetrics, ?> endpointMetric,
                                                    Map<HttpClientMetrics, ?> socketMetric,
                                                    SocketAddress localAddress, SocketAddress remoteAddress,
                                                    HttpClientRequest request) {
        return unmap2WithResult(endpointMetric, socketMetric,
                (m, ec, sc) -> m.responsePushed(ec, sc, localAddress, remoteAddress, request));
    }


    @Override
    public void requestReset(Map<HttpClientMetrics, ?> requestMetric) {
        unmap(requestMetric, (m, c) -> m.requestReset(c));
    }


    @Override
    public void responseEnd(Map<HttpClientMetrics, ?> requestMetric, HttpClientResponse response) {
        unmap(requestMetric, (m, c) -> m.responseEnd(c, response));
    }


    @Override
    public Map<HttpClientMetrics, ?> connected(Map<HttpClientMetrics, ?> endpointMetric,
                                               Map<HttpClientMetrics, ?> socketMetric,
                                               WebSocket webSocket) {
        return unmap2WithResult(endpointMetric, socketMetric,
                (m, ec, sc) -> m.connected(ec, sc, webSocket));
    }


    @Override
    public void disconnected(Map<HttpClientMetrics, ?> webSocketMetric) {
        unmap(webSocketMetric, (m, c) -> m.disconnected(c));
    }
}
