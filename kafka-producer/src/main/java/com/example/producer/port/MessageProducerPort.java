package com.example.producer.port;

import java.util.Map;

import reactor.core.publisher.Mono;
/**
 * Author: hahuaranga@indracompany.com
 * Created on: 14-07-2025 at 21:51:34
 * File: MessageProducerPort.java
 */
public interface MessageProducerPort {
    Mono<Void> sendMessage(String topic, String key, String message);
    Mono<Void> sendMessageWithHeaders(String topic, String key, String message, Map<String, String> headers);
}
