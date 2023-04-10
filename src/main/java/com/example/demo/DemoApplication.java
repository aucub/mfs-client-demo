package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.*;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.metadata.CompositeMetadataCodec;
import io.rsocket.metadata.TaggingMetadataCodec;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.ByteBufPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeTypeUtils;
import reactor.netty.tcp.TcpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class,org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        /*

        CompositeByteBuf metadata = ByteBufAllocator.DEFAULT.compositeBuffer();
        ByteBuf setupRouteMetadata = TaggingMetadataCodec.createTaggingContent(
                ByteBufAllocator.DEFAULT,
                Collections.singletonList("connect"));
        CompositeMetadataCodec.encodeAndAddMetadata(metadata,
                ByteBufAllocator.DEFAULT,
                WellKnownMimeType.MESSAGE_RSOCKET_ROUTING.getString(),setupRouteMetadata);
        UsernamePasswordMetadata usernamePasswordMetadata = new UsernamePasswordMetadata("test2", "test2");
        ByteBuf credentials;
        credentials= Unpooled.wrappedBuffer("rootroot".getBytes());
        CompositeMetadataCodec.encodeAndAddMetadata(metadata,
                ByteBufAllocator.DEFAULT,
                WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString(),credentials);
        RSocket socket = RSocketConnector.create()
                // 设置 metadata MIME Type，方便服务端根据 MIME 类型确定 metadata 内容
                .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString())
                // SETUP 阶段的 Payload，data 里面存放 UUID
                .setupPayload(ByteBufPayload.create(
                        ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, "testttttt"),
                        metadata))
                // 设置重连策略
                .reconnect(Retry.backoff(2, Duration.ofMillis(500)))
                .connect(
                        TcpClientTransport.create(
                                TcpClient.create()
                                        .host("127.0.0.1")
                                        .port(9898)))
                .block();

        */
        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
