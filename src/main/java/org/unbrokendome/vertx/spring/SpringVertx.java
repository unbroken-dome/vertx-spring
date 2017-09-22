package org.unbrokendome.vertx.spring;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.core.spi.VertxFactory;
import io.vertx.core.spi.VertxMetricsFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;
import org.unbrokendome.vertx.spring.metrics.DispatchingVertxMetricsFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@SuppressWarnings("unused")
public class SpringVertx implements SmartLifecycle, BeanFactoryAware {

    public static final String DEFAULT_VERTICLE_FACTORY_PREFIX = "spring";

    private final Logger logger = LoggerFactory.getLogger(SpringVertx.class);

    private final VertxFactory factory;
    private final VertxOptions options;
    private final List<VerticleRegistration> verticleRegistrations;
    private final String verticleFactoryPrefix;
    private final int startupPhase;
    private final boolean autoStartup;

    private final Object startMonitor = new Object();
    private BeanFactory beanFactory;

    private volatile Vertx vertx;


    public SpringVertx(VertxFactory factory, VertxOptions options,
                       Collection<VerticleRegistration> verticleRegistrations,
                       List<VertxListener> listeners, String verticleFactoryPrefix,
                       int startupPhase, boolean autoStartup) {
        this.factory = factory;
        this.options = new VertxOptions(options);
        this.verticleRegistrations = new ArrayList<>(verticleRegistrations);
        this.verticleFactoryPrefix = verticleFactoryPrefix;
        this.startupPhase = startupPhase;
        this.autoStartup = autoStartup;
    }


    @Override
    public final boolean isAutoStartup() {
        return autoStartup;
    }


    @Override
    public final int getPhase() {
        return startupPhase;
    }


    @Override
    public final boolean isRunning() {
        return vertx != null;
    }


    @Override
    public synchronized void start() {
        if (vertx == null) {
            synchronized(startMonitor) {
                if (vertx == null) {
                    CompletableFuture<Vertx> vertxStartedFuture = new CompletableFuture<>();
                    CompletableFuture<Vertx> vertxReadyFuture = vertxStartedFuture;

                    if (options.isClustered()) {
                        factory.clusteredVertx(options, ar -> {
                            if (ar.succeeded()) {
                                vertxStartedFuture.complete(ar.result());
                            } else {
                                vertxStartedFuture.completeExceptionally(ar.cause());
                            }
                        });
                    } else {
                        Vertx vertx = factory.vertx(options);
                        vertxStartedFuture.complete(vertx);
                    }

                    if (verticleFactoryPrefix != null) {
                        vertxReadyFuture = vertxReadyFuture.thenApply(vertx -> {
                            SpringVerticleFactory verticleFactory = new SpringVerticleFactory(verticleFactoryPrefix, beanFactory);
                            logger.debug("Registering VerticleFactory: {}", verticleFactory);
                            vertx.registerVerticleFactory(verticleFactory);
                            return vertx;
                        });
                    }

                    if (!verticleRegistrations.isEmpty()) {

                        // Group all verticle registrations by order. Verticles with the same order will be
                        // deployed simultaneously.
                        SortedMap<Integer, List<VerticleRegistration>> registrationGroups = verticleRegistrations.stream()
                                .collect(Collectors.groupingBy(SpringVertx::getVerticleOrder, TreeMap::new, Collectors.toList()));

                        for (Map.Entry<Integer, List<VerticleRegistration>> entry : registrationGroups.entrySet()) {
                            int order = entry.getKey();
                            List<VerticleRegistration> registrations = entry.getValue();
                            vertxReadyFuture = vertxReadyFuture.thenCompose(vertx ->
                                    deployVerticleGroup(vertx, order, registrations));
                        }
                    } else {
                        logger.debug("No verticle registrations set; no verticles will be deployed after startup.");
                    }

                    try {
                        this.vertx = vertxReadyFuture.join();
                        logger.info("Vert.x startup complete");

                    } catch (CompletionException ex) {
                        if (ex.getCause() instanceof RuntimeException) {
                            throw (RuntimeException) ex.getCause();
                        } else {
                            throw ex;
                        }
                    }
                }
            }
        }
    }


    private static int getVerticleOrder(VerticleRegistration registration) {
        if (registration instanceof Ordered) {
            return ((Ordered) registration).getOrder();
        } else {
            return 0;
        }
    }


    private CompletableFuture<Vertx> deployVerticleGroup(Vertx vertx, int order,
                                                         Collection<VerticleRegistration> registrations) {
        logger.info("Deploying verticles with order {}", order);

        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] futures = new CompletableFuture[registrations.size()];

        int i = 0;
        for (VerticleRegistration registration : registrations) {
            futures[i++] = deployVerticle(vertx, registration);
        }

