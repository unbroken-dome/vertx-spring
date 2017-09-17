package org.unbrokendome.vertx.spring;

import io.vertx.core.Context;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;


public interface VertxListener {

    default void vertxStarted(Vertx vertx, VertxOptions options) {
    }

    default void vertxStopped(Vertx vertx) {
    }

    default void verticleDeployed(Verticle verticle, Context context) {
    }

    default void verticleUndeployed(Verticle verticle, Context context) {
    }
}
