package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RSocketTests {
    @Autowired
    RSocketService rSocketService;

    @Autowired
    DemoService demoService;

    @Test
    void connect() {
        rSocketService.connect("test77");
    }

    @Test
    void test1() {
        demoService.pu();
    }
}
