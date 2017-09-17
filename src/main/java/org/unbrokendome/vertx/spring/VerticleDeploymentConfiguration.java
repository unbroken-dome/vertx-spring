package org.unbrokendome.vertx.spring;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class VerticleDeploymentConfiguration implements VertxConfigurer {

    private final ObjectProvider<List<VerticleRegistration>> verticleRegistrationsProvider;

    public VerticleDeploymentConfiguration(ObjectProvider<List<VerticleRegistration>> verticleRegistrationsProvider) {
        this.verticleRegistrationsProvider = verticleRegistrationsProvider;
    }

    @Bean
    public static VerticleBeanPostProcessor verticleBeanPostProcessor() {
        return new VerticleBeanPostProcessor();
    }

    @Override
    public void configure(SpringVertx.Builder builder) {
        List<VerticleRegistration> verticleRegistrations = verticleRegistrationsProvider.getIfAvailable();
        if (verticleRegistrations != null) {
            builder.verticleRegistrations(verticleRegistrations);
        }
    }
}
