package org.unbrokendome.vertx.spring.boot;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.unbrokendome.vertx.spring.TestVerticle;


@SpringBootConfiguration
@ImportAutoConfiguration(VertxAutoConfiguration.class)
public class TestApplication {

    @Bean
    public TestVerticle testVerticle() {
        return new TestVerticle();
    }
}
