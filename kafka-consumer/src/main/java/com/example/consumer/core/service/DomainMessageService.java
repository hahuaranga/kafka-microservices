package com.example.consumer.core.service;

import com.example.consumer.core.domain.MessageProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 15-07-2025 at 23:21:26
 * File: DomainMessageService.java
 */

@Slf4j
@Service
public class DomainMessageService implements MessageProcessor {
    
    @Override
    public void handleMessage(String message) {
        // Aquí va tu lógica de negocio principal
    	log.info("Processing message: {}", message);
        // Puede incluir:
        // - Validación
        // - Transformación
        // - Llamadas a otros servicios
        // - Persistencia
    }
    
    @Override
    public void handleMessageWithMetadata(String payload, Map<String, String> headers) {
        log.info("Processing message with headers from topic. Payload: {}, Headers: {}", payload, headers);
        // Lógica que usa metadatos
    }    
}
