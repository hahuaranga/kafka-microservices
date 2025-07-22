package com.example.consumer.config;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
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
 * File: OpenSearchAdapter.java
 */

@Configuration
@RequiredArgsConstructor
public class OpenSearchConfig {

	private final OpenSearchProperties properties;
	
    @Bean
    OpenSearchClient openSearchClient() throws Exception {
        
    	// Configuración SSL
        if (properties.getTruststorePath() != null && !properties.getTruststorePath().isEmpty()) {
            System.setProperty("javax.net.ssl.trustStore", properties.getTruststorePath());
            System.setProperty("javax.net.ssl.trustStorePassword", properties.getTruststorePassword());
        }

        final HttpHost host = getHostByUrl(properties.getUrl());
        
        // Configuración de timeouts
        final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.of(properties.getConnectionTimeout()))
            .setResponseTimeout(Timeout.of(properties.getSocketTimeout()))
            .build();

        // Configuración de credenciales
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            new AuthScope(host),
            new UsernamePasswordCredentials(
                properties.getUsername(), 
                properties.getPassword().toCharArray()
            )
        );

        // Configuración SSL/TLS
        final SSLContext sslContext = SSLContextBuilder
            .create()
            .loadTrustMaterial(null, (chains, authType) -> true)
            .build();

        // Construcción del cliente
        final ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder.builder(host);
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            final TlsStrategy tlsStrategy = ClientTlsStrategyBuilder.create()
                .setSslContext(sslContext)
                .build();

            final PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder
                .create()
                .setTlsStrategy(tlsStrategy)
                .build();

            return httpClientBuilder
                .setDefaultCredentialsProvider(credentialsProvider)
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig);
        });

        final OpenSearchTransport transport = builder.build();
        return new OpenSearchClient(transport);
    }

	private HttpHost getHostByUrl(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return new HttpHost(
            uri.getScheme(),
            uri.getHost(),
            uri.getPort()
        );
	}
}