package com.sporty.service.feed.standardization.normalizer;

import com.sporty.service.feed.standardization.model.NormalizedMessage;

import java.util.Map;

/**
 * Contract for a provider- and message-type-specific normalizer.
 * Each implementation is responsible for a single (source, rawMessageType) combination
 * and converts the provider's raw payload format into a canonical {@link NormalizedMessage}.
 * <p>
 * Extends {@link FeedNormalizerDescriptor} for registry metadata, and adds the
 * normalization behaviour. Implementations are registered automatically by
 * {@link FeedNormalizerRegistry} via Spring's dependency injection.
 */
public interface FeedNormalizer extends FeedNormalizerDescriptor {

    /**
     * Converts a raw provider payload into a canonical {@link NormalizedMessage}.
     *
     * @param sourceMessage the raw message deserialized from the provider's JSON
     * @return the normalised representation
     * @throws IllegalArgumentException if a required field is missing or invalid
     */
    NormalizedMessage normalize(Map<String, Object> sourceMessage);
}
