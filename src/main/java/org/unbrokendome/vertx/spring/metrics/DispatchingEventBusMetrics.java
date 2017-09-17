package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.spi.metrics.EventBusMetrics;

import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
class DispatchingEventBusMetrics
        extends AbstractDispatchingMetrics<EventBusMetrics>
        implements EventBusMetrics<Map<EventBusMetrics, ?>> {

    public DispatchingEventBusMetrics(List<? extends EventBusMetrics> delegates) {
        super(delegates);
    }


    @Override
    public Map<EventBusMetrics, ?> handlerRegistered(String address, String repliedAddress) {
        return dispatchWithResult(m -> m.handlerRegistered(address, repliedAddress));
    }


    @Override
    public void handlerUnregistered(Map<EventBusMetrics, ?> handler) {
        unmap(handler, (m, h) -> m.handlerUnregistered(h));
    }


    @Override
    public void scheduleMessage(Map<EventBusMetrics, ?> handler, boolean local) {
        unmap(handler, (m, h) -> m.scheduleMessage(h, local));
    }


    @Override
    public void beginHandleMessage(Map<EventBusMetrics, ?> handler, boolean local) {
        unmap(handler, (m, h) -> m.beginHandleMessage(h, local));
    }


    @Override
    public void endHandleMessage(Map<EventBusMetrics, ?> handler, Throwable failure) {
        unmap(handler, (m, h) -> m.endHandleMessage(h, failure));
    }


    @Override
    public void messageSent(String address, boolean publish, boolean local, boolean remote) {
        dispatch(m -> m.messageSent(address, publish, local, remote));
    }


    @Override
    public void messageReceived(String address, boolean publish, boolean local, int handlers) {
        dispatch(m -> m.messageReceived(address, publish, local, handlers));
    }


    @Override
    public void messageWritten(String address, int numberOfBytes) {
        dispatch(m -> m.messageWritten(address, numberOfBytes));
    }


    @Override
    public void messageRead(String address, int numberOfBytes) {
        dispatch(m -> m.messageRead(address, numberOfBytes));
    }


    @Override
    public void replyFailure(String address, ReplyFailure failure) {
        dispatch(m -> m.replyFailure(address, failure));
    }
}
