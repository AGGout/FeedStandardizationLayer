package com.sporty.service.feed.standardization.normalizer;

import com.sporty.service.feed.standardization.model.MatchResult;
import com.sporty.service.feed.standardization.util.Util;

import java.util.EnumMap;
import java.util.Map;

/**
 * Shared utility for extracting and validating numeric odds from a raw provider payload.
 * Centralises the extraction logic used by all odds-change normalizers.
 */
public final class OddsExtractor {

    private OddsExtractor() {
    }

    /**
     * Extracts and validates a set of numeric odds values from a nested map field.
     * Each entry in {@code keyMapping} must be present in the sub-map and must be a {@link Number}.
     *
     * @param raw        the raw message map
     * @param field      the field name containing the odds sub-map
     * @param keyMapping mapping from provider-specific key to canonical {@link MatchResult}
     * @return odds keyed by {@link MatchResult}
     * @throws IllegalArgumentException if the field is missing, wrong type,
     *                                  or any odds entry is absent or non-numeric
     */
    public static Map<MatchResult, Double> extractOdds(Map<String, Object> raw, String field,
                                                        Map<String, MatchResult> keyMapping) {
        Map<String, Object> oddsRaw = Util.requireMap(raw, field);
        Map<MatchResult, Double> odds = new EnumMap<>(MatchResult.class);
        for (Map.Entry<String, MatchResult> entry : keyMapping.entrySet()) {
            String key = entry.getKey();
            Object val = oddsRaw.get(key);
            if (val == null)
                throw new IllegalArgumentException("Missing odds for key: " + key);
            if (!(val instanceof Number))
                throw new IllegalArgumentException(
                        "Odds value for key '%s' must be a number but got %s".formatted(key, val.getClass().getSimpleName()));
            odds.put(entry.getValue(), ((Number) val).doubleValue());
        }
        return odds;
    }
}
