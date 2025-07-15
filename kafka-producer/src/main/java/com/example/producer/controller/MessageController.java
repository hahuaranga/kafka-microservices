package com.example.producer.controller;

import com.example.producer.dto.MessageRequest;
import com.example.producer.port.MessageProducerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
/**
 * Author: hahuaranga@indracompany.com
 * Created on: 14-07-2025 at 22:06:54
 * File: MessageController.java
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageProducerPort messageProducer;
    
    @Value("${app.kafka.topic}")
    private String topic;

    @PostMapping
    public Mono<ResponseEntity<Void>> sendMessage(@RequestBody MessageRequest request) {
        return messageProducer.sendMessage(topic, request.key(), request.message())
                .thenReturn(ResponseEntity.accepted().build());
    }
}
