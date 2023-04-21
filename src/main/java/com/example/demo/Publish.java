package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.spring.codec.CloudEventDecoder;
import io.cloudevents.spring.codec.CloudEventEncoder;
import io.rsocket.SocketAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

@Service
public class Publish implements Runnable{


    private final static ObjectMapper mapper=new ObjectMapper();


    @Autowired
    private RSocketRequester.Builder builder;

    private static RSocketRequester rsocketRequester;

    Snow snow = new Snow(0L,0L);



    public final static void init() {
        String host = "localhost";
        int port = 9898;
      /*  SocketAcceptor responder = RSocketMessageHandler.responder(RSocketStrategies.builder()
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
                //.routeMatcher(new PathPatternRouteMatcher())
                //.dataBufferFactory(new DefaultDataBufferFactory(true))
                .build(), new ClientHandler());*/
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
                        encoders.add(new SimpleAuthenticationEncoder());
                        encoders.add(new Jackson2CborEncoder());
                    })
                    .routeMatcher(new PathPatternRouteMatcher())
                    .dataBufferFactory(new DefaultDataBufferFactory(true))
                    .build()
        )
                .tcp(host, port);
        //rsocketRequester=RSocketRequester.builder().dataMimeType(MimeType.valueOf("application/cloudevents+json")).tcp("localhost", 9898);
    }
    void echoWithCorrectHeaders() {
        final EventExtension eventExtension = new EventExtension();
        long id=Long.parseLong(snow.generateNextId());
        eventExtension.setPublishingid(id);
        /*eventExtension.setDelay(0);
        eventExtension.setUserid("root");
        eventExtension.setExpiration("99999");*/
        CountDownLatch latch = new CountDownLatch(1);
        Flux<CloudEventV1> flux1 = Flux.range(1, 5)
                //.delayElements(Duration.ofMillis(500))
                .map(i -> (CloudEventV1) CloudEventBuilder.v1()
                        .withDataContentType("application/cloudevents+json")
                        .withId(UUID.randomUUID().toString()) //
                        .withSource(URI.create("https://spring.io/foos")) //
                        .withType("io.spring.event.Foo") //
                        .withTime(Instant.now().atOffset(ZoneOffset.UTC))
                        .withData(PojoCloudEventData.wrap("newFo000000",
                                mapper::writeValueAsBytes))
                        .withExtension(eventExtension)
                        .build())
                .doOnComplete(() -> {
                    latch.countDown();
                    //System.out.println("OKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
                });
        /*rsocketRequester.route("connect").metadata(new MetadataHeader("test", "", "test1", "stream",0), MimeType.valueOf("application/x.metadataHeader+json"))
                .data((CloudEventV1) CloudEventBuilder.v1()
                        .withDataContentType("application/cloudevents+json")
                        .withId(UUID.randomUUID().toString()) //
                        .withSource(URI.create("https://spring.io/foos")) //
                        .withType("io.spring.event.Foo") //
                        .withTime(Instant.now().atOffset(ZoneOffset.UTC))
                        .withData(PojoCloudEventData.wrap("newFo000000",
                                mapper::writeValueAsBytes))
                        .withExtension(eventExtension)
                        .build())
                .retrieveMono(Void.class);//.blockLast(Duration.ofSeconds(1000000));*/
        rsocketRequester
                .route("publish")
                .metadata("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJkMWM1ZDI3OS1kMTk4LTRiMTYtYjEzMy1mYzE5ODhjNWJjYzUiLCJpc3MiOiIwYzU5OTg5ZDM5NzAzODBhZTE2ODg4MDY4NmM0YTA3MCIsInN1YiI6IjBjNTk5ODlkMzk3MDM4MGFlMTY4ODgwNjg2YzRhMDcwIiwiZXhwIjoxNzgyMDE2OTEyLCJhdWQiOiJtZnMiLCJzY29wZSI6WyJ1c2VyTWFuIiwiZ2V0Snd0IiwiZ2VuZXJhdGVKd3QiLCJzZWFyY2hTZXNzaW9uIiwicm9sZSIsImtpY2tvdXQiLCJkaXNhYmxlIiwiY29ubmVjdCIsInB1c2giLCJwdWJsaXNoIiwiY29uc3VtZSJdfQ.8wHE60sj9wYkZ_aejpgIpssi6-S034td3GjnF7qW2Sw", MimeTypeUtils.parseMimeType("message/x.rsocket.authentication.bearer.v0"))
                .metadata(new MetadataHeader("","test1",0), MimeType.valueOf("application/x.metadataHeader+json"))
                .data(flux1).retrieveFlux(String.class).subscribe(
                        s -> System.out.println(s)
                );
                //.setupMetadata("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJjNDllMGJkNC01ZWJlLTRiODMtYjBkNy0xZDc3MTdjNDQyMGMiLCJpc3MiOiIwYzU5OTg5ZDM5NzAzODBhZTE2ODg4MDY4NmM0YTA3MCIsInN1YiI6IjBjNTk5ODlkMzk3MDM4MGFlMTY4ODgwNjg2YzRhMDcwIiwiZXhwIjozMTU1Njg4OTg2NDQwMzE5OSwiYXVkIjoibWZzIiwic2NvcGUiOlsidXNlck1hbiIsImdldEp3dCIsImdlbmVyYXRlSnd0Iiwic2VhcmNoU2Vzc2lvbiIsInJvbGUiLCJraWNrb3V0IiwiZGlzYWJsZSIsImNvbm5lY3QiLCJwdXNoIiwicHVibGlzaCIsImNvbnN1bWUiXX0.iWHl7WbhWuID964Gha0XY0YiPFgydqy07Ku-FqBiBus", MimeTypeUtils.parseMimeType("message/x.rsocket.authentication.bearer.v0"))
                //.setupMetadata(metadata, MimeType.valueOf("application/x.meta+json"))
                //.rsocketStrategies(builder -> builder.encoder(new CloudEventEncoder()))
                //.rsocketConnector(connector -> connector.acceptor(responder))

    }

    @Override
    public void run() {
        init();
        echoWithCorrectHeaders();
    }
}
