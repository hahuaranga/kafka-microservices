package com.example.consumer.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 15-07-2025 at 23:24:32
 * File: KafkaConsumer.java
 */

@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    
    private final KafkaMessageHandler handler;

    @KafkaListener(topics = "${kafka.topic.name}")
    public void consume(String message) {
        handler.process(message);
    }
}
