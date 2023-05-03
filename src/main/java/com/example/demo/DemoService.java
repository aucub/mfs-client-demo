package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@RestController
public class DemoService {

    private final static ObjectMapper mapper = new ObjectMapper();

    Snow snow = new Snow(0L, 0L);

    public void doLogin() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://127.0.0.1:8080/")
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)")
                .build();
        Mono<String> mono = webClient
                .post()
                .uri("login/doLogin")
                .bodyValue(new UserLoginDto("root", "root"))
                .retrieve()
                .bodyToMono(String.class);
        System.out.println(mono.block());
    }

    @RequestMapping("publish")
    public void publish() {
        Flux<CloudEvent> flux = Flux.range(1, 300)
                .delayElements(Duration.ofMillis(50))
                .map(i -> {
                    EventExtension eventExtension = new EventExtension();
                    eventExtension.setPublishingid(snow.nextId());
                    return (CloudEvent) CloudEventBuilder.v1()
                            .withDataContentType("text")
                            .withId(UUID.randomUUID().toString()) //
                            .withSource(URI.create("https://spring.io/foos")) //
                            .withType("io.spring.event.Foo") //
                            .withData(PojoCloudEventData.wrap(UUID.randomUUID().toString(),
                                    mapper::writeValueAsBytes))
                            .withExtension(eventExtension)
                            .build();
                });
        RSocketRequester.builder().dataMimeType(MimeType.valueOf("application/cloudevents+json")).tcp("localhost", 9898)
                .route("publish")
                .metadata(Token.token, MimeTypeUtils.parseMimeType("message/x.rsocket.authentication.bearer.v0"))
                .metadata(new MetadataHeader("", "test1", 0), MimeType.valueOf("application/x.metadataHeader+json"))
                .data(flux).retrieveFlux(String.class).subscribe(item -> log.info(item));
    }
}
