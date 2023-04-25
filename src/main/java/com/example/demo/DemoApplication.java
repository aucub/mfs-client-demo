package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
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
        ByteBuf credentials;
        credentials= Unpooled.wrappedBuffer("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJkMWM1ZDI3OS1kMTk4LTRiMTYtYjEzMy1mYzE5ODhjNWJjYzUiLCJpc3MiOiIwYzU5OTg5ZDM5NzAzODBhZTE2ODg4MDY4NmM0YTA3MCIsInN1YiI6IjBjNTk5ODlkMzk3MDM4MGFlMTY4ODgwNjg2YzRhMDcwIiwiZXhwIjoxNzgyMDE2OTEyLCJhdWQiOiJtZnMiLCJzY29wZSI6WyJ1c2VyTWFuIiwiZ2V0Snd0IiwiZ2VuZXJhdGVKd3QiLCJzZWFyY2hTZXNzaW9uIiwicm9sZSIsImtpY2tvdXQiLCJkaXNhYmxlIiwiY29ubmVjdCIsInB1c2giLCJwdWJsaXNoIiwiY29uc3VtZSJdfQ.8wHE60sj9wYkZ_aejpgIpssi6-S034td3GjnF7qW2Sw".getBytes());
        CompositeMetadataCodec.encodeAndAddMetadata(metadata,
                ByteBufAllocator.DEFAULT,
                WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString(),credentials);
        RSocket socket = RSocketConnector.create()
                // 设置 metadata MIME Type，方便服务端根据 MIME 类型确定 metadata 内容
                .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString())
                // SETUP 阶段的 Payload
                .setupPayload(ByteBufPayload.create(
                        ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, "test"),
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

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        int threadSize = 2;
        System.out.println("777777777766666666");
        for (int i = 0; i < threadSize; i++) {
            Thread.startVirtualThread(() -> {
                        executor.submit(new Publish());
                    }
            );
        }
    }

}
