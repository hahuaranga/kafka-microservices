package com.example.consumer.infrastructure.integration.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.messaging.MessageChannel;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 27-07-2025 at 22:50:26
 * File: IntegrationConfig.java
 */

@Configuration
public class IntegrationConfig {
	
	@Bean
	MessageChannel kafkaInputChannel() {
	    return new DirectChannel();
	}	
	
    @Bean
    IntegrationFlow kafkaInboundFlow(
            @Qualifier("kafkaMessageListenerContainer") KafkaMessageListenerContainer<String, String> container,
            @Qualifier("kafkaInputChannel") MessageChannel inputChannel) {
        return IntegrationFlow
                .from(org.springframework.integration.kafka.dsl.Kafka
                        .messageDrivenChannelAdapter(container)
                        .outputChannel(inputChannel))
                .channel(inputChannel)
                .get();
    }    
}
