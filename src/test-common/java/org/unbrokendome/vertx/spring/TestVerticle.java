package org.unbrokendome.vertx.spring;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import org.springframework.beans.factory.BeanNameAware;


public class TestVerticle extends AbstractVerticle implements BeanNameAware {

    private String name;

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    public final Context getContext() {
        return context;
    }

    public final boolean isDeployed() {
        return context != null;
    }


    @Override
    public String toString() {
        return name != null ? name : super.toString();
    }
}
