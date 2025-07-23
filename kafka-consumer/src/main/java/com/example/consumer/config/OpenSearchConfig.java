package com.example.consumer.config;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 22-07-2025 at 09:39:55
 * File: OpenSearchConfig.java
 */

@Configuration
@RequiredArgsConstructor
public class OpenSearchConfig {

    private final OpenSearchProperties properties;

    @Bean
    OpenSearchClient openSearchClient() throws Exception {
        // 1. Configuración SSL
        configureSSL();

        // 2. Obtener host desde URL
        final HttpHost host = getHostByUrl(properties.getUrl());

        // 3. Configurar el cliente HTTP
        final OpenSearchTransport transport = ApacheHttpClient5TransportBuilder.builder(host)
        	    .setHttpClientConfigCallback(httpClientBuilder -> {
        	        try {
        	            return configureHttpClient(httpClientBuilder);
        	        } catch (Exception e) {
        	            throw new RuntimeException("Failed to configure HTTP client", e);
        	        }
        	    })
        	    .build();

        return new OpenSearchClient(transport);
    }

    private void configureSSL() {
        if (properties.getTruststorePath() != null && !properties.getTruststorePath().isEmpty()) {
            System.setProperty("javax.net.ssl.trustStore", properties.getTruststorePath());
            System.setProperty("javax.net.ssl.trustStorePassword", properties.getTruststorePassword());
        }
    }

    private HttpAsyncClientBuilder configureHttpClient(HttpAsyncClientBuilder httpClientBuilder) throws Exception {
        // Configuración de credenciales
        if ("basicauth".equalsIgnoreCase(properties.getAuthType())) {
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                new AuthScope(null, -1), // AuthScope amplio
                new UsernamePasswordCredentials(
                    properties.getUsername(), 
                    properties.getPassword().toCharArray()
                )
            );
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }

        // Configuración TLS (sin HTTP/2)
        SSLContext sslContext = SSLContextBuilder.create()
            .loadTrustMaterial(null, (chains, authType) -> true)
            .build();

        TlsStrategy tlsStrategy = ClientTlsStrategyBuilder.create()
            .setSslContext(sslContext)
            // Forzar TLSv1.2 (compatible con OpenSearch)
            .setTlsVersions(new String[]{"TLSv1.2"}) 
            .build();

        // Configuración del connection manager
        PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder.create()
            .setTlsStrategy(tlsStrategy)
            .build();

        // Configuración de timeouts
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.of(properties.getConnectionTimeout()))
            .setResponseTimeout(Timeout.of(properties.getSocketTimeout()))
            .build();

        return httpClientBuilder
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            // Deshabilitar características avanzadas que podrían usar HTTP/2
            .disableCookieManagement()
            .disableAuthCaching()
            .disableConnectionState();
    }

    private HttpHost getHostByUrl(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return new HttpHost(
            uri.getScheme(),
            uri.getHost(),
            uri.getPort() != -1 ? uri.getPort() : 
                ("https".equals(uri.getScheme()) ? 443 : 80)
        );
    }
}