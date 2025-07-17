package com.example.consumer.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 15-07-2025 at 23:24:32
 * File: KafkaConsumer.java
 */

@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    
    private final KafkaMessageHandler handler;
    
    private static final String TOPIC_EXPRESSION = "#{@kafkaProperties.topicName()}";

    @KafkaListener(topics = TOPIC_EXPRESSION)
    public void consume(Message<String> message) {
        // Procesamiento básico sin headers
        handler.process(message.getPayload());
    }
    
    @KafkaListener(topics = TOPIC_EXPRESSION)
    public void consumeWithHeaders(Message<String> message) {
        Map<String, String> headersMap = extractHeaders(message.getHeaders());
        handler.processWithMetadata(message.getPayload(), headersMap);
    }

    private Map<String, String> extractHeaders(MessageHeaders headers) {
        Map<String, String> headersMap = new HashMap<>();
        
        headers.forEach((key, value) -> {
            // Filtramos solo headers de Kafka (opcional)
            if (key.startsWith("kafka_")) {
                if (value instanceof byte[]) {
                    headersMap.put(key, new String((byte[]) value, StandardCharsets.UTF_8));
                } else {
                    headersMap.put(key, value.toString());
                }
            }
        });
        
        return headersMap;
    }
}