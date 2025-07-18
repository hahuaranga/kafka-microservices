package com.example.consumer.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 17-07-2025 at 16:55:15
 * File: KafkaConsumer.java
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

	private final KafkaMessageHandler handler;

	private static final String TOPIC_EXPRESSION = "#{@kafkaProperties.topicName()}";

	@KafkaListener(topics = TOPIC_EXPRESSION)
	public void consume(Message<String> message) {
		
		// Procesamiento básico sin headers
		try {
			handler.process(message.getPayload());
			log.debug("Processed message without headers");
		} catch (Exception e) {
			log.error("Error processing basic message: {}", e.getMessage());
		}
	}

	@KafkaListener(topics = TOPIC_EXPRESSION)
	public void consumeWithHeaders(Message<String> message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {

		try {
			Map<String, String> headers = extractHeaders(message.getHeaders());
			log.debug("Processing message with {} headers from topic {}", headers.size(), topic);
			handler.processWithMetadata(message.getPayload(), headers);
		} catch (Exception e) {
			log.error("Error processing message with headers from topic {}: {}", topic, e.getMessage());
		}
	}

	private Map<String, String> extractHeaders(MessageHeaders headers) {
		if (headers == null) {
			return Collections.emptyMap();
		}

		Map<String, String> headersMap = new HashMap<>();

		headers.forEach((key, value) -> {
			try {
				if (value instanceof byte[]) {
					headersMap.put(key, new String((byte[]) value, StandardCharsets.UTF_8));
				} else if (value != null) {
					headersMap.put(key, value.toString());
				}
			} catch (Exception e) {
				log.warn("Error processing header {}: {}", key, e.getMessage());
			}
		});

		return headersMap;
	}
}