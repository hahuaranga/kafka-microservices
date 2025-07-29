package com.example.consumer.infrastructure.integration.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.messaging.MessageChannel;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 27-07-2025 at 22:50:26
 * File: IntegrationConfig.java
 */

@Configuration
public class IntegrationConfig {
	
	@Bean
	MessageChannel kafkaInputChannelSync() {
	    return new DirectChannel();
	}	
	
	@Bean
	MessageChannel kafkaInputChannelAsync() {
	    return new DirectChannel();
	}
	
    @Bean
    IntegrationFlow kafkaInboundFlow(
            @Qualifier("kafkaMessageListenerContainer") ConcurrentMessageListenerContainer<String, String> container,
            @Qualifier("kafkaInputChannelAsync") MessageChannel inputChannel) {
        return IntegrationFlow
                .from(Kafka
                        .messageDrivenChannelAdapter(container)
                        .outputChannel(inputChannel))
                .channel(inputChannel)
                .get();
    }    
}
