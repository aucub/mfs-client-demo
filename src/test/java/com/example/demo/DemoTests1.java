package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoTests1 {
    @Autowired
    private RSocketRequester.Builder builder;

    @Autowired
    private ObjectMapper mapper;

    private RSocketRequester rsocketRequester;

    @BeforeEach
    public void init() {
        String host = "localhost";
        int port = 9898;
        rsocketRequester = builder
                //.dataMimeType(MimeType.valueOf("application/cloudevents+json"))
                .dataMimeType(MimeType.valueOf("application/cloudevents+json"))
                .tcp(host, port);
    }

    @Test
    void echoWithCorrectHeaders() {
       /* CountDownLatch latch = new CountDownLatch(1);
        Flux<CloudEvent> flux1 = Flux.range(1, 300)
                .delayElements(Duration.ofMillis(5000))
                .map(i -> {
                    return CloudEventBuilder.v1()
                            .withDataContentType("application/cloudevents+json")
                            .withId(UUID.randomUUID().toString()) //
                            .withSource(URI.create("https://spring.io/foos")) //
                            .withType("io.spring.event.Foo") //
                            .withData(PojoCloudEventData.wrap("newFo",
                                    mapper::writeValueAsBytes))
                            .build();
                    //return new CloudEventV1(UUID.randomUUID().toString(), URI.create("https://spring.io/foos"), "com.github.pull.create", "text/plain", URI.create(""), "", null, PojoCloudEventData.wrap("test", mapper::writeValueAsBytes), null);
                })
                .doOnComplete(() -> {
                    latch.countDown();
                });*/
        Flux<byte[]> flux = rsocketRequester.route("consume")
                .data(new Consume("stream", "test1", 499550L, 0L, true, 0))
                .retrieveFlux(byte[].class);
        flux.subscribe(item -> System.out.println(new String(item)));
        flux.blockLast(Duration.ofSeconds(5000));

    }

}
