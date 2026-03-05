package com.sporty.service.standardization.controller;

import com.sporty.service.standardization.processor.FeedProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class FeedNormalizerController {

    private final FeedProcessingService processingService;

    public FeedNormalizerController(FeedProcessingService processingService) {
        this.processingService = processingService;
    }

    @PostMapping("/provider-alpha/feed")
    public ResponseEntity<Void> alphaFeed(@RequestBody Map<String, Object> rawMessage) {
        processingService.process("alpha", rawMessage, Instant.now().toEpochMilli());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/provider-beta/feed")
    public ResponseEntity<Void> betaFeed(@RequestBody Map<String, Object> rawMessage) {
        processingService.process("beta", rawMessage, Instant.now().toEpochMilli());
        return ResponseEntity.accepted().build();
    }
}
