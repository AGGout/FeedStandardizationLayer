package com.sporty.service.feed.standardization.normalizer.alpha;

import com.sporty.service.feed.standardization.model.NormalizedOddsChangeMessage;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlphaOddsChangeNormalizerTest {

    private final AlphaOddsChangeNormalizer normalizer = new AlphaOddsChangeNormalizer();

    @Test
    void normalizesValidMessage() {
        Map<String, Object> raw = Map.of(
                "event_id", "ev123",
                "values", Map.of("1", 2.0, "X", 3.1, "2", 3.8)
        );

        NormalizedOddsChangeMessage result = (NormalizedOddsChangeMessage) normalizer.normalize(raw);

        assertEquals("alpha", result.source());
        assertEquals("ev123", result.sourceId());
        assertEquals(2.0, result.odds().get("1"));
        assertEquals(3.1, result.odds().get("X"));
        assertEquals(3.8, result.odds().get("2"));
    }

    @Test
    void throwsWhenEventIdMissing() {
        Map<String, Object> raw = Map.of("values", Map.of("1", 2.0, "X", 3.1, "2", 3.8));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(raw));
        assertTrue(ex.getMessage().contains("event_id"));
    }

    @Test
    void throwsWhenValuesMapMissing() {
        Map<String, Object> raw = Map.of("event_id", "ev123");

        assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(raw));
    }

    @Test
    void throwsWhenOddsSymbolMissingFromValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("1", 2.0);
        values.put("X", 3.1);
        // "2" intentionally omitted
        Map<String, Object> raw = Map.of("event_id", "ev123", "values", values);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(raw));
        assertTrue(ex.getMessage().contains("2"));
    }
}
