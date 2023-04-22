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
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

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
        /*rsocketRequester=RSocketRequester.builder().dataMimeType(MimeType.valueOf("application/cloudevents+json")).rsocketStrategies(RSocketStrategies.builder()
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
                })).tcp("localhost", 9898);*/
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
        /*rsocketRequester = builder
                .dataMimeType(MimeType.valueOf("application/cloudevents+json"))*//*.rsocketStrategies(RSocketStrategies.builder()
                        .decoders(decoders -> {
                            decoders.add(new CloudEventDecoder());
                        })
                        .encoders(encoders -> {
                            encoders.add(new io.cloudevents.spring.codec.CloudEventEncoder());
                            encoders.add(new SimpleAuthenticationEncoder());
                            encoders.add(new Jackson2JsonEncoder());
                        })
                        .routeMatcher(new PathPatternRouteMatcher())
                        .dataBufferFactory(new DefaultDataBufferFactory(true))
                        .build())*//*
                .tcp(host, port);*/
    }


    @Test
    void echoWithCorrectHeaders() {
        final EventExtension eventExtension = new EventExtension();
        eventExtension.setAppid("mfs");
        //eventExtension.setReplyto("test");
        // eventExtension.setDelay("100000");
        //eventExtension.setPriority(10);
        CountDownLatch latch = new CountDownLatch(1);
        /*Map<String, Object> extensions = new HashMap<>();
       // extensions.put("userId", "test");
        extensions.put("appId", "mfs");
        extensions.put("priority", 0);
        extensions.put("correlationId", UUID.randomUUID().toString());
        //extensions.put("replyTo", "");
        extensions.put("contentEncoding", "application/cloudevents+json");
        //extensions.put("expiration", "2023-01-01T00:00:00.000Z");
        //extensions.put("x-delay", 0);*/
        Flux<CloudEvent> flux1 = Flux.range(1, 30000)
                // .delayElements(Duration.ofMillis(5))
                .map(i -> {
                    long id = Long.valueOf(snow.generateNextId());
                    eventExtension.setPublishingid(id);
                    return CloudEventBuilder.v1()
                            .withDataContentType("application/cloudevents+json")
                            .withId(UUID.randomUUID().toString()) //
                            .withSource(URI.create("https://spring.io/foos")) //
                            .withType("io.spring.event.Foo") //
                            .withTime(Instant.now().atOffset(ZoneOffset.UTC))
                            .withData(PojoCloudEventData.wrap("newFo000000",
                                    mapper::writeValueAsBytes))
                            .withExtension(eventExtension)
                            .build();
                    /*return EventFormatProvider
                            .getInstance()
                            .resolveFormat(JsonFormat.CONTENT_TYPE)
                            .serialize(event);*/
                    // return new CloudEventV1(UUID.randomUUID().toString(), URI.create("https://spring.io/foos"), "com.github.pull.create", "text/plain", URI.create(""), "", null, PojoCloudEventData.wrap("test", mapper::writeValueAsBytes), extensions);
                    /*return EventFormatProvider
                            .getInstance()
                            .resolveFormat(JsonFormat.CONTENT_TYPE)
                            .serialize(event);*/
                })
                .doOnComplete(() -> {
                    latch.countDown();
                });
        //Flux<String> flux =
        rsocketRequester.route("publish").metadata(new MetadataHeader("test", "test1", 0), MimeType.valueOf("application/x.metadataHeader+json"))
                .data(flux1)
                .retrieveFlux(String.class);
        // flux.blockLast(Duration.ofSeconds(5000));

    }


}

