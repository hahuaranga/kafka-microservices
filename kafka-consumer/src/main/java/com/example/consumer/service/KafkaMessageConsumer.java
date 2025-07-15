package com.example.consumer.service;

import com.example.consumer.port.MessageConsumerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
/**
 * Author: hahuaranga@indracompany.com
 * Created on: 14-07-2025 at 22:26:14
 * File: KafkaMessageConsumer.java
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageConsumer implements MessageConsumerPort {

    private final Sinks.Many<String> messageSink = Sinks.many().multicast().onBackpressureBuffer();

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        log.info("Mensaje recibido: {}", message);
        messageSink.tryEmitNext(message);
    }

    @Override
    public Flux<String> consumeMessages() {
        return messageSink.asFlux();
    }
}
