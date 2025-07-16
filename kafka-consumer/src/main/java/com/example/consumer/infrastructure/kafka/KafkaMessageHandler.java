package com.example.consumer.infrastructure.kafka;

import com.example.consumer.core.domain.MessageProcessor;
import com.example.consumer.infrastructure.input.MessageInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 15-07-2025 at 23:26:18
 * File: KafkaMessageHandler.java
 */

@Component
@RequiredArgsConstructor
public class KafkaMessageHandler implements MessageInputPort {
    
    private final MessageProcessor messageProcessor;

    @Override
    public void process(String message) {
        messageProcessor.handleMessage(message);
    }
}
