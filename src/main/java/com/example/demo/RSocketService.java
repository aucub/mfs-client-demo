package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.SocketAcceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class RSocketService {
    private final static ObjectMapper mapper = new ObjectMapper();
    public static RSocketRequester rSocketRequester;
    private RSocketRequester.Builder rsocketRequesterBuilder;

    @Autowired
    public RSocketService(RSocketRequester.Builder rsocketRequesterBuilder) {
        this.rsocketRequesterBuilder = rsocketRequesterBuilder;
    }

    @RequestMapping("/connect")
    public void connect() {
        SocketAcceptor responder = RSocketMessageHandler.responder(RSocketStrategies.builder()
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
                .build(), new ClientHandler());
        rSocketRequester = rsocketRequesterBuilder
                .setupRoute("connect")
                .setupMetadata(Token.token, MimeTypeUtils.parseMimeType("message/x.rsocket.authentication.bearer.v0"))
                .rsocketConnector(connector -> connector.acceptor(responder))
                .connectTcp("127.0.0.1", 9898).block();
        this.rSocketRequester.rsocket()
                .onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.info("Client DISCONNECTED"))
                .subscribe();
    }
}

@Slf4j
class ClientHandler {

    @MessageMapping("status")
    public Mono<String> statusUpdate(String status) {
        System.out.println(status);
        return Mono.just("OK");
    }
}
