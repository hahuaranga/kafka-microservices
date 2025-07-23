package com.example.consumer.infrastructure.output;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.stereotype.Component;
import com.example.consumer.config.OpenSearchProperties;
import com.example.consumer.core.port.OpenSearchOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 22-07-2025 at 09:39:55
 * File: OpenSearchAdapter.java
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenSearchAdapter implements OpenSearchOutputPort {

    private final OpenSearchClient client;
    
    private final OpenSearchProperties properties;

    @Override
    public void indexSync(String message) {
    	log.debug("Processing indexSync ...");
        try {
            IndexRequest<Map<String, String>> request = new IndexRequest.Builder<Map<String, String>>()
                .index(properties.getIndexName())
                .document(createDocument(message))
                .build();

            IndexResponse response = client.index(request);
            log.info("Document indexed with id: " + response.id());
        } catch (IOException | OpenSearchException e) {
            throw new RuntimeException("Failed to index document synchronously", e);
        }
    }
    
    @Override
    public void indexAsync(String message) {
    	log.debug("Processing indexAsync ...");
        CompletableFuture.runAsync(() -> {
            try {
                IndexRequest<Map<String, String>> request = new IndexRequest.Builder<Map<String, String>>()
                    .index(properties.getIndexName())
                    .document(createDocument(message))
                    .build();

                IndexResponse response = client.index(request);
                log.info("Document indexed async with id: " + response.id());
            } catch (IOException | OpenSearchException e) {
            	log.error("Failed to index document asynchronously: " + e.getMessage());
            }
        });
    }
    
    @Override
    public void createIndexIfNotExists() {
    	log.debug("Processing createIndexIfNotExists ...");
        try {
            boolean exists = client.indices().exists(new ExistsRequest.Builder()
                .index(properties.getIndexName())
                .build()).value();

            if (!exists) {
                client.indices().create(new CreateIndexRequest.Builder()
                    .index(properties.getIndexName())
                    .build());
                log.info("Index created: " + properties.getIndexName());
            }
        } catch (IOException | OpenSearchException e) {
        	log.error("Failed to create index {} after retries", properties.getIndexName(), e);
            throw new RuntimeException("Failed to check/create index", e);
        }
    }
    
    private Map<String, String> createDocument(String message) {
        Map<String, String> document = new HashMap<>();
        document.put("message", message);
        document.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return document;
    }
    
}
