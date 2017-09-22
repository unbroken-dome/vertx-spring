package org.unbrokendome.vertx.spring.boot.clustermanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


@ConfigurationProperties("vertx.cluster-manager.zookeeper")
@SuppressWarnings("unused")
public class ZookeeperClusterManagerProperties {

    private Resource config;
    private String nodeId;
    private List<String> hosts;
    private int sessionTimeout = 20000;
    private int connectTimeout = 3000;
    private String rootPath = "io.vertx";

    @NestedConfigurationProperty
    private final Retry retry = new Retry();



    public Resource getConfig() {
        return config;
    }


    public void setConfig(Resource config) {
        this.config = config;
    }


    public String getNodeId() {
        return nodeId;
    }


    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }


    public List<String> getHosts() {
        return hosts;
    }


    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }


    public int getSessionTimeout() {
        return sessionTimeout;
    }


    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }


    public int getConnectTimeout() {
        return connectTimeout;
    }


    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }


    public String getRootPath() {
        return rootPath;
    }


    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }


    public Retry getRetry() {
        return retry;
    }


    private static class Retry {
        private int initialSleepTime = 100;
        private int intervalTimes = 10000;
        private int maxTimes = 5;


        public int getInitialSleepTime() {
            return initialSleepTime;
        }


        public void setInitialSleepTime(int initialSleepTime) {
            this.initialSleepTime = initialSleepTime;
        }


        public int getIntervalTimes() {
            return intervalTimes;
        }


        public void setIntervalTimes(int intervalTimes) {
            this.intervalTimes = intervalTimes;
        }


        public int getMaxTimes() {
            return maxTimes;
        }


        public void setMaxTimes(int maxTimes) {
            this.maxTimes = maxTimes;
        }


        JsonObject toJsonObject() {
            return new JsonObject()
                    .put("initialSleepTime", initialSleepTime)
                    .put("intervalTimes", intervalTimes)
                    .put("maxTimes", maxTimes);
        }
    }


    public JsonObject getConfigAsJsonObject() throws IOException {
        if (this.config != null) {
            return loadJsonFromResource(config);
        } else {
            return this.toJsonObject();
        }
    }


    private JsonObject toJsonObject() {
        return new JsonObject()
                .put("zookeeperHosts", StringUtils.collectionToCommaDelimitedString(hosts))
                .put("sessionTimeout", sessionTimeout)
                .put("connectTimeout", connectTimeout)
                .put("rootPath", rootPath)
                .put("retry", retry.toJsonObject());
    }


    @SuppressWarnings("unchecked")
    private JsonObject loadJsonFromResource(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            Map<String, Object> map = new ObjectMapper().readValue(inputStream, Map.class);
            return new JsonObject(map);
        }
    }
}
