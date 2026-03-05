package com.sporty.service.standardization.normalizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedNormalizerRegistryTest {

    private FeedNormalizer normalizerFor(String source, String rawMessageType) {
        return new FeedNormalizer() {
            @Override
            public String getSource() { return source; }

            @Override
            public String getRawMessageType() { return rawMessageType; }

            @Override
            public com.sporty.service.standardization.model.NormalizedMessage normalize(java.util.Map<String, Object> msg) {
                return null;
            }
        };
    }

    @Test
    void returnsCorrectNormalizerForSourceAndType() {
        FeedNormalizer a = normalizerFor("sourceA", "type1");
        FeedNormalizerRegistry registry = new FeedNormalizerRegistry(List.of(a, normalizerFor("sourceA", "type2"), normalizerFor("sourceB", "type1")));

        assertSame(a, registry.getNormalizer("sourceA", "type1"));
    }

    @Test
    void throwsForUnknownSource() {
        FeedNormalizerRegistry registry = new FeedNormalizerRegistry(List.of(normalizerFor("sourceA", "type1")));

        assertThrows(IllegalArgumentException.class, () -> registry.getNormalizer("unknown", "type1"));
    }

    @Test
    void throwsForUnknownMessageType() {
        FeedNormalizerRegistry registry = new FeedNormalizerRegistry(List.of(normalizerFor("sourceA", "type1")));

        assertThrows(IllegalArgumentException.class, () -> registry.getNormalizer("sourceA", "unknown"));
    }

    @Test
    void throwsOnDuplicateSourceAndMessageType() {
        assertThrows(IllegalStateException.class, () ->
                new FeedNormalizerRegistry(List.of(normalizerFor("sourceA", "type1"), normalizerFor("sourceA", "type1"))));
    }
}
