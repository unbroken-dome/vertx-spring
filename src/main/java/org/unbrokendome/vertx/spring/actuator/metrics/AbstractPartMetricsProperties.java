package org.unbrokendome.vertx.spring.actuator.metrics;

abstract class AbstractPartMetricsProperties {

    private String prefix;
    private boolean enabled = true;

    public AbstractPartMetricsProperties(String defaultPrefix) {
        this.prefix = defaultPrefix;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
