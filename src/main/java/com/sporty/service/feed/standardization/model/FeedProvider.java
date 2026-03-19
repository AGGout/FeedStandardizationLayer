package com.sporty.service.feed.standardization.model;

/**
 * Canonical registry of all supported feed providers (API clients).
 * <p>
 * Each constant's {@code id} is the stable string identifier used throughout
 * the system: as the normalizer source key, in normalised messages, and in logs.
 * Adding a new provider requires a new constant here plus the corresponding
 * normalizer implementations.
 */
public enum FeedProvider {

    ALPHA("alpha"),
    BETA("beta");

    private final String id;

    FeedProvider(String id) { this.id = id; }

    /** @return the stable string identifier for this provider, e.g. {@code "alpha"} */
    public String getId() { return id; }
}
