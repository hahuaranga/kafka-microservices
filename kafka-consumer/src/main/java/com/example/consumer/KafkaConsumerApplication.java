package com.example.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 14-07-2025 at 22:35:40
 * File: KafkaConsumerApplication.java
 */

@SpringBootApplication
@ConfigurationPropertiesScan
public class KafkaConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerApplication.class, args);
    }
}
