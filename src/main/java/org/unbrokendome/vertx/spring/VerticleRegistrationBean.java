package org.unbrokendome.vertx.spring;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.Ordered;


@SuppressWarnings("unused")
public class VerticleRegistrationBean implements VerticleRegistration, BeanNameAware, Ordered {

    private Verticle verticle;
    private String verticleName;
    private DeploymentOptions deploymentOptions;
    private Integer order;
    private String name;
    private String beanName;

    public VerticleRegistrationBean() {
    }


    public VerticleRegistrationBean(Verticle verticle) {
        this.verticle = verticle;
    }


    public VerticleRegistrationBean(String verticleName) {
        this.verticleName = verticleName;
    }


    public VerticleRegistrationBean(Verticle verticle, DeploymentOptions deploymentOptions) {
        this(verticle);
        this.deploymentOptions = deploymentOptions;
    }


    public VerticleRegistrationBean(String verticleName, DeploymentOptions deploymentOptions) {
        this(verticleName);
        this.deploymentOptions = deploymentOptions;
    }


    @Override
    public Verticle getVerticle() {
        return verticle;
    }


    public VerticleRegistrationBean setVerticle(Verticle verticle) {
        this.verticle = verticle;
        return this;
    }


    @Override
    public String getVerticleName() {
        return verticleName;
    }


    public VerticleRegistrationBean setVerticleName(String verticleName) {
        this.verticleName = verticleName;
        return this;
    }


    @Override
    public DeploymentOptions getDeploymentOptions() {
        if (deploymentOptions != null) {
            return deploymentOptions;
        }
        if (verticle instanceof DeployableVerticle) {
            return ((DeployableVerticle) verticle).getDeploymentOptions();
        }
        return null;
    }


    public VerticleRegistrationBean setDeploymentOptions(DeploymentOptions deploymentOptions) {
        this.deploymentOptions = deploymentOptions;
        return this;
    }


    @Override
    public int getOrder() {
        if (order != null) {
            return order;
        }
        if (verticle instanceof Ordered) {
            return ((Ordered) verticle).getOrder();
        }
        return 0;
    }


    public VerticleRegistrationBean setOrder(int order) {
        this.order = order;
        return this;
    }


    public String getName() {
        return name;
    }


    public VerticleRegistration setName(String name) {
        this.name = name;
        return this;
    }


    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


    @Override
    public String toString() {
        if (name != null) {
            return name;
        } else if (beanName != null) {
            return beanName;
        } else if (verticleName != null) {
            return verticleName;
        } else if (verticle != null) {
            return verticle.getClass().getName();
        } else {
            return super.toString();
        }
    }
}
