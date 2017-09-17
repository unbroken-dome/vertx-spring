package org.unbrokendome.vertx.spring.events;

import io.vertx.core.Context;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.unbrokendome.vertx.spring.VertxListener;

import java.util.function.Supplier;


public final class EventPublishingVertxListener implements VertxListener, ApplicationEventPublisherAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void vertxStarted(Vertx vertx, VertxOptions options) {
        publish(() -> new VertxStartedEvent(vertx, options));
    }

    @Override
    public void vertxStopped(Vertx vertx) {
        publish(() -> new VertxStoppedEvent(vertx));
    }

    @Override
    public void verticleDeployed(Verticle verticle, Context context) {
        publish(() -> new VerticleDeployedEvent(verticle, context));
    }

    @Override
    public void verticleUndeployed(Verticle verticle, Context context) {
        publish(() -> new VerticleUndeployedEvent(verticle, context));
    }

    private <T extends ApplicationEvent> void publish(Supplier<T> eventSupplier) {
        if (applicationEventPublisher != null) {
            T event = eventSupplier.get();
            logger.debug("Publishing {} with source {}", event.getClass().getSimpleName(), event.getSource());
            applicationEventPublisher.publishEvent(event);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
