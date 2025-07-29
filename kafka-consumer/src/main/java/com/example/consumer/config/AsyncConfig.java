package com.example.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 28-07-2025 at 19:41:01
 * File: AsyncConfig.java
 */

@Configuration
public class AsyncConfig {

    @Bean
    TaskExecutor openSearchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("opensearch-async-");
        executor.initialize();
        return executor;
    }
    
}
