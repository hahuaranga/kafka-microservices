package com.example.consumer.core.port;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.opensearch.client.opensearch.core.IndexResponse;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 22-07-2025 at 09:36:13
 * File: OpenSearchOutputPort.java
 */

public interface OpenSearchOutputPort {

    void indexSync(String message);
    
	void indexSync(String payload, Map<String, String> headers);
	
	CompletableFuture<IndexResponse> indexAsync(String message);
    
	CompletableFuture<IndexResponse> indexAsync(String message, Map<String, String> headers);
    
    void createIndexIfNotExists();

    
}
