package org.unbrokendome.vertx.spring.events;

import io.vertx.core.Vertx;
import org.springframework.context.ApplicationEvent;


public abstract class AbstractVertxEvent extends ApplicationEvent {

    protected AbstractVertxEvent(Vertx vertx) {
        super(vertx);
    }

    public final Vertx getVertx() {
        return (Vertx) getSource();
    }
}
