package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.ZoneOffset;

@SpringBootTest
public class RSocketTests {
    @Autowired
    RSocketService rSocketService;

    @Autowired
    DemoService demoService;

    @Test
    void connect() {
        rSocketService.connect();
    }

    @Test
    void test1() {
        demoService.pu();
    }

    @Test
    void test2() {
        System.out.println(Instant.now().atOffset(ZoneOffset.UTC));
    }
}
