package com.example.consumer.config;

import java.net.URI;
import java.net.URISyntaxException;
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
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
    OpenSearchClient openSearchClient() throws Exception {
    	
    	log.debug("Processing openSearchClient ...");
    	
        // 1. Configuración SSL
    	log.debug("Executing configureSSL ...");
    	configureSSL();

        // 2. Obtener host desde URL
    	log.debug("Executing getHostByUrl ...");
        final HttpHost host = getHostByUrl(properties.getUrl());
        
        // --------------------------------
        
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host), new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword().toCharArray()));
        
        final SSLContext sslcontext = SSLContextBuilder
        	      .create()
        	      .loadTrustMaterial(null, (chains, authType) -> true)
        	      .build();
        
        final ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder.builder(host);
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            // Configuración TLS (sin HTTP/2)
            TlsStrategy tlsStrategy = ClientTlsStrategyBuilder.create()
                .setSslContext(sslcontext)
                // ---> FORZAR HTTP/1.1 <---
                .setTlsVersions("TLSv1.2", "TLSv1.3")  // Evita ALPN (usado en HTTP/2)
                .build();

            PoolingAsyncClientConnectionManager cm = PoolingAsyncClientConnectionManagerBuilder
                .create()
                .setTlsStrategy(tlsStrategy)
                .build();

            // Configuración final del cliente
            return httpClientBuilder
                .setDefaultCredentialsProvider(credentialsProvider)
                .setConnectionManager(cm)
                // ---> DESHABILITAR CARACTERÍSTICAS AVANZADAS <---
                .disableConnectionState()  // Evita negociación de protocolos
                .disableCookieManagement()
                .disableAuthCaching();
        });        
        
//        final ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder.builder(host);
//        builder.setHttpClientConfigCallback(httpClientBuilder -> {
//          final TlsStrategy tlsStrategy = ClientTlsStrategyBuilder.create()
//            .setSslContext(sslcontext)
//            // See https://issues.apache.org/jira/browse/HTTPCLIENT-2219
//            .setTlsDetailsFactory(new Factory<SSLEngine, TlsDetails>() {
//              @Override
//              public TlsDetails create(final SSLEngine sslEngine) {
//                return new TlsDetails(sslEngine.getSession(), sslEngine.getApplicationProtocol());
//              }
//            })
//            .build();
//
//          final PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder
//            .create()
//            .setTlsStrategy(tlsStrategy)
//            .build();
//
//          return httpClientBuilder
//            .setDefaultCredentialsProvider(credentialsProvider)
//            .setConnectionManager(connectionManager);
//        });
        
        final OpenSearchTransport transport = builder.build();
        return new OpenSearchClient(transport);
    }

    private void configureSSL() {
        if (properties.getTruststorePath() != null && !properties.getTruststorePath().isEmpty()) {
            System.setProperty("javax.net.ssl.trustStore", properties.getTruststorePath());
            System.setProperty("javax.net.ssl.trustStorePassword", properties.getTruststorePassword());
        }
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