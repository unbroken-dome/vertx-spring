package org.unbrokendome.vertx.spring;

import io.vertx.core.Context;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.VertxMetricsFactory;
import io.vertx.core.spi.metrics.VertxMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unbrokendome.vertx.spring.metrics.VertxMetricsAdapter;

import java.util.List;
import java.util.function.Consumer;


public class VertxListenerAwareMetricsFactory implements VertxMetricsFactory {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<? extends VertxListener> listeners;

    public VertxListenerAwareMetricsFactory(List<? extends VertxListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public VertxMetrics metrics(Vertx vertx, VertxOptions options) {
        dispatch("vertxStarted", listener -> listener.vertxStarted(vertx, options));
        return new ListenerAwareVertxMetrics(vertx);
    }

    private void dispatch(String eventName, Consumer<VertxListener> listenerAction) {
        for (VertxListener listener : listeners) {
            try {
                listenerAction.accept(listener);
            } catch (Throwable t) {
                logger.error("Error in VertxListener {} while handling {} event", listener, eventName, t);
            }
        }
    }

    private class ListenerAwareVertxMetrics implements VertxMetricsAdapter {

        private final Vertx vertx;

        private ListenerAwareVertxMetrics(Vertx vertx) {
            this.vertx = vertx;
        }

        @Override
        public void verticleDeployed(Verticle verticle) {
            Context context = Vertx.currentContext();
            dispatch("verticleDeployed", listener -> listener.verticleDeployed(verticle, context));
        }

        @Override
        public void verticleUndeployed(Verticle verticle) {
            Context context = Vertx.currentContext();
            dispatch("verticleUndeployed", listener -> listener.verticleUndeployed(verticle, context));
        }

        @Override
        public void close() {
            dispatch("vertxStopped", listener -> listener.vertxStopped(vertx));
        }
    }
}
