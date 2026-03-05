package com.sporty.service.feed.standardization.normalizer;

import com.sporty.service.feed.standardization.model.NormalizedMessage;

import java.util.Map;

/**
 * Contract for a provider- and message-type-specific normalizer.
 * Each implementation is responsible for a single (source, rawMessageType) combination
 * and converts the provider's raw payload format into a canonical {@link NormalizedMessage}.
 * <p>
 * Implementations are registered automatically by {@link FeedNormalizerRegistry}
 * via Spring's dependency injection.
 */
public interface FeedNormalizer {

    /**
     * Converts a raw provider payload into a canonical {@link NormalizedMessage}.
     *
     * @param sourceMessage the raw message deserialized from the provider's JSON
     * @return the normalised representation
     * @throws IllegalArgumentException if a required field is missing or invalid
     */
    NormalizedMessage normalize(Map<String, Object> sourceMessage);

    /** @return the provider identifier this normalizer handles, e.g. {@code "alpha"} */
    String getSource();

    /** @return the raw message type string as sent by the provider, e.g. {@code "odds_update"} */
    String getRawMessageType();

    /** @return the JSON field name used by this provider to identify the message type, e.g. {@code "msg_type"} */
    String getMessageTypeKey();
}
