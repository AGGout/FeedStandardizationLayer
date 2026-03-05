package com.sporty.service.feed.standardization.normalizer.beta;

import com.sporty.service.feed.standardization.model.NormalizedOddsChangeMessage;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BetaOddsChangeNormalizerTest {

    private final BetaOddsChangeNormalizer normalizer = new BetaOddsChangeNormalizer();

    @Test
    void normalizesValidMessage() {
        Map<String, Object> raw = Map.of(
                "event_id", "ev456",
                "odds", Map.of("home", 1.95, "draw", 3.2, "away", 4.0)
        );

        NormalizedOddsChangeMessage result = (NormalizedOddsChangeMessage) normalizer.normalize(raw);

        assertEquals("beta", result.source());
        assertEquals("ev456", result.sourceId());
        assertEquals(1.95, result.odds().get("1"));
        assertEquals(3.2,  result.odds().get("X"));
        assertEquals(4.0,  result.odds().get("2"));
    }

    @Test
    void throwsWhenOddsMapMissing() {
        Map<String, Object> raw = Map.of("event_id", "ev456");

        assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(raw));
    }

    @Test
    void throwsWhenOddsEntryMissing() {
        Map<String, Object> odds = new HashMap<>();
        odds.put("home", 1.95);
        odds.put("draw", 3.2);
        // "away" intentionally omitted
        Map<String, Object> raw = Map.of("event_id", "ev456", "odds", odds);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(raw));
        assertTrue(ex.getMessage().contains("away"));
    }
}
