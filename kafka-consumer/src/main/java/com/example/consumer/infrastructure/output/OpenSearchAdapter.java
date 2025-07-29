package com.example.consumer.infrastructure.output;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.core.task.TaskExecutor;
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
    
    private final TaskExecutor openSearchExecutor;

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
    public void indexSync(String message, Map<String, String> headers) {
    	log.debug("Processing indexSync with metadata ...");
        try {
            Map<String, String> doc = createDocument(message);
            doc.putAll(headers);           // agrega los headers al documento

            IndexRequest<Map<String, String>> request = new IndexRequest.Builder<Map<String, String>>()
                    .index(properties.getIndexName())
                    .document(doc)
                    .build();

            IndexResponse response = client.index(request);
            log.info("Document (with headers) indexed with id: {}", response.id());
        } catch (IOException | OpenSearchException e) {
            throw new RuntimeException("Failed to index document with headers synchronously", e);
        }    	
    }
    
    @Override
    public CompletableFuture<IndexResponse> indexAsync(String message) {
        log.debug("Processing indexAsync ...");

        return CompletableFuture.supplyAsync(() -> {
            try {
                IndexRequest<Map<String, String>> request = new IndexRequest.Builder<Map<String, String>>()
                    .index(properties.getIndexName())
                    .document(createDocument(message))
                    .build();

                IndexResponse response = client.index(request);
                log.info("Document indexed async with id: {}", response.id());
                return response;
            } catch (IOException | OpenSearchException e) {
                log.error("Failed to index document asynchronously: {}", e.getMessage());
                throw new RuntimeException("Async index failed", e);
            }
        }, openSearchExecutor);
    }
    
    @Override
    public CompletableFuture<IndexResponse> indexAsync(String message, Map<String, String> headers) {
    	log.debug("Processing indexAsync with metadata ...");
        
    	return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, String> doc = createDocument(message);
                doc.putAll(headers);       // agrega los headers al documento

                IndexRequest<Map<String, String>> request = new IndexRequest.Builder<Map<String, String>>()
                        .index(properties.getIndexName())
                        .document(doc)
                        .build();

                IndexResponse response = client.index(request);
                log.info("Document (with headers) indexed async with id: {}", response.id());
                return response;
            } catch (IOException | OpenSearchException e) {
                log.error("Failed to index document with headers asynchronously: {}", e.getMessage());
                throw new RuntimeException("Async index with headers failed", e);
            }
        }, openSearchExecutor);

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
