package org.unbrokendome.vertx.spring;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@SuppressWarnings("unused")
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VerticleDeployment {

    @AliasFor("autoDeploy")
    boolean value() default true;

    @AliasFor("value")
    boolean autoDeploy() default true;

    boolean worker() default DeploymentOptions.DEFAULT_WORKER;

    boolean multiThreaded() default DeploymentOptions.DEFAULT_MULTI_THREADED;

    boolean ha() default DeploymentOptions.DEFAULT_HA;

    int instances() default DeploymentOptions.DEFAULT_INSTANCES;

    String workerPoolName() default "";

    int workerPoolSize() default VertxOptions.DEFAULT_WORKER_POOL_SIZE;

    long maxWorkerExecuteTime() default VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME;
}
