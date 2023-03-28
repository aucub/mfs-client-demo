package com.example.demo;


import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

public class DemoService {
    public void pageList() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .defaultHeader(HttpHeaders.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)")
                .build();
        Mono<String> mono = webClient
                .post()
                .uri("/user/pageList")  // 请求路径
                .bodyValue(new FindPageDto("",10,1))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); //响应数据类型转换
        System.out.println(mono.block());
    }
}
