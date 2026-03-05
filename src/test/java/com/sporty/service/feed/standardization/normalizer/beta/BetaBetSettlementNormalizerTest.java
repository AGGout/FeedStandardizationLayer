package com.sporty.service.feed.standardization.normalizer.beta;

import com.sporty.service.feed.standardization.model.MatchResult;
import com.sporty.service.feed.standardization.model.NormalizedBetSettlementMessage;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BetaBetSettlementNormalizerTest {

    private final BetaBetSettlementNormalizer normalizer = new BetaBetSettlementNormalizer();

    @Test
    void normalizesValidMessage() {
        Map<String, Object> raw = Map.of("event_id", "ev456", "result", "away");

        NormalizedBetSettlementMessage result = (NormalizedBetSettlementMessage) normalizer.normalize(raw);

        assertEquals("beta", result.source());
        assertEquals("ev456", result.eventId());
        assertEquals(MatchResult.AWAY, result.matchResult());
    }

    @Test
    void throwsWhenResultMissing() {
        Map<String, Object> raw = Map.of("event_id", "ev456");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(raw));
        assertTrue(ex.getMessage().contains("result"));
    }

    @Test
    void throwsWhenResultIsUnknown() {
        Map<String, Object> raw = Map.of("event_id", "ev456", "result", "unknown");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(raw));
        assertTrue(ex.getMessage().contains("unknown"));
    }
}
