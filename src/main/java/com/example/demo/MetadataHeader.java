package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataHeader {
    private String userId;
    private String exchange;
    private String routingKey;
    private String queueType;
    private int batchSize;
}
