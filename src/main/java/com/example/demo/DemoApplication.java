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
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        int threadSize = 2;
        for (int i = 0; i < threadSize; i++) {
            Thread.startVirtualThread(() -> {
                        executor.submit(new Publish());
                    }
            );
        }
    }

}
