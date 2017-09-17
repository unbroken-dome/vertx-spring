package org.unbrokendome.vertx.spring.boot;

import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.dns.AddressResolverOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.unbrokendome.vertx.spring.SpringVertx;
import org.unbrokendome.vertx.spring.VertxConfigurer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ConfigurationProperties(prefix = "vertx")
@SuppressWarnings({ "unused", "DefaultAnnotationParam" })
@Order(Ordered.LOWEST_PRECEDENCE)
public class VertxProperties implements VertxConfigurer {

    private boolean autoDeployVerticles = true;

    private VertxOptions vertxOptions = new VertxOptions();

    @NestedConfigurationProperty
    private final Ha ha = new Ha(vertxOptions);

    @NestedConfigurationProperty
    private AddressResolver addressResolver;

    @NestedConfigurationProperty
    private EventBusOptions eventBus;


    public static class Ha {

        private final VertxOptions vertxOptions;

        private Ha(VertxOptions vertxOptions) {
            this.vertxOptions = vertxOptions;
        }

        public boolean isEnabled() {
            return vertxOptions.isHAEnabled();
        }

        public void setEnabled(boolean enabled) {
            vertxOptions.setHAEnabled(enabled);
        }

        public int getQuorumSize() {
            return vertxOptions.getQuorumSize();
        }

        public void setQuorumSize(int quorumSize) {
            vertxOptions.setQuorumSize(quorumSize);
        }

        public String getGroup() {
            return vertxOptions.getHAGroup();
        }

        public void setGroup(String group) {
            vertxOptions.setHAGroup(group);
        }
    }


    public static class AddressResolver {

        private final AddressResolverOptions options = new AddressResolverOptions();

        @NestedConfigurationProperty
        private final Cache cache = new Cache(options);

        private Map<String, List<String>> hosts;
        private String hostsValue;
        private Resource hostsResource;

        public static class Cache {

            private final AddressResolverOptions options;

            private Cache(AddressResolverOptions options) {
                this.options = options;
            }

            public int getMinTimeToLive() {
                return options.getCacheMinTimeToLive();
            }

            public void setMinTimeToLive(int minTimeToLive) {
                options.setCacheMinTimeToLive(minTimeToLive);
            }

            public int getMaxTimeToLive() {
                return options.getCacheMaxTimeToLive();
            }

            public void setMaxTimeToLive(int maxTimeToLive) {
                options.setCacheMaxTimeToLive(maxTimeToLive);
            }

            public int getNegativeTimeToLive() {
                return options.getCacheNegativeTimeToLive();
            }

            public void setNegativeTimeToLive(int negativeTimeToLive) {
                options.setCacheNegativeTimeToLive(negativeTimeToLive);
            }
        }

        public Resource getHostsResource() {
            return hostsResource;
        }

        public void setHostsResource(Resource hostsResource) {
            this.hostsResource = hostsResource;
        }

        public String getHostsValue() {
            return hostsValue;
        }

        public void setHostsValue(String hostsValue) {
            this.hostsValue = hostsValue;
        }

        public Map<String, List<String>> getHosts() {
            return hosts;
        }

        public void setHosts(Map<String, List<String>> hosts) {
            this.hosts = hosts;
        }

        public List<String> getServers() {
            return options.getServers();
        }

        public void setServers(List<String> servers) {
            options.setServers(servers);
        }

        public boolean isOptResourceEnabled() {
            return options.isOptResourceEnabled();
        }

        public void setOptResourceEnabled(boolean optResourceEnabled) {
            options.setOptResourceEnabled(optResourceEnabled);
        }

        public long getQueryTimeout() {
            return options.getQueryTimeout();
        }

        public void setQueryTimeout(long queryTimeout) {
            options.setQueryTimeout(queryTimeout);
        }

        public int getMaxQueries() {
            return options.getMaxQueries();
        }

        public void setMaxQueries(int maxQueries) {
            options.setMaxQueries(maxQueries);
        }

        public boolean getRdFlag() {
            return options.getRdFlag();
        }

        public void setRdFlag(boolean rdFlag) {
            options.setRdFlag(rdFlag);
        }

        public List<String> getSearchDomains() {
            return options.getSearchDomains();
        }

        public void setSearchDomains(List<String> searchDomains) {
            options.setSearchDomains(searchDomains);
        }

        public int getNdots() {
            return options.getNdots();
        }

        public void setNdots(int ndots) {
            options.setNdots(ndots);
        }

        public boolean isRotateServers() {
            return options.isRotateServers();
        }

        public void setRotateServers(boolean rotateServers) {
            options.setRotateServers(rotateServers);
        }

