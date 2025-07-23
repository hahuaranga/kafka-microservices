package com.example.consumer.config;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 22-07-2025 at 09:39:55
 * File: OpenSearchConfig.java
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OpenSearchConfig {

	private final OpenSearchProperties properties;
	
    @Bean
    OpenSearchClient openSearchClient() throws URISyntaxException {
        log.info("Configurando cliente OpenSearch para {}", properties.getUrl());

        // 1. Parsear URL
        URI uri = new URI(properties.getUrl());
        HttpHost host = new HttpHost(
            uri.getHost(),
            uri.getPort(),
            uri.getScheme()
        );

        // 2. Configurar autenticación básica
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            new AuthScope(host),
            new UsernamePasswordCredentials(
                properties.getUsername(),
                properties.getPassword()
            )
        );

        // 3. Configurar timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout((int) properties.getConnectionTimeout().toMillis())
                .setSocketTimeout((int) properties.getSocketTimeout().toMillis())
                .setConnectionRequestTimeout((int) properties.getConnectionRequestTimeout().toMillis())
                .build();

        // 4. Construir cliente REST con configuración completa
        RestClient restClient = RestClient.builder(host)
            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setDefaultRequestConfig(requestConfig)
                        .setMaxConnPerRoute(properties.getMaxConnectionsPerRoute())
                        .setMaxConnTotal(properties.getMaxConnectionsTotal());
                }
            })
            .build();

        // 5. Crear cliente OpenSearch
        return new OpenSearchClient(
            new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }
	
    
}