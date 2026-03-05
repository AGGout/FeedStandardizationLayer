package com.sporty.service.standardization.messaging;

import com.sporty.service.standardization.model.NormalizedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class JsonLogMessenger implements Messenger {
    private static final Logger logger = LoggerFactory.getLogger(JsonLogMessenger.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void send(NormalizedMessage message, Map<String, Object> headers) {
        Map<String, Object> data = Map.of("headers", headers, "message", message);
        logger.info(objectMapper.writeValueAsString(data));
    }
}
