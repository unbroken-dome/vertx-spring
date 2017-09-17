package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.DatagramSocketMetrics;

import java.util.List;


class DispatchingDatagramSocketMetrics
        extends AbstractDispatchingMetrics<DatagramSocketMetrics>
        implements DatagramSocketMetrics {

    public DispatchingDatagramSocketMetrics(List<? extends DatagramSocketMetrics> delegates) {
        super(delegates);
    }


    @Override
    public void listening(String localName, SocketAddress localAddress) {
        dispatch(m -> m.listening(localName, localAddress));
    }


    @Override
    public void bytesRead(Void socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        dispatch(m -> m.bytesRead(socketMetric, remoteAddress, numberOfBytes));
    }


    @Override
    public void bytesWritten(Void socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        dispatch(m -> m.bytesWritten(socketMetric, remoteAddress, numberOfBytes));
    }


    @Override
    public void exceptionOccurred(Void socketMetric, SocketAddress remoteAddress, Throwable t) {
        dispatch(m -> m.exceptionOccurred(socketMetric, remoteAddress, t));
    }
}