        AddressResolverOptions toAddressResolverOptions() {
            AddressResolverOptions options = new AddressResolverOptions(this.options);
            if (hosts != null) {
                String value = hosts.entrySet().stream()
                        .map(entry -> entry.getKey() + StringUtils.collectionToDelimitedString(entry.getValue(), " "))
                        .collect(Collectors.joining("\n"));
                options.setHostsValue(Buffer.buffer(value));
            } else if (hostsValue != null) {
                options.setHostsValue(Buffer.buffer(hostsValue));
            } else if (hostsResource != null) {
                if (hostsResource instanceof FileSystemResource) {
                    options.setHostsPath(((FileSystemResource) hostsResource).getPath());
                } else {
                    try {
                        try (InputStream input = hostsResource.getInputStream()) {

                            String value = new BufferedReader(new InputStreamReader(input)).lines()
                                    .collect(Collectors.joining("\n"));
                            options.setHostsValue(Buffer.buffer(value));
                        }
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }
            }

            return options;
        }
    }

    public boolean isClustered() {
        return vertxOptions.isClustered();
    }

    public void setClustered(boolean clustered) {
        vertxOptions.setClustered(true);
    }

    public boolean isAutoDeployVerticles() {
        return autoDeployVerticles;
    }

    public void setAutoDeployVerticles(boolean autoDeployVerticles) {
        this.autoDeployVerticles = autoDeployVerticles;
    }

    public Ha getHa() {
        return ha;
    }

    public int getEventLoopPoolSize() {
        return vertxOptions.getEventLoopPoolSize();
    }

    public void setEventLoopPoolSize(int eventLoopPoolSize) {
        vertxOptions.setEventLoopPoolSize(eventLoopPoolSize);
    }

    public int getWorkerPoolSize() {
        return vertxOptions.getWorkerPoolSize();
    }

    public void setWorkerPoolSize(int workerPoolSize) {
        vertxOptions.setWorkerPoolSize(workerPoolSize);
    }

    public long getBlockedThreadCheckInterval() {
        return vertxOptions.getBlockedThreadCheckInterval();
    }

    public void setBlockedThreadCheckInterval(long blockedThreadCheckInterval) {
        vertxOptions.setBlockedThreadCheckInterval(blockedThreadCheckInterval);
    }

    public long getMaxEventLoopExecuteTime() {
        return vertxOptions.getMaxEventLoopExecuteTime();
    }

    public void setMaxEventLoopExecuteTime(long maxEventLoopExecuteTime) {
        vertxOptions.setMaxEventLoopExecuteTime(maxEventLoopExecuteTime);
    }

    public long getMaxWorkerExecuteTime() {
        return vertxOptions.getMaxWorkerExecuteTime();
    }

    public void setMaxWorkerExecuteTime(long maxWorkerExecuteTime) {
        vertxOptions.setMaxWorkerExecuteTime(maxWorkerExecuteTime);
    }

    public int getInternalBlockingPoolSize() {
        return vertxOptions.getInternalBlockingPoolSize();
    }


    public void setInternalBlockingPoolSize(int internalBlockingPoolSize) {
        vertxOptions.setInternalBlockingPoolSize(internalBlockingPoolSize);
    }


    public MetricsOptions getMetricsOptions() {
        return vertxOptions.getMetricsOptions();
    }


    public void setMetricsOptions(MetricsOptions metrics) {
        vertxOptions.setMetricsOptions(metrics);
    }


    public long getWarningExceptionTime() {
        return vertxOptions.getWarningExceptionTime();
    }


    public void setWarningExceptionTime(long warningExceptionTime) {
        vertxOptions.setWarningExceptionTime(warningExceptionTime);
    }


    public EventBusOptions getEventBus() {
        return eventBus;
    }


    public void setEventBus(EventBusOptions eventBus) {
        this.eventBus = eventBus;
    }

    public AddressResolver getAddressResolver() {
        return addressResolver;
    }

    public void setAddressResolver(AddressResolver addressResolver) {
        this.addressResolver = addressResolver;
    }

    public boolean isFileResolverCachingEnabled() {
        return vertxOptions.isFileResolverCachingEnabled();
    }


    public void setFileResolverCachingEnabled(boolean fileResolverCachingEnabled) {
        vertxOptions.setFileResolverCachingEnabled(fileResolverCachingEnabled);
    }


    public VertxOptions toVertxOptions() {
        VertxOptions newOptions = new VertxOptions(this.vertxOptions);

        if (addressResolver != null) {
            newOptions.setAddressResolverOptions(addressResolver.toAddressResolverOptions());
        }
        if (eventBus != null) {
            newOptions.setEventBusOptions(eventBus);
        }
        return newOptions;
    }


    @Override
    public void configure(SpringVertx.Builder builder) {
        builder.options(toVertxOptions());
    }
}
