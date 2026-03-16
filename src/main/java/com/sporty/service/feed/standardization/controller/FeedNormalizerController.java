package com.sporty.service.feed.standardization.controller;

import com.sporty.service.feed.standardization.processor.FeedProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * REST controller that accepts raw feed messages from external providers and
 * delegates them to the {@link com.sporty.service.feed.standardization.processor.FeedProcessingService}
 * for normalisation and forwarding. Each endpoint is dedicated to one provider so
 * the source identifier can be determined without inspecting the payload.
 */
@RestController
public class FeedNormalizerController {

    private final FeedProcessingService processingService;

    public FeedNormalizerController(FeedProcessingService processingService) {
        this.processingService = processingService;
    }

    /**
     * Accepts a raw feed message from provider Alpha and queues it for processing.
     *
     * @param rawMessage the raw JSON payload deserialized into a map
     * @return 202 Accepted on success, 400 if the message is malformed or unrecognised
     */
    @PostMapping("/provider-alpha/feed")
    public ResponseEntity<Void> alphaFeed(@RequestBody Map<String, Object> rawMessage) {
        return handleFeed("alpha", rawMessage);
    }

    /**
     * Accepts a raw feed message from provider Beta and queues it for processing.
     *
     * @param rawMessage the raw JSON payload deserialized into a map
     * @return 202 Accepted on success, 400 if the message is malformed or unrecognised
     */
    @PostMapping("/provider-beta/feed")
    public ResponseEntity<Void> betaFeed(@RequestBody Map<String, Object> rawMessage) {
        return handleFeed("beta", rawMessage);
    }

    private ResponseEntity<Void> handleFeed(String source, Map<String, Object> rawMessage) {
        if (rawMessage == null || rawMessage.isEmpty())
            throw new IllegalArgumentException("Request body must not be empty");
        processingService.process(source, rawMessage, Instant.now().toEpochMilli());
        return ResponseEntity.accepted().build();
    }
}
