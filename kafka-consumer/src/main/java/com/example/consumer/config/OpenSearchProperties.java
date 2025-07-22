package com.example.consumer.config;

import lombok.Data;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 21-07-2025 at 12:15:09
 * File: OpenSearchProperties.java
 */

@Data
@ConfigurationProperties(prefix = "opensearch")
public class OpenSearchProperties {
	private String url;
    private String username;
    private String password;
    private String indexName;
    private String truststorePath;
    private String truststorePassword;
    
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration connectionTimeout = Duration.ofSeconds(30);
    
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration socketTimeout = Duration.ofSeconds(30);    
}