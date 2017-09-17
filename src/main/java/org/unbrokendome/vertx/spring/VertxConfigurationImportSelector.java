package org.unbrokendome.vertx.spring;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;


public class VertxConfigurationImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {

        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableVertx.class.getName());
        boolean deployVerticles = (boolean) attributes.getOrDefault("deployVerticles", Boolean.TRUE);

        if (deployVerticles) {
            return new String[] { VertxConfiguration.class.getName(), VerticleDeploymentConfiguration.class.getName() };
        } else {
            return new String[] { VertxConfiguration.class.getName() };
        }
    }
}
