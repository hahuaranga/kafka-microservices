package com.example.consumer.infrastructure.kafka;

import com.example.consumer.core.domain.MessageProcessor;
import com.example.consumer.infrastructure.input.MessageInputPort;
import lombok.RequiredArgsConstructor;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 15-07-2025 at 23:26:18
 * File: KafkaMessageHandler.java
 */

@Deprecated
@Profile("legacy")
@Component
@RequiredArgsConstructor
public class KafkaMessageHandler implements MessageInputPort {
    
    private final MessageProcessor messageProcessor;

    @Override
    public void process(String message) {
        messageProcessor.syncHandleMessage(message);
    }
    
    public void processWithMetadata(String payload, Map<String, String> headers) {
    	messageProcessor.syncHandleMessageWithMetadata(payload, headers);
    }
    
}
