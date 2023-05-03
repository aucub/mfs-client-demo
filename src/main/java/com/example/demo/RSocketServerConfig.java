package com.example.demo;


//已弃用
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



