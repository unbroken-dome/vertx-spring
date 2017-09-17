package org.unbrokendome.vertx.spring;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import org.springframework.core.Ordered;


public interface DeployableVerticle extends Verticle, Ordered {

    DeploymentOptions getDeploymentOptions();

    default int getOrder() {
        return 0;
    }
}
