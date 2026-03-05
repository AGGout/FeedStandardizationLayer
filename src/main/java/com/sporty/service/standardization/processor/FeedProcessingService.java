package com.sporty.service.standardization.processor;

import java.util.Map;

public interface FeedProcessingService {
    void process(String source, Map<String, Object> rawMessage, long timestamp);
}
