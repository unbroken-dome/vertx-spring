package org.unbrokendome.vertx.spring;


import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.unbrokendome.vertx.spring.events.EventPublishingVertxListener;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class VertxConfiguration {

    private final Logger logger = LoggerFactory.getLogger(VertxConfiguration.class);

    @Bean
    public EventPublishingVertxListener eventPublishingVertxListener() {
        return new EventPublishingVertxListener();
    }

    @Bean
    public SpringVertx vertx(
            ObjectProvider<VertxOptions> optionsProvider,
            ObjectProvider<List<VertxListener>> listenersProvider,
            ObjectProvider<List<VertxConfigurer>> configurersProvider) {

        SpringVertx.Builder builder = SpringVertx.builder();

        VertxOptions options = optionsProvider.getIfAvailable();
        if (options != null) {
            builder.options(options);
        }

        List<VertxListener> listeners = listenersProvider.getIfAvailable();
        if (listeners != null) {
            for (VertxListener listener : listeners) {
                builder.listener(listener);
            }
        }

        List<VertxConfigurer> configurers = configurersProvider.getIfAvailable();
        if (configurers != null) {
            List<VertxConfigurer> sortedConfigurers = new ArrayList<>(configurers);
            AnnotationAwareOrderComparator.sort(sortedConfigurers);

            for (VertxConfigurer configurer : sortedConfigurers) {
                logger.debug("Applying configurer: {}", configurer);
                configurer.configure(builder);
            }
        }

        return builder.build();
    }
}
