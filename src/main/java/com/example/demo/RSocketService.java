package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Collections;

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
        this.rsocketRequester = rsocketRequesterBuilder
                .setupRoute("connect")
                .setupData(s)
                .dataMimeType(MimeTypeUtils.TEXT_PLAIN)
                .setupMetadata(client, MimeType.valueOf("application/x.meta+json"))
                .setupMetadata("test2227855",MimeType.valueOf("application/x.token+json"))
                .rsocketStrategies(builder ->
                        builder.encoder(new Jackson2JsonEncoder()))
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
