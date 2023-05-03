package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.spring.codec.CloudEventDecoder;
import io.cloudevents.spring.codec.CloudEventEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoTests {
    Snow snow = new Snow(0L, 0L);
    @Autowired
    private RSocketRequester.Builder builder;
    @Autowired
    private ObjectMapper mapper;
    private RSocketRequester rsocketRequester;

    @Bean
    public RSocketStrategiesCustomizer cloudEventsCustomizer() {
        return new RSocketStrategiesCustomizer() {
            @Override
            public void customize(RSocketStrategies.Builder strategies) {
                strategies.encoder(new CloudEventEncoder());
                strategies.decoder(new CloudEventDecoder());
            }
        };

    }

    @BeforeEach
    public void init() {
        String host = "localhost";
        int port = 9898;
        rsocketRequester = RSocketRequester.builder()
                .dataMimeType(MimeType.valueOf("application/cloudevents+json"))
                .rsocketStrategies(RSocketStrategies.builder()
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
                        .build()
                )
                .tcp(host, port);
    }


    @Test
    void echoWithCorrectHeaders() {
        Flux<CloudEvent> flux = Flux.range(1, 3000)
                .delayElements(Duration.ofMillis(50))
                .map(i -> {
                    EventExtension eventExtension = new EventExtension();
                    eventExtension.setAppid("mfs");
                    // eventExtension.setDelay("100000");
                    //eventExtension.setPriority(10);
                    eventExtension.setExpiration(Instant.now().plusSeconds(1200).getEpochSecond() * 1000);
                    eventExtension.setPublishingid(snow.nextId());
                    return CloudEventBuilder.v1()
                            .withDataContentType("text")
                            .withId(UUID.randomUUID().toString()) //
                            .withSource(URI.create("https://spring.io/foos")) //
                            .withType("io.spring.event.Foo") //
                            .withTime(Instant.now().atOffset(ZoneOffset.UTC))
                            .withData(PojoCloudEventData.wrap(UUID.randomUUID().toString(),
                                    mapper::writeValueAsBytes))
                            .withExtension(eventExtension)
                            .build();
                });
        rsocketRequester.route("publish")
                .metadata(Token.token, MimeTypeUtils.parseMimeType("message/x.rsocket.authentication.bearer.v0"))
                .metadata(new MetadataHeader("", "test1", 0), MimeType.valueOf("application/x.metadataHeader+json"))
                .data(flux)
                .retrieveFlux(String.class).subscribe(System.out::println);
    }


}

