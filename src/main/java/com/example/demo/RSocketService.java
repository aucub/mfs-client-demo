package com.example.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.CompositeMetadataCodec;
import io.rsocket.metadata.TaggingMetadataCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Collections;

@Service
public class RSocketService {
    private RSocketRequester rsocketRequester;
    private RSocketRequester.Builder rsocketRequesterBuilder;
    private RSocketStrategies rsocketStrategies;

    @Autowired
    public RSocketService(RSocketRequester.Builder rsocketRequesterBuilder, @Qualifier("rSocketStrategies") RSocketStrategies rsocketStrategies) {
        this.rsocketRequesterBuilder = rsocketRequesterBuilder;
        this.rsocketStrategies = rsocketStrategies;
    }

    public void connect(String s) {
        SocketAcceptor responder = RSocketMessageHandler.responder(rsocketStrategies, new ClientHandler());
        CompositeByteBuf metadata = ByteBufAllocator.DEFAULT.compositeBuffer();
        ByteBuf token = TaggingMetadataCodec
                .createTaggingContent(ByteBufAllocator.DEFAULT, Collections.singletonList(s));
        CompositeMetadataCodec.encodeAndAddMetadata(metadata,
                ByteBufAllocator.DEFAULT,
                "message/x.hello.trace",
                token);
        this.rsocketRequester = rsocketRequesterBuilder
                .setupRoute("connect")
                .setupData(s)
                .dataMimeType(MimeTypeUtils.TEXT_PLAIN)
                .setupMetadata(token, MimeType.valueOf("message/x.hello.trace"))
                .rsocketStrategies(builder ->
                        builder.encoder(new Jackson2JsonEncoder()))
                .rsocketConnector(connector -> connector.acceptor(responder))
                .connectTcp("127.0.0.1", 9898)
                .block();
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
