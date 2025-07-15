package com.example.producer.service;

import com.example.producer.port.MessageProducerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
        
        return kafkaTemplate.send(record)
                .doOnSuccess(result -> {
                    if (result != null && result.recordMetadata() != null) {
                        log.info("Mensaje enviado con éxito a Kafka. Topic: {}, Key: {}, Partition: {}, Offset: {}", 
                                topic, key, result.recordMetadata().partition(), result.recordMetadata().offset());
                    } else {
                        log.info("Mensaje enviado a Kafka (sin metadata disponible). Topic: {}, Key: {}", topic, key);
                    }
                })
                .doOnError(e -> log.error("Error al enviar mensaje a Kafka. Topic: {}, Key: {}", topic, key, e))
                .then();
    }
}
