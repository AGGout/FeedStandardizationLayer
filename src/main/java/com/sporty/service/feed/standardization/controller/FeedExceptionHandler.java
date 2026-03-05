package com.sporty.service.feed.standardization.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates exceptions thrown during feed processing into meaningful HTTP responses.
 * Keeps error handling logic out of the controllers themselves.
 */
@RestControllerAdvice
public class FeedExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(FeedExceptionHandler.class);

    /**
     * Handles validation and routing failures such as unknown providers, missing fields,
     * or unrecognised message types. Returns 400 Bad Request with the error message as body.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Catch-all for unexpected errors. Returns 500 with a generic message to avoid
     * leaking internal details to the caller.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpected(Exception ex) {
        logger.error("Unexpected error during feed processing", ex);
        return ResponseEntity.internalServerError().body("An unexpected error occurred. Please try again later.");
    }
}
