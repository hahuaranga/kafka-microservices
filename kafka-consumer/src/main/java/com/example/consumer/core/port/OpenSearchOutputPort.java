package com.example.consumer.core.port;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 22-07-2025 at 09:36:13
 * File: OpenSearchOutputPort.java
 */

public interface OpenSearchOutputPort {

    void indexSync(String message);
    void indexAsync(String message);
    void createIndexIfNotExists();
    
}
