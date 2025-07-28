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
	private ConsumerProperties consumer;
	private ProducerProperties producer;
	private SecurityProperties security;
    
    @Data
    public static class ConsumerProperties {
        private String bootstrapServers;
        private String topicName = "example-topic";
        private String groupId;
        private String autoOffsetReset;
        private SecurityProperties security;
        private int concurrency = 1;
        private String dlqTopicName = "dlq-topic";
    }
    
    @Data
    public static class ProducerProperties {
    	//private String topicName = "dlq-topic";
    	private String bootstrapServers;
    	private String keySerializer;
    	private String valueSerializer;
    	private int lingerMs;
    	private int batchSize;
    	private int bufferMemory;
    	private String compressionType;
    	private String acks;
    	private int retries;   	
    }

    @Data
    public static class SecurityProperties {
        private String protocol;
        private String mechanism;
        private String username;
        private String password;
    }
}