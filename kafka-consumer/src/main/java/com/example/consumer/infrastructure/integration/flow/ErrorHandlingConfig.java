package com.example.consumer.infrastructure.integration.flow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageChannel;
import com.example.consumer.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 28-07-2025 at 11:44:36
 * File: ErrorHandlingConfig.java
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ErrorHandlingConfig {
	
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    private final KafkaProperties kafkaProperties;

	@Bean
    MessageChannel errorChannel() {
        return new DirectChannel();
    }

    @Bean
    IntegrationFlow errorFlow() {
        return IntegrationFlow.from(errorChannel())
                .handle(String.class, (payload, headers) -> {
                    // Log del error
                    log.error("Enviando a DLQ: {}", payload);
                    return payload;
                })
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate)
                        .topic(kafkaProperties.getConsumer().getDlqTopicName()))
                .get();
    }
    
}
