package com.example.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import lombok.Data;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 15-07-2025 at 23:19:44
 * File: KafkaProperties.java
 */

@Data
@Validated
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private String topicName;
    private String groupId;
    private String autoOffsetReset;
    private SecurityProperties security;
    private int concurrency = 1;

    @Data
    public static class SecurityProperties {
        private String protocol;
        private String mechanism;
        private String username;
        private String password;
    }
}