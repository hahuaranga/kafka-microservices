package com.example.consumer.core.service;

import com.example.consumer.core.domain.MessageProcessor;
import com.example.consumer.core.port.OpenSearchOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

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
    public void handleMessage(String message) {
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
    public void handleMessageWithMetadata(String payload, Map<String, String> headers) {
        log.info("Processing message with headers from topic. Payload: {}, Headers: {}", payload, headers);
        // Lógica que usa metadatos
        
        // Integracion con OpenSearch
        openSearchOutputPort.createIndexIfNotExists();
        openSearchOutputPort.indexAsync(payload);
    }    
}
