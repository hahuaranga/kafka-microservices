package com.example.consumer.port;

import reactor.core.publisher.Flux;
/**
 * Author: hahuaranga@indracompany.com
 * Created on: 14-07-2025 at 22:21:32
 * File: MessageConsumerPort.java
 */
public interface MessageConsumerPort {
    Flux<String> consumeMessages();
}
