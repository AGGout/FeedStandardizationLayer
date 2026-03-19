package com.sporty.service.feed.standardization.messaging;

import com.sporty.service.feed.standardization.model.NormalizedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Development/stub implementation of {@link Messenger} that serialises the
 * message and its headers to JSON and writes them to the application log.
 * Intended to be replaced by a real broker integration (e.g. Kafka, RabbitMQ)
 * in production.
 */
@Component
public class JsonLogMessenger implements Messenger {
    private static final Logger logger = LoggerFactory.getLogger(JsonLogMessenger.class);

    private final ObjectMapper objectMapper;

    public JsonLogMessenger(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(NormalizedMessage message, Map<String, Object> headers) {
        Map<String, Object> data = Map.of("headers", headers, "message", message);
        try {
            logger.info(objectMapper.writeValueAsString(data));
        } catch (Exception e) {
            logger.error("Failed to serialize normalized message for logging", e);
        }
    }
}
