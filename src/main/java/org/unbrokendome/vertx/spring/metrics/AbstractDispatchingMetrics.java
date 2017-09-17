package org.unbrokendome.vertx.spring.metrics;

import io.vertx.core.spi.metrics.EventBusMetrics;
import io.vertx.core.spi.metrics.Metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


@SuppressWarnings("unchecked")
abstract class AbstractDispatchingMetrics<M extends Metrics> implements Metrics {

    private final List<? extends M> delegates;

    protected AbstractDispatchingMetrics(List<? extends M> delegates) {
        this.delegates = delegates;
    }


    protected final List<? extends M> getDelegates() {
        return delegates;
    }


    protected final void dispatch(Consumer<? super M> action) {
        delegates.forEach(action);
    }


    protected final Map<M, ?> dispatchWithResult(Function<M, ?> func) {
        Map<M, Object> resultMap = new HashMap<>(delegates.size());
        for (M delegate : delegates) {
            Object result = func.apply(delegate);
            resultMap.put(delegate, result);
        }
        return resultMap;
    }


    protected final <N extends Metrics> N createSubMetrics(
            Function<M, ? extends N> supplier,
            Function<List<? extends N>, ? extends N> dispatchingMetricsSupplier) {
        List<N> allSubMetrics = new ArrayList<>(delegates.size());
        for (M delegate : delegates) {
            N subMetrics = supplier.apply(delegate);
            if (subMetrics != null) {
                allSubMetrics.add(subMetrics);
            }
        }
        if (allSubMetrics.isEmpty()) {
            return null;
        } else if (allSubMetrics.size() == 1) {
            return allSubMetrics.get(0);
        } else {
            return dispatchingMetricsSupplier.apply(allSubMetrics);
        }
    }


    @SuppressWarnings("unchecked")
    protected final void unmap(Map<M, ?> context, BiConsumer<M, ?> action) {
        for (M delegate : delegates) {
            Object delegateContext = context.get(delegate);
            if (delegateContext != null || context.containsKey(delegate)) {
                ((BiConsumer) action).accept(delegate, delegateContext);
            }
        }
    }


    protected final Map<M, ?> unmapWithResult(Map<M, ?> context, BiFunction<M, ?, ?> func) {
        Map<M, Object> resultMap = new HashMap<>(context.size());
        for (M delegate : delegates) {
            Object delegateContext = context.get(delegate);
            if (delegateContext != null || context.containsKey(delegate)) {
                Object result = ((BiFunction) func).apply(delegate, delegateContext);
                resultMap.put(delegate, result);
            }
        }
        return resultMap;
    }


    protected final void unmap2(Map<M, ?> context1, Map<M, ?> context2, TriConsumer<M, ?, ?> action) {
        for (M delegate : delegates) {
            Object delegateContext1 = context1.get(delegate);
            if (delegateContext1 != null || context1.containsKey(delegate)) {
                Object delegateContext2 = context2.get(delegate);
                if (delegateContext2 != null || context2.containsKey(delegate)) {
                    ((TriConsumer) action).accept(delegate, delegateContext1, delegateContext2);
                }
            }
        }
    }


    protected final Map<M, ?> unmap2WithResult(Map<M, ?> context1, Map<M, ?> context2, TriFunction<M, ?, ?, ?> func) {
        Map<M, Object> resultMap = new HashMap<>(Math.min(context1.size(), context2.size()));
        for (M delegate : delegates) {
            Object delegateContext1 = context1.get(delegate);
            if (delegateContext1 != null || context1.containsKey(delegate)) {
                Object delegateContext2 = context2.get(delegate);
                if (delegateContext2 != null || context2.containsKey(delegate)) {
                    Object result = ((TriFunction) func).apply(delegate, delegateContext1, delegateContext2);
                    resultMap.put(delegate, result);
                }
            }
        }
        return resultMap;
    }


    @Override
    public final boolean isEnabled() {
        for (M delegate : delegates) {
            if (delegate.isEnabled()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public final void close() {
        dispatch(M::close);
    }


    @FunctionalInterface
    protected interface TriConsumer<T, U, V> {

        void accept(T t, U u, V v);
    }

    @FunctionalInterface
    protected interface TriFunction<T, U, V, R> {

        R apply(T t, U u, V v);
    }
}
