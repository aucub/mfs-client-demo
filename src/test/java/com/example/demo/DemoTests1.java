package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
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
                .dataMimeType(MimeType.valueOf("application/cloudevents+json"))
                .tcp(host, port);
    }

    @Test
    void echo() {
        Flux<byte[]> flux = rsocketRequester.route("consume")
                .metadata(Token.token, MimeTypeUtils.parseMimeType("message/x.rsocket.authentication.bearer.v0"))
                .data(new Consume("stream", "test1", 0, 0, false, 0))
                .retrieveFlux(byte[].class);
        flux.subscribe(item -> System.out.println(new String(item)));
        flux.blockLast(Duration.ofSeconds(5000));
    }

}