        return CompletableFuture.allOf(futures)
                .thenApply(any -> vertx);
    }


    private CompletableFuture<Void> deployVerticle(Vertx vertx, VerticleRegistration registration) {

        Verticle verticle = registration.getVerticle();
        String verticleName = registration.getVerticleName();

        if (verticle == null && verticleName == null) {
            logger.error("Invalid VerticleRegistration {}: Either verticle or verticleName must be given", registration);
            return CompletableFuture.completedFuture(null);
        }

        DeploymentOptions deploymentOptions = registration.getDeploymentOptions();
        if (deploymentOptions == null) {
            deploymentOptions = new DeploymentOptions();
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        Handler<AsyncResult<String>> resultHandler = ar -> {
            if (ar.succeeded()) {
                logger.info("Successfully deployed verticle \"{}\" with deployment ID \"{}\"", registration, ar.result());
                future.complete(null);
            } else {
                logger.error("Failed to deploy verticle \"{}\"", registration, ar.cause());
                future.completeExceptionally(ar.cause());
            }
        };

        if (verticle != null) {
            vertx.deployVerticle(verticle, deploymentOptions, resultHandler);
        } else {
            vertx.deployVerticle(verticleFactoryPrefix + ":" + verticleName, deploymentOptions, resultHandler);
        }
        return future;
    }


    @Override
    public void stop(Runnable callback) {
        Vertx vertx = this.vertx;
        if (vertx != null) {
            logger.debug("Shutting down Vert.x instance");
            vertx.close(ar -> {
                if (ar.succeeded()) {
                    logger.info("Vert.x instance shut down successfully");
                } else {
                    logger.error("Failed to shut down Vert.x instance", ar.cause());
                }
                this.vertx = null;
                if (callback != null) {
                    callback.run();
                }
            });
        }
    }


    @Override
    public void stop() {
        if (vertx != null) {
            CountDownLatch latch = new CountDownLatch(1);
            stop(latch::countDown);
            try {
                latch.await();
            } catch (InterruptedException ex) {
                logger.error("Interrupted while waiting for Vert.x instance to stop", ex);
            }
        }
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    public static Builder builder() {
        return new Builder();
    }


    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {
        private VertxFactory factory = Vertx.factory;
        private VertxOptions options = null;
        private List<VerticleRegistration> verticleRegistrations = new ArrayList<>();
        private List<VertxMetricsFactory> metricsFactories = new ArrayList<>();
        private List<VertxListener> listeners = new ArrayList<>();
        private String verticleFactoryPrefix = DEFAULT_VERTICLE_FACTORY_PREFIX;
        private int startupPhase = 0;
        private boolean autoStartup = true;


        public Builder factory(VertxFactory vertxFactory) {
            this.factory = vertxFactory;
            return this;
        }


        public Builder options(Consumer<VertxOptions> optionsSpec) {
            if (this.options == null) {
                this.options = new VertxOptions();
            }
            optionsSpec.accept(options);
            return this;
        }


        public Builder options(VertxOptions options) {
            this.options = new VertxOptions(options);
            return this;
        }


        public Builder clusterManager(ClusterManager clusterManager) {
            return options(opt -> {
                if (opt.getClusterManager() == null) {
                    opt.setClusterManager(clusterManager);
                }
            });
        }


        public Builder verticleFactoryPrefix(String prefix) {
            this.verticleFactoryPrefix = prefix;
            return this;
        }


        public Builder verticle(Verticle verticle) {
            return verticle(new VerticleRegistrationBean(verticle));
        }


        public Builder verticle(Verticle verticle, DeploymentOptions options) {
            return verticle(new VerticleRegistrationBean(verticle, options));
        }


        public Builder verticle(VerticleRegistration verticleRegistration) {
            this.verticleRegistrations.add(verticleRegistration);
            return this;
        }


        public Builder verticles(Iterable<? extends Verticle> verticles) {
            for (Verticle verticle : verticles) {
                this.verticle(verticle);
            }
            return this;
        }


        public Builder verticles(Verticle... verticles) {
            return verticles(Arrays.asList(verticles));
        }


        public Builder verticleRegistrations(Iterable<? extends VerticleRegistration> verticleRegistrations) {
            for (VerticleRegistration registration : verticleRegistrations) {
                this.verticle(registration);
            }
            return this;
        }


        public Builder verticleRegistrations(VerticleRegistration... verticleRegistrations) {
            return verticleRegistrations(Arrays.asList(verticleRegistrations));
        }


        public Builder listener(VertxListener listener) {
            this.listeners.add(listener);
            return this;
        }


        public Builder metricsFactory(VertxMetricsFactory metricsFactory) {
            this.metricsFactories.add(metricsFactory);
            return this;
        }


        public Builder startupPhase(int startupPhase) {
            this.startupPhase = startupPhase;
            return this;
        }


        public Builder autoStartup(boolean autoStartup) {
            this.autoStartup = autoStartup;
            return this;
        }


        private VertxOptions getOrCreateOptions() {
            if (options == null) {
                options = new VertxOptions();
            }
            return options;
        }


        public SpringVertx build() {

            if (!listeners.isEmpty()) {
                metricsFactories.add(new VertxListenerAwareMetricsFactory(listeners));
            }

            if (!metricsFactories.isEmpty()) {
                VertxMetricsFactory singleMetricsFactory;
                if (metricsFactories.size() > 1) {
                    singleMetricsFactory = new DispatchingVertxMetricsFactory(metricsFactories);
                } else {
                    singleMetricsFactory = metricsFactories.get(0);
                }
                MetricsOptions metricsOptions = getOrCreateOptions().getMetricsOptions();
                metricsOptions.setEnabled(true);
                metricsOptions.setFactory(singleMetricsFactory);
            }

            return new SpringVertx(
                    factory,
                    getOrCreateOptions(),
                    verticleRegistrations,
                    listeners,
                    verticleFactoryPrefix,
                    startupPhase,
                    autoStartup);
        }
    }
}
