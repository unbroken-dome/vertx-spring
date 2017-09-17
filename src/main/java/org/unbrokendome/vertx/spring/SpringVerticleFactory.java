package org.unbrokendome.vertx.spring;

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;


public class SpringVerticleFactory implements VerticleFactory {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String prefix;
    private final BeanFactory beanFactory;


    public SpringVerticleFactory(String prefix, BeanFactory beanFactory) {
        this.prefix = prefix;
        this.beanFactory = beanFactory;
    }


    @Override
    public String prefix() {
        return prefix;
    }


    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        String beanName = getBeanNameFromVerticleName(verticleName);
        return createVerticleFromBean(beanName);
    }


    private String getBeanNameFromVerticleName(String verticleName) {
        String beanName;
        if (verticleName.startsWith(prefix + ":")) {
            beanName = verticleName.substring(prefix.length() + 1);
        } else {
            beanName = verticleName;
        }
        return beanName;
    }


    private Verticle createVerticleFromBean(String beanName) {
        if (!beanFactory.containsBean(beanName)) {
            throw new IllegalArgumentException("No such bean: " + beanName);
        }

        if (!beanFactory.isTypeMatch(beanName, Verticle.class)) {
            throw new IllegalArgumentException("Bean \"" + beanName + "\" is not of type Verticle");
        }

        if (beanFactory.isSingleton(beanName)) {
            throw new IllegalArgumentException("Verticle bean \"" + beanName + "\" is a singleton bean");
        }

        logger.debug("Creating verticle from bean \"{}\"", beanName);
        return beanFactory.getBean(beanName, Verticle.class);
    }


    @Override
    public String toString() {
        return "SpringVerticleFactory with prefix \"" + prefix + "\"";
    }
}
