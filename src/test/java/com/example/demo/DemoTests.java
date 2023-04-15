package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.v1.CloudEventV1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoTests {
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
                .dataMimeType(MimeType.valueOf("application/cloudevents+json"))
                .tcp(host, port);
    }

    @Test
    void echoWithCorrectHeaders() {
        CountDownLatch latch = new CountDownLatch(1);
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("userId", "test");
        extensions.put("appId", "mfs");
        extensions.put("priority", 0);
        extensions.put("correlationId", UUID.randomUUID().toString());
        extensions.put("replyTo", "");
        extensions.put("contentEncoding", "application/json");
        extensions.put("expiration", "2023-01-01T00:00:00.000Z");
        extensions.put("x-delay", 0);
        Flux<CloudEventV1> flux1 = Flux.range(1, 300)
                .delayElements(Duration.ofMillis(500))
                .map(i -> {
                    /*return CloudEventBuilder.v1()
                            .withDataContentType("application/cloudevents+json")
                            .withId(UUID.randomUUID().toString()) //
                            .withSource(URI.create("https://spring.io/foos")) //
                            .withType("io.spring.event.Foo") //
                            .withData(PojoCloudEventData.wrap("newFo",
                                    mapper::writeValueAsBytes))
                            .build();*/
                    return new CloudEventV1(UUID.randomUUID().toString(), URI.create("https://spring.io/foos"), "com.github.pull.create", "text/plain", URI.create(""), "", null, PojoCloudEventData.wrap("test", mapper::writeValueAsBytes), extensions);
                })
                .doOnComplete(() -> {
                    latch.countDown();
                });
        Flux<String> flux = rsocketRequester.route("publish")
                .data(flux1)
                .retrieveFlux(String.class);
        flux.blockLast(Duration.ofSeconds(5000));

    }

}
