package com.example.consumer.core.domain;

import java.util.Map;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 15-07-2025 at 23:19:44
 * File: MessageProcessor.java
 */
/**
 * Interfaz que define el contrato para procesamiento de mensajes en el dominio.
 * Representa la capacidad del sistema para manejar mensajes entrantes.
 */
public interface MessageProcessor {
    
    /**
     * Procesa un mensaje recibido aplicando la lógica de negocio.
     * @param message El mensaje a procesar en formato String
     */
    void handleMessage(String message);
    
    /**
     * Versión alternativa para procesamiento con metadatos.
     * @param payload El contenido del mensaje
     * @param headers Metadatos asociados al mensaje (opcional)
     */
    default void handleMessageWithMetadata(String payload, Map<String, String> headers) {
        // Implementación por defecto que delega al método básico
        handleMessage(payload);
    }
}
