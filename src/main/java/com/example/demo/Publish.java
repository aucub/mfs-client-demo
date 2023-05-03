package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.v1.CloudEventV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class Publish implements Runnable {


    private final static ObjectMapper mapper = new ObjectMapper();
    private static RSocketRequester rsocketRequester;
    Snow snow = new Snow(0L, 0L);
    @Autowired
    private RSocketRequester.Builder builder;

    public final static void init() {
        String host = "127.0.0.1";
        int port = 9898;
        rsocketRequester =
                RSocketRequester.builder()
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
                                    encoders.add(new BearerTokenAuthenticationEncoder());
                                    encoders.add(new SimpleAuthenticationEncoder());
                                    encoders.add(new Jackson2CborEncoder());
                                })
                                .routeMatcher(new PathPatternRouteMatcher())
                                .dataBufferFactory(new DefaultDataBufferFactory(true))
                                .build()
                        )
                        .tcp(host, port);
    }

    void echo() {

        Flux<CloudEventV1> flux1 = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(50))
                .map(i -> {
                    EventExtension eventExtension = new EventExtension();
                    eventExtension.setPublishingid(snow.nextId());
                    return (CloudEventV1) CloudEventBuilder.v1()
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
        rsocketRequester
                .route("publish")
                .metadata(Token.token, MimeTypeUtils.parseMimeType("message/x.rsocket.authentication.bearer.v0"))
                .metadata(new MetadataHeader("", "test1", 0), MimeType.valueOf("application/x.metadataHeader+json"))
                .data(flux1).retrieveFlux(String.class).subscribe(
                        s -> System.out.println(s)
                );
    }

    @Override
    public void run() {
        init();
        echo();
    }
}
