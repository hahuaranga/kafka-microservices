package com.example.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Author: hahuaranga@indracompany.com Created on: 15-07-2025 at 23:11:06 File:
 * KafkaProperties.java
 */

@Validated
@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
    String bootstrapServers,
    String topicName,       // Nuevo campo para el topic
    String groupId,
    String autoOffsetReset,
    SecurityProperties security
) {
    public record SecurityProperties(
        String protocol,
        String mechanism,
        String username,
        String password
    ) {}
}