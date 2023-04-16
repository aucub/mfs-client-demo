package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static com.example.demo.RSocketService.rSocketRequester;

@Slf4j
@RestController
public class DemoService {

    private final static ObjectMapper mapper = new ObjectMapper();


    public void pageList() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://127.0.0.1:8080/")
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)")
                .build();
        Mono<String> mono = webClient
                .get()
                .uri("login/doLogin2")  // 请求路径
                //.bodyValue(new UserLoginDto("root",null,"root"))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); //响应数据类型转换
        System.out.println(mono.block());
    }

    public void pub() {
        RSocketRequester rSocketRequester = RSocketRequester.builder().tcp("127.0.0.1", 9898);
        rSocketRequester
                .route("publish.publish1")
                .metadata("test", MimeType.valueOf("application/json"))
                .data("Hello RSocket!")
                .retrieveMono(String.class)
                .subscribe(response -> log.info(response));

    }

    @RequestMapping("pu")
    public void pu() {
        //UsernamePasswordMetadata usernamePasswordMetadata = new UsernamePasswordMetadata("root", "root");
        Random rand = new Random(System.currentTimeMillis());
        CountDownLatch latch = new CountDownLatch(1);
        Flux<CloudEvent> flux = Flux.range(1, 300)
                .delayElements(Duration.ofMillis(50))
                .map(i -> {
                    return (CloudEvent)CloudEventBuilder.v1()
                            .withDataContentType("application/cloudevents+json")
                            .withId(UUID.randomUUID().toString()) //
                            .withSource(URI.create("https://spring.io/foos")) //
                            .withType("io.spring.event.Foo") //
                            .withData(PojoCloudEventData.wrap("test",
                                    mapper::writeValueAsBytes))
                            .build();
                    /*return EventFormatProvider
                            .getInstance()
                            .resolveFormat(JsonFormat.CONTENT_TYPE)
                            .serialize(event);*/
                    //return new CloudEventV1(UUID.randomUUID().toString(), URI.create("https://spring.io/foos"), "com.github.pull.create", "text/plain", URI.create(""), "", null, PojoCloudEventData.wrap("test", mapper::writeValueAsBytes), null);
                })
                .doOnComplete(() -> {
                    latch.countDown();
                });/*CloudEventBuilder.v1()
                .withDataContentType("application/cloudevents+json")
                .withId(UUID.randomUUID().toString()) //
                .withSource(URI.create("https://spring.io/foos")) //
                .withType("io.spring.event.Foo") //
                .withData(PojoCloudEventData.wrap("test",
                        mapper::writeValueAsBytes))
                .build()*/
        RSocketRequester.builder().dataMimeType(MimeType.valueOf("application/cloudevents+json")).tcp("localhost", 9898)
                .route("publish")
                //.metadata(usernamePasswordMetadata, MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                .data(flux).retrieveFlux(String.class).subscribe(item -> log.info(item));

        /*rSocketRequester
                .route("publish")
                .metadata(usernamePasswordMetadata, MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                .send();
                *//*.data("Hello RSocket!")
                .retrieveMono(String.class)
                .subscribe(response -> log.info(response));*/
    }

    @RequestMapping("poo")
    public void poo() {
        rSocketRequester.route("publish")
                .data("ssssssssssssssss").send();
    }

   /* @Bean
    @Order(-1)
    public RSocketStrategiesCustomizer cloudEventsCustomizer() {
        return new RSocketStrategiesCustomizer() {
            @Override
            public void customize(RSocketStrategies.Builder strategies) {
                strategies.encoder(new io.cloudevents.spring.codec.CloudEventEncoder());
                strategies.decoder(new io.cloudevents.spring.codec.CloudEventDecoder());
            }
        };

    }*/

}
