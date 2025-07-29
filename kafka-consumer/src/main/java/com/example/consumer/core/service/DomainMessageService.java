package com.example.consumer.core.service;

import com.example.consumer.core.domain.MessageProcessor;
import com.example.consumer.core.port.OpenSearchOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.opensearch.client.opensearch.core.IndexResponse;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 15-07-2025 at 23:21:26
 * File: DomainMessageService.java
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainMessageService implements MessageProcessor {
    
	private final OpenSearchOutputPort openSearchOutputPort;
	
    @Override
    public void syncHandleMessage(String message) {
        // Aquí va tu lógica de negocio principal
    	log.info("Processing message: {}", message);
        // Puede incluir:
        // - Validación
        // - Transformación
        // - Llamadas a otros servicios
        // - Persistencia
    	
    	// Integracion con OpenSearch
        openSearchOutputPort.createIndexIfNotExists();
        openSearchOutputPort.indexSync(message);
    }
    
    @Override
    public void syncHandleMessageWithMetadata(String message, Map<String, String> headers) {
        log.info("Processing message with headers from topic. Payload: {}, Headers: {}", message, headers);
        // Lógica que usa metadatos
        
        // Integracion con OpenSearch
        openSearchOutputPort.createIndexIfNotExists();
        openSearchOutputPort.indexSync(message, headers);
    }

	@Override
	public CompletableFuture<IndexResponse> asyncHandleMessage(String message) {
		log.info("Processing message: {}", message);
        openSearchOutputPort.createIndexIfNotExists();
        return openSearchOutputPort.indexAsync(message);
		
	}

	@Override
	public CompletableFuture<IndexResponse> asyncHandleMessageWithMetadata(String message, Map<String, String> headers) {
		log.info("Processing message: {}", message);
        openSearchOutputPort.createIndexIfNotExists();
        return openSearchOutputPort.indexAsync(message, headers);
	}    
}
