package com.example.demo;

import io.cloudevents.spring.codec.CloudEventDecoder;
import io.cloudevents.spring.codec.CloudEventEncoder;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

@Configuration
@Component
public class RSocketServerConfig {

    /*  @Bean
      RSocketStrategiesCustomizer rSocketStrategiesCustomizer() {
          return strategies -> strategies.encoder(new SimpleAuthenticationEncoder());
      }
  */

    /*@Bean
    //@Order(-1)
    public RSocketStrategiesCustomizer cloudEventsCustomizer() {
        return new RSocketStrategiesCustomizer() {
            @Override
            public void customize(RSocketStrategies.Builder strategies) {
                strategies.encoder(new CloudEventEncoder());
                //strategies.decoder(new CloudEventDecoder());
            }
        };

    }*/
    /*@Bean
   // @Order(-1)
    public RSocketStrategies rsocketStrategies() {
        return RSocketStrategies.builder()
                .decoders(decoders -> {
                    decoders.add(new io.cloudevents.spring.codec.CloudEventDecoder());
                    decoders.add(new Jackson2CborDecoder());
                    decoders.add(new Jackson2JsonDecoder());
                })
                .encoders(encoders -> {
                    encoders.add(new io.cloudevents.spring.codec.CloudEventEncoder());
                    encoders.add(new Jackson2JsonEncoder());
                    encoders.add(new SimpleAuthenticationEncoder());
                    encoders.add(new Jackson2CborEncoder());
                })
                .routeMatcher(new PathPatternRouteMatcher())
                .dataBufferFactory(new DefaultDataBufferFactory(true))
                .build();
    }*/

}



