package com.example.producer.service;

import com.example.producer.port.MessageProducerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 14-07-2025 at 21:52:42
 * File: KafkaMessageProducer.java
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageProducer implements MessageProducerPort {

    private final ReactiveKafkaProducerTemplate<String, String> kafkaTemplate;

    @Override
    public Mono<Void> sendMessage(String topic, String key, String message) {
        return sendMessageWithHeaders(topic, key, message, null);
    }

    @Override
    public Mono<Void> sendMessageWithHeaders(String topic, String key, String message, Map<String, String> headers) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
        
        // Añadir headers si existen
        if (headers != null) {
            headers.forEach((headerKey, headerValue) -> 
                record.headers().add(headerKey, headerValue.getBytes(StandardCharsets.UTF_8))
            );
        }
        
        return kafkaTemplate.send(record)
            .doOnSuccess(result -> logMessageSuccess(topic, key, result))
            .doOnError(e -> log.error("Error sending message to Kafka. Topic: {}, Key: {}", topic, key, e))
            .then();
    }

    private void logMessageSuccess(String topic, String key, SenderResult<Void> result) {
        if (result != null && result.recordMetadata() != null) {
            log.info("Message sent successfully. Topic: {}, Key: {}, Partition: {}, Offset: {}",
                topic, key, result.recordMetadata().partition(), result.recordMetadata().offset());
        } else {
            log.info("Message sent (no metadata available). Topic: {}, Key: {}", topic, key);
        }
    }
}
