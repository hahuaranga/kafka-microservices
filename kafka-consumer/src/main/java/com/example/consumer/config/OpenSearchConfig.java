package com.example.consumer.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
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
import org.apache.http.ssl.SSLContextBuilder;

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
    OpenSearchClient openSearchClient() throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        log.info("Configurando cliente OpenSearch para {}", properties.getUrl());  	
        
        RestClient restClient = getRestClient(
        		getHttpHost(), 							// Parsear URL
        		getCredentialProvider(getHttpHost()), 	// Configurar autenticación básica
        		getRequestConfig(), 					// Configurar timeouts
        		getSslContext()							// Construir cliente REST con configuración completa y aceptar cualquier certificado
        		);

        // Crear cliente OpenSearch
        return new OpenSearchClient(
            new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }

	/**
	 * @param host
	 * @param credentialsProvider
	 * @param requestConfig
	 * @param sslContext
	 * @return
	 */
	private RestClient getRestClient(HttpHost host, BasicCredentialsProvider credentialsProvider,
			RequestConfig requestConfig, SSLContext sslContext) {
		RestClient restClient = RestClient.builder(host)
            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setDefaultRequestConfig(requestConfig)
                        .setMaxConnPerRoute(properties.getMaxConnectionsPerRoute())
                        .setMaxConnTotal(properties.getMaxConnectionsTotal())
                        .setSSLContext(sslContext)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                }
            })
            .build();
		return restClient;
	}

	/**
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 */
	private SSLContext getSslContext() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial((chain, authType) -> true) // Acepta cualquier certificado (¡Cuidado en producción!)
                .build();
		return sslContext;
	}

	/**
	 * @return
	 */
	private RequestConfig getRequestConfig() {
		RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout((int) properties.getConnectionTimeout().toMillis())
                .setSocketTimeout((int) properties.getSocketTimeout().toMillis())
                .setConnectionRequestTimeout((int) properties.getConnectionRequestTimeout().toMillis())
                .build();
		return requestConfig;
	}

	/**
	 * @param host
	 * @return
	 */
	private BasicCredentialsProvider getCredentialProvider(HttpHost host) {
		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            new AuthScope(host),
            new UsernamePasswordCredentials(
                properties.getUsername(),
                properties.getPassword()
            )
        );
		return credentialsProvider;
	}

	/**
	 * @return
	 * @throws URISyntaxException
	 */
	private HttpHost getHttpHost() throws URISyntaxException {
		URI uri = new URI(properties.getUrl());
        HttpHost host = new HttpHost(
            uri.getHost(),
            uri.getPort(),
            uri.getScheme()
        );
		return host;
	}
	  
}