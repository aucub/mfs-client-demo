package com.example.demo;

import io.cloudevents.spring.codec.CloudEventDecoder;
import io.cloudevents.spring.codec.CloudEventEncoder;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

@Configuration
public class RSocketServerConfig {

  /*  @Bean
    RSocketStrategiesCustomizer rSocketStrategiesCustomizer() {
        return strategies -> strategies.encoder(new SimpleAuthenticationEncoder());
    }
*/
    @Bean
    public RSocketStrategies rsocketStrategies() {
        return RSocketStrategies.builder()
                .decoders(decoders -> {
                    decoders.add(new CloudEventDecoder());
                    decoders.add(new Jackson2CborDecoder());
                    decoders.add(new Jackson2JsonDecoder());
                })
                .encoders(encoders -> {
                    encoders.add(new SimpleAuthenticationEncoder());
                    encoders.add(new CloudEventEncoder());
                    encoders.add(new Jackson2CborEncoder());
                    encoders.add(new Jackson2JsonEncoder());
                })
                .routeMatcher(new PathPatternRouteMatcher())
                .dataBufferFactory(new DefaultDataBufferFactory(true))
                .build();
    }

}



