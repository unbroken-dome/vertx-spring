package org.unbrokendome.vertx.spring;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import org.springframework.core.Ordered;


public interface VerticleRegistration {

    default Verticle getVerticle() {
        return null;
    }

    default String getVerticleName() {
        return null;
    }

    default DeploymentOptions getDeploymentOptions() {
        return null;
    }
}
