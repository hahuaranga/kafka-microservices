package com.example.producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 17-07-2025 at 16:53:23
 * File: KafkaConfigProperties.java
 */

@Validated
@ConfigurationProperties(prefix = "kafka")
public record KafkaConfigProperties(
    String topic,
    Producer producer,
    Security security
) {
    public record Producer(
        String bootstrapServers,
        String keySerializer,
        String valueSerializer,
        int lingerMs,
        int batchSize,
        int bufferMemory,
        String compressionType,
        String acks,
        int retries
    ) {}

    public record Security(
        String protocol,
        String mechanism,
        String username,
        String password
    ) {}
}
