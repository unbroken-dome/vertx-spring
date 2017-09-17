package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.spi.metrics.HttpServerMetrics;

import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
class DispatchingHttpServerMetrics extends AbstractDispatchingTcpMetrics<HttpServerMetrics>
        implements HttpServerMetrics<Map<HttpServerMetrics, ?>, Map<HttpServerMetrics, ?>, Map<HttpServerMetrics, ?>> {

    public DispatchingHttpServerMetrics(List<? extends HttpServerMetrics> delegates) {
        super(delegates);
    }


    @Override
    public Map<HttpServerMetrics, ?> requestBegin(Map<HttpServerMetrics, ?> socketMetric,
                                                  HttpServerRequest request) {
        return unmapWithResult(socketMetric, (m, c) -> m.requestBegin(c, request));
    }


    @Override
    public void requestReset(Map<HttpServerMetrics, ?> requestMetric) {
        unmap(requestMetric, (m, c) -> m.requestReset(c));
    }


    @Override
    public Map<HttpServerMetrics, ?> responsePushed(Map<HttpServerMetrics, ?> socketMetric,
                                                    HttpMethod method, String uri, HttpServerResponse response) {
        return unmapWithResult(socketMetric,
                (m, c) -> m.responsePushed(c, method, uri, response));
    }


    @Override
    public void responseEnd(Map<HttpServerMetrics, ?> requestMetric, HttpServerResponse response) {
        unmap(requestMetric, (m, c) -> m.responseEnd(c, response));
    }


    @Override
    public Map<HttpServerMetrics, ?> upgrade(Map<HttpServerMetrics, ?> requestMetric, ServerWebSocket serverWebSocket) {
        return unmapWithResult(requestMetric, (m, c) -> m.upgrade(c, serverWebSocket));
    }


    @Override
    public Map<HttpServerMetrics, ?> connected(Map<HttpServerMetrics, ?> socketMetric, ServerWebSocket serverWebSocket) {
        return unmapWithResult(socketMetric, (m, c) -> m.connected(c, serverWebSocket));
    }


    @Override
    public void disconnected(Map<HttpServerMetrics, ?> serverWebSocketMetric) {
        unmap(serverWebSocketMetric, (m, c) -> m.disconnected(c));
    }
}
