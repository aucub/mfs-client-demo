package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.v03.CloudEventV03;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.spring.codec.CloudEventEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.CompositeMetadataCodec;
import io.rsocket.metadata.TaggingMetadataCodec;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
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
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@Slf4j
public class RSocketService {
    private RSocketRequester rsocketRequester;
    private RSocketRequester.Builder rsocketRequesterBuilder;
    private RSocketStrategies rsocketStrategies;

    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public RSocketService(RSocketRequester.Builder rsocketRequesterBuilder, RSocketStrategies rsocketStrategies) {
        this.rsocketRequesterBuilder = rsocketRequesterBuilder;
        this.rsocketStrategies = rsocketStrategies;
    }

    public void connect(String s) {
        SocketAcceptor responder = RSocketMessageHandler.responder(rsocketStrategies, new ClientHandler());
        CompositeByteBuf metadata = ByteBufAllocator.DEFAULT.compositeBuffer();
        MetadataHeader metadataHeader=new MetadataHeader("test","test","test","test");
        ByteBuf client = TaggingMetadataCodec.createTaggingContent(ByteBufAllocator.DEFAULT, Collections.singletonList("test"));
        try {
            client=Unpooled.wrappedBuffer(mapper.writeValueAsBytes(metadataHeader));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        CompositeMetadataCodec.encodeAndAddMetadata(metadata,
                ByteBufAllocator.DEFAULT,
                WellKnownMimeType.APPLICATION_JSON,client
                );
        ByteBuf topic = TaggingMetadataCodec.createTaggingContent(ByteBufAllocator.DEFAULT, Collections.singletonList("test1"));
        UsernamePasswordMetadata usernamePasswordMetadata=new UsernamePasswordMetadata("root","root");
        ByteBuf credentials;
        try {
            credentials=Unpooled.wrappedBuffer(mapper.writeValueAsBytes(usernamePasswordMetadata));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        CloudEventBuilder.v1()
                .withDataContentType("application/cloudevents+json")
                .withId(UUID.randomUUID().toString()) //
                .withSource(URI.create("https://spring.io/foos")) //
                .withType("io.spring.event.Foo") //
                .withDataSchema(URI.create(""))
                .withSubject("")
                .withTime(OffsetDateTime.now())
                .withData(PojoCloudEventData.wrap(new Location("0111","ms372",47.533,98.644),
                        mapper::writeValueAsBytes))
                .build();
        CloudEventV1 cloudEventV1=new CloudEventV1(UUID.randomUUID().toString(),URI.create("https://spring.io/foos"),"io.spring.event.Foo","application/json",URI.create(""),"",null,PojoCloudEventData.wrap(new Location("0111","ms372",47.533,98.644),
                mapper::writeValueAsBytes),null);
            this.rsocketRequester = rsocketRequesterBuilder
                    .setupRoute("connect1")
                    .setupData(cloudEventV1)
                    .dataMimeType(MimeType.valueOf("application/cloudevents+json"))
                    //.setupMetadata(client, MimeType.valueOf("application/x.meta+json"))
                    //.setupMetadata("test2227855",MimeType.valueOf("application/x.token+json"))
                    //.setupMetadata(usernamePasswordMetadata,MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
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
        ;
        return Flux.interval(Duration.ofSeconds(5)).map(index -> String.valueOf(Runtime.getRuntime().freeMemory()));
    }
}
