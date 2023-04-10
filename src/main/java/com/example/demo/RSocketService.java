package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.v1.CloudEventV1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class RSocketService {
    private final static ObjectMapper mapper = new ObjectMapper();
    private RSocketRequester rsocketRequester;
    private RSocketRequester.Builder rsocketRequesterBuilder;
    private RSocketStrategies rsocketStrategies;

    @Autowired
    public RSocketService(RSocketRequester.Builder rsocketRequesterBuilder, RSocketStrategies rsocketStrategies) {
        this.rsocketRequesterBuilder = rsocketRequesterBuilder;
        this.rsocketStrategies = rsocketStrategies;
    }

    public void connect(String s) {
        SocketAcceptor responder = RSocketMessageHandler.responder(rsocketStrategies, new ClientHandler());
        MetadataHeader metadataHeader = new MetadataHeader("test", "test", "test", "test");
        ByteBuf metadata;
        try {
            metadata = Unpooled.wrappedBuffer(mapper.writeValueAsBytes(metadataHeader));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        CloudEventV1 cloudEventV1 = new CloudEventV1(UUID.randomUUID().toString(), URI.create("https://spring.io/foos"), "io.spring.event.Foo", "application/json", URI.create(""), "", null, PojoCloudEventData.wrap(new Location("0111", "ms372", 47.533, 98.644),
                mapper::writeValueAsBytes), null);
        UsernamePasswordMetadata usernamePasswordMetadata = new UsernamePasswordMetadata("root", "root");
        this.rsocketRequester = rsocketRequesterBuilder
                .setupRoute("connect")
                .setupData(cloudEventV1)
                .dataMimeType(MimeType.valueOf("application/cloudevents+json"))
                .setupMetadata(usernamePasswordMetadata, MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                //.setupMetadata(metadata, MimeType.valueOf("application/x.meta+json"))
                //.rsocketStrategies(builder -> builder.encoder(new CloudEventEncoder()))
                .rsocketConnector(connector -> connector.acceptor(responder))
                .connectTcp("127.0.0.1", 9898)
                .block();
        this.rsocketRequester.rsocket()
                .onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.info("Client DISCONNECTED"))
                .subscribe();
    }
}

@Slf4j
class ClientHandler {

    @MessageMapping("status")
    public Flux<String> statusUpdate(String status) {
        return Flux.interval(Duration.ofSeconds(5)).map(index -> String.valueOf(Runtime.getRuntime().freeMemory()));
    }
}
