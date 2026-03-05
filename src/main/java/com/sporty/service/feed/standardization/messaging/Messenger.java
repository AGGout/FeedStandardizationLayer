package com.sporty.service.feed.standardization.messaging;

import com.sporty.service.feed.standardization.model.NormalizedMessage;

import java.util.Map;

/**
 * Abstraction for dispatching a normalised message to a downstream system,
 * such as a message broker, event stream, or log sink.
 * Implementations are free to choose the transport mechanism.
 */
public interface Messenger {

    /**
     * Sends a normalised message with the given metadata headers.
     *
     * @param message the normalised feed message to dispatch
     * @param headers metadata to attach, e.g. idempotency keys or routing hints
     */
    void send(NormalizedMessage message, Map<String, Object> headers);
}
