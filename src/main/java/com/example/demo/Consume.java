package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Consume {
    private String queueType;
    private String queue;
    private long offset;
    private long timestamp;
    private Boolean manual;
    private int batchSize;

}

