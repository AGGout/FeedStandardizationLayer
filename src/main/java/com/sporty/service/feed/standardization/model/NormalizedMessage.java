package com.sporty.service.feed.standardization.model;

/**
 * Canonical representation of a feed message after normalisation.
 * Sealed to exactly two known subtypes, enabling exhaustive pattern matching.
 * Implementations are designed to be serialized directly to JSON for downstream consumers.
 */
public sealed interface NormalizedMessage
        permits NormalizedOddsChangeMessage, NormalizedBetSettlementMessage {

    MessageType getMessageType();
    String source();
    String eventId();
}
