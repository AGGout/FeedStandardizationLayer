package com.sporty.service.standardization.normalizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeedNormalizerRegistryTest {

    private FeedNormalizer normalizerFor(String source) {
        return new FeedNormalizer() {
            @Override public String getSource() { return source; }
            @Override public com.sporty.service.standardization.model.NormalizedMessage normalize(java.util.Map<String, Object> msg) { return null; }
        };
    }

    @Test
    void returnsCorrectNormalizerForSource() {
        FeedNormalizer a = normalizerFor("sourceA");
        FeedNormalizerRegistry registry = new FeedNormalizerRegistry(List.of(a, normalizerFor("sourceB")));

        assertSame(a, registry.getNormalizerForSource("sourceA"));
    }

    @Test
    void throwsForUnknownSource() {
        FeedNormalizerRegistry registry = new FeedNormalizerRegistry(List.of(normalizerFor("sourceA")));

        assertThrows(IllegalArgumentException.class, () -> registry.getNormalizerForSource("unknown"));
    }

    @Test
    void throwsOnDuplicateSource() {
        assertThrows(IllegalStateException.class, () ->
                new FeedNormalizerRegistry(List.of(normalizerFor("sourceA"), normalizerFor("sourceA"))));
    }
}
