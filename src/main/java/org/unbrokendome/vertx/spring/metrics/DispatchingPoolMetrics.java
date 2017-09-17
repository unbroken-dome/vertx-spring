package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.spi.metrics.PoolMetrics;

import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
public class DispatchingPoolMetrics extends AbstractDispatchingMetrics<PoolMetrics>
        implements PoolMetrics<Map<PoolMetrics, ?>> {

    public DispatchingPoolMetrics(List<? extends PoolMetrics> delegates) {
        super(delegates);
    }


    @Override
    public Map<PoolMetrics, ?> submitted() {
        return dispatchWithResult(m -> m.submitted());
    }


    @Override
    public Map<PoolMetrics, ?> begin(Map<PoolMetrics, ?> context) {
        return unmapWithResult(context, (m, c) -> m.begin(c));
    }


    @Override
    public void rejected(Map<PoolMetrics, ?> context) {
        unmap(context, (m, c) -> m.rejected(c));
    }


    @Override
    public void end(Map<PoolMetrics, ?> context, boolean succeeded) {
        unmap(context, (m, c) -> m.end(c, succeeded));
    }
}
