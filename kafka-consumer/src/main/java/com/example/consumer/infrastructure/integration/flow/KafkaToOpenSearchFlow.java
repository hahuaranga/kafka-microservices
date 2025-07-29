package com.example.consumer.infrastructure.integration.flow;

import com.example.consumer.core.domain.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 27-07-2025 at 22:53:03
 * File: KafkaToOpenSearchFlow.java
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaToOpenSearchFlow {
    
    private final @Qualifier("kafkaInputChannelSync") MessageChannel kafkaInputChannelSync;
    
    private final @Qualifier("kafkaInputChannelAsync") MessageChannel kafkaInputChannelAsync;
    
    private final MessageProcessor messageProcessor;
        
    @Bean
    IntegrationFlow openSearchIndexingSyncFlow() {
    	log.debug("");
        return IntegrationFlow.from(kafkaInputChannelSync)
                .handle(String.class, (payload, headers) -> {
                    try {
						Map<String, String> headersMap = headers.entrySet().stream()
						        .collect(Collectors.toMap(
						            Map.Entry::getKey,
						            e -> e.getValue() != null ? e.getValue().toString() : null
						        ));
						messageProcessor.syncHandleMessageWithMetadata(payload, headersMap); 
						return null;
					} catch (Exception e) {
	                    log.error("Error al indexar sync: {}", e.getMessage());
	                    throw new RuntimeException("Falló indexación sync", e);
					}
                })
                .channel("errorChannel") // Redirige al canal de errores
                .get();
    }
    
    @Bean
    IntegrationFlow openSearchIndexingAsyncFlow() {
        return IntegrationFlow.from(kafkaInputChannelAsync)
                .transform(String.class, payload -> payload) // payload es String
                .handle(String.class, (payload, headers) -> {
                    Map<String, String> headersMap = headers.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> e.getValue() != null ? e.getValue().toString() : null
                            ));

                    return Mono.fromCompletionStage(
                            messageProcessor.asyncHandleMessageWithMetadata(payload, headersMap)
                    ).onErrorMap(ex -> new RuntimeException("Falló indexación async", ex));
                })
                .channel("errorChannel")
                .get();
    }  
    
}
