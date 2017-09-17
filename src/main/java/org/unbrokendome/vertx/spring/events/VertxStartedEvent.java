package org.unbrokendome.vertx.spring.events;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;


public final class VertxStartedEvent extends AbstractVertxEvent {

    private final VertxOptions options;

    public VertxStartedEvent(Vertx vertx, VertxOptions options) {
        super(vertx);
        this.options = options;
    }

    public VertxOptions getOptions() {
        return options;
    }
}
