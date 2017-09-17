package org.unbrokendome.vertx.spring.events;

import io.vertx.core.Context;
import io.vertx.core.Verticle;


public final class VerticleUndeployedEvent extends AbstractVerticleEvent {

    public VerticleUndeployedEvent(Verticle verticle, Context context) {
        super(verticle, context);
    }
}
