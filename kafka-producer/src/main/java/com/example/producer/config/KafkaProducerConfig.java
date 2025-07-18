package com.example.producer.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 17-07-2025 at 16:55:15
 * File: KafkaProducerConfig.java
 */

@Configuration
@EnableConfigurationProperties(KafkaConfigProperties.class)
public class KafkaProducerConfig {

    @Bean
    SenderOptions<String, String> senderOptions(KafkaConfigProperties properties) {
        Map<String, Object> props = new HashMap<>();
        
        // Configuración básica
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.producer().bootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        // Configuración de performance
        props.put(ProducerConfig.LINGER_MS_CONFIG, properties.producer().lingerMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, properties.producer().batchSize());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, properties.producer().bufferMemory());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, properties.producer().compressionType());
        props.put(ProducerConfig.ACKS_CONFIG, properties.producer().acks());
        props.put(ProducerConfig.RETRIES_CONFIG, properties.producer().retries());
        
        // Configuración de seguridad
        if (properties.security() != null) {
            props.put("security.protocol", properties.security().protocol());
            props.put(SaslConfigs.SASL_MECHANISM, properties.security().mechanism());
            props.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
                "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";",
                properties.security().username(),
                properties.security().password()
            ));
        }
        
        // Configuración adicional para headers
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, 
            "org.springframework.kafka.support.LoggingProducerInterceptor");        
        
        return SenderOptions.create(props);
    }

    @Bean
    ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate(
        SenderOptions<String, String> senderOptions) {
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }
}
