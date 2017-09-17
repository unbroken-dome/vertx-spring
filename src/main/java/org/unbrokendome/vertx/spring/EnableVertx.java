package org.unbrokendome.vertx.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(VertxConfigurationImportSelector.class)
@SuppressWarnings("unused")
public @interface EnableVertx {
    boolean deployVerticles() default true;
}
