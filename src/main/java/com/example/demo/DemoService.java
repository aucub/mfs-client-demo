package com.example.demo;

import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DemoService {

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
    public void pu(){
        UsernamePasswordMetadata usernamePasswordMetadata = new UsernamePasswordMetadata("test2", "test2");
        RSocketRequester.builder().tcp("127.0.0.1", 9898)
                .route("connect")
                .metadata(usernamePasswordMetadata, MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                .data("ssssssssssssssss").send();

        /*rSocketRequester
                .route("publish")
                .metadata(usernamePasswordMetadata, MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                .send();
                *//*.data("Hello RSocket!")
                .retrieveMono(String.class)
                .subscribe(response -> log.info(response));*/
    }

}
