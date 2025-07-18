package com.example.producer.controller;

import com.example.producer.config.KafkaConfigProperties;
import com.example.producer.dto.MessageRequest;
import com.example.producer.port.MessageProducerPort;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final KafkaConfigProperties kafkaProperties;

    @PostMapping
    public Mono<ResponseEntity<Void>> sendMessage(@RequestBody MessageRequest request) {
        // Ejemplo con headers
        Map<String, String> headers = Map.of(
            "event-type", "user-created",
            "trace-id", UUID.randomUUID().toString(),
            "content-type", "application/json"
        );
        
        return messageProducer.sendMessageWithHeaders(
            kafkaProperties.topic(),
            request.key(),
            request.message(),
            headers
        ).thenReturn(ResponseEntity.accepted().build());
    }
    
    @PostMapping("/simple")
    public Mono<ResponseEntity<Void>> sendSimpleMessage(
        @RequestBody String message,
        @RequestParam(required = false) String key) {
        
        // Usar "default-key" si no se proporciona key
        String messageKey = (key != null) ? key : "default-key";
        
        return messageProducer.sendMessage(
            kafkaProperties.topic(),
            messageKey,
            message
        ).thenReturn(ResponseEntity.accepted().build());
    }    
}
