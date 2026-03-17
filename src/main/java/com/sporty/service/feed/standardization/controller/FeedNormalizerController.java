package com.sporty.service.feed.standardization.controller;

import com.sporty.service.feed.standardization.processor.FeedProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * REST controller that accepts raw feed messages from external providers and
 * delegates them to the {@link com.sporty.service.feed.standardization.processor.FeedProcessingService}
 * for normalisation and forwarding. The provider is identified by the URL path segment,
 * e.g. {@code /provider-alpha/feed}.
 */
@RestController
public class FeedNormalizerController {

    private final FeedProcessingService processingService;

    public FeedNormalizerController(FeedProcessingService processingService) {
        this.processingService = processingService;
    }

    /**
     * Accepts a raw feed message from any registered provider and queues it for processing.
     * The provider identifier is extracted from the URL path (e.g. {@code alpha} in
     * {@code /provider-alpha/feed}).
     *
     * @param provider   the provider identifier extracted from the URL path
     * @param rawMessage the raw JSON payload deserialized into a map
     * @return 202 Accepted on success, 400 if the message is malformed or unrecognised
     */
    @PostMapping("/provider-{provider}/feed")
    public ResponseEntity<Void> feed(@PathVariable String provider,
                                     @RequestBody Map<String, Object> rawMessage) {
        if (rawMessage == null || rawMessage.isEmpty())
            throw new IllegalArgumentException("Request body must not be empty");
        processingService.process(provider, rawMessage, Instant.now().toEpochMilli());
        return ResponseEntity.accepted().build();
    }
}
