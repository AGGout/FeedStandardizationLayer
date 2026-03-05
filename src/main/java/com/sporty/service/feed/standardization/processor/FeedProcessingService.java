package com.sporty.service.feed.standardization.processor;

import java.util.Map;

/**
 * Orchestrates the end-to-end processing of a single raw feed message:
 * normalisation, header enrichment, and forwarding to the messaging layer.
 */
public interface FeedProcessingService {

    /**
     * Processes a raw feed message from the given provider.
     *
     * @param source      the provider identifier, e.g. {@code "alpha"}
     * @param rawMessage  the raw payload deserialized from JSON
     * @param timestamp   Unix epoch milliseconds captured when the request arrived
     */
    void process(String source, Map<String, Object> rawMessage, long timestamp);
}
