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
    private int maxConnectionsPerRoute = 10;
    private int maxConnectionsTotal = 30;
    
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration connectionTimeout = Duration.ofSeconds(5);
    
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration socketTimeout = Duration.ofSeconds(10);
    
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration connectionRequestTimeout = Duration.ofSeconds(5);

}