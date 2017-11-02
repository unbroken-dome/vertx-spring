package org.unbrokendome.vertx.spring;


import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
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
            ObjectProvider<ClusterManager> clusterManagerProvider,
            ObjectProvider<List<VertxListener>> listenersProvider,
            ObjectProvider<List<VertxConfigurer>> configurersProvider) {

        SpringVertx.Builder builder = SpringVertx.builder();

        List<VertxConfigurer> configurers = new ArrayList<>();

        ClusterManager clusterManager = clusterManagerProvider.getIfAvailable();
        if (clusterManager != null) {
            configurers.add(new ClusterManagerConfigurer(clusterManager));
        }

        List<VertxListener> listeners = listenersProvider.getIfAvailable();
        if (listeners != null) {
            for (VertxListener listener : listeners) {
                builder.listener(listener);
            }
        }

        List<VertxConfigurer> injectedConfigurers = configurersProvider.getIfAvailable();
        if (injectedConfigurers != null) {
            configurers.addAll(injectedConfigurers);
        }

        if (!configurers.isEmpty()) {
            List<VertxConfigurer> sortedConfigurers = new ArrayList<>(configurers);
            AnnotationAwareOrderComparator.sort(sortedConfigurers);
            for (VertxConfigurer configurer : sortedConfigurers) {
                logger.debug("Applying configurer: {}", configurer);
                configurer.configure(builder);
            }
        }

        // If we have a VertxOptions bean, it will replace all the options possibly gathered by configurers,
        // so make sure to call it last
        VertxOptions options = optionsProvider.getIfAvailable();
        if (options != null) {
            builder.options(options);
        }

        return builder.build();
    }


    private static class ClusterManagerConfigurer implements VertxConfigurer, Ordered {

        private final ClusterManager clusterManager;

        public ClusterManagerConfigurer(ClusterManager clusterManager) {
            this.clusterManager = clusterManager;
        }

        @Override
        public int getOrder() {
            return HIGHEST_PRECEDENCE + 1;
        }

        @Override
        public void configure(SpringVertx.Builder builder) {
            builder.clusterManager(clusterManager);
        }
    }
}
