package com.example.consumer.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 15-07-2025 at 23:01:16
 * File: KafkaConfig.java
 */

@Configuration
//@EnableConfigurationProperties(KafkaProperties.class)
@RequiredArgsConstructor
public class KafkaConfig {

	private final KafkaProperties kafkaProperties;
	
    @Bean
    ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Basic configuration
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getAutoOffsetReset());
        
        // Deserializers with error handling
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, StringDeserializer.class);
        
        // Security configuration
        if (kafkaProperties.getSecurity() != null) {
            configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getSecurity().getProtocol());
            configProps.put(SaslConfigs.SASL_MECHANISM, kafkaProperties.getSecurity().getMechanism());
            configProps.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
                "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";",
                kafkaProperties.getSecurity().getUsername(),
                kafkaProperties.getSecurity().getPassword()
            ));
        }
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        ConsumerFactory<String, String> consumerFactory) {
        
        ConcurrentKafkaListenerContainerFactory<String, String> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        
        // Additional configurations
        factory.setConcurrency(3); // Adjust as needed
        factory.getContainerProperties().setMissingTopicsFatal(false);
        
        return factory;
    }
    
    // Solo para validacion
    @Bean
    NewTopic applicationTopic(KafkaProperties properties) {
        return TopicBuilder.name(kafkaProperties.getTopicName())
                          .partitions(3)
                          .replicas(2)
                          .config(TopicConfig.RETENTION_MS_CONFIG, "604800000") // 7 días
                          .build();
    }    
}
