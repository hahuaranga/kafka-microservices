package com.example.consumer.infrastructure.input;

/**
 * Author: hahuaranga@indracompany.com
 * Created on: 15-07-2025 at 23:28:23
 * File: MessageInputPort.java
 */
public interface MessageInputPort {
    void process(String message);
}
