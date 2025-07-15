package com.example.consumer.controller;

import com.example.consumer.port.MessageConsumerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
/**
 * Author: hahuaranga@indracompany.com
 * Created on: 14-07-2025 at 22:34:47
 * File: MessageController.java
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageConsumerPort messageConsumer;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamMessages() {
        return messageConsumer.consumeMessages();
    }
}
