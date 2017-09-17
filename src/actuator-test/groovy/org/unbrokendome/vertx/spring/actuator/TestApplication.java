package org.unbrokendome.vertx.spring.actuator;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.unbrokendome.vertx.spring.TestVerticle;


@SpringBootConfiguration
@EnableAutoConfiguration
public class TestApplication {

    @Bean
    public TestVerticle testVerticle() {
        return new TestVerticle();
    }
}
