package com.sporty.service.feed.standardization.normalizer.alpha;

import com.sporty.service.feed.standardization.model.MatchResult;
import com.sporty.service.feed.standardization.model.NormalizedBetSettlementMessage;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlphaBetSettlementNormalizerTest {

    private final AlphaBetSettlementNormalizer normalizer = new AlphaBetSettlementNormalizer();

    @Test
    void normalizesValidMessage() {
        Map<String, Object> raw = Map.of("event_id", "ev123", "outcome", "X");

        NormalizedBetSettlementMessage result = (NormalizedBetSettlementMessage) normalizer.normalize(raw);

        assertEquals("alpha", result.source());
        assertEquals("ev123", result.sourceId());
        assertEquals(MatchResult.DRAW, result.matchResult());
    }

    @Test
    void throwsWhenOutcomeMissing() {
        Map<String, Object> raw = Map.of("event_id", "ev123");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(raw));
        assertTrue(ex.getMessage().contains("outcome"));
    }

    @Test
    void throwsWhenOutcomeIsUnknownSymbol() {
        Map<String, Object> raw = Map.of("event_id", "ev123", "outcome", "home");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(raw));
        assertTrue(ex.getMessage().contains("home"));
    }
}
