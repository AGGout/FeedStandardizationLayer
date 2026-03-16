package com.sporty.service.feed.standardization.util;

import com.sporty.service.feed.standardization.model.MatchResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * General-purpose utility methods shared across the application.
 */
public final class Util {

    private Util() {
    }

    /**
     * Creates a deterministic UUID version 7 from source, id, and timestamp.
     * The same (source, id, timestamp) triple always produces the same UUID.
     * <p>
     * UUID v7 layout (RFC 9562):
     * - bits  0–47:  unix_ts_ms (48 bits)
     * - bits 48–51:  version = 0x7 (4 bits)
     * - bits 52–63:  seq_hi (12 bits)  ┐ filled deterministically
     * - bits 64–65:  variant = 0b10     │ from SHA-1(source:id)
     * - bits 66–127: random (62 bits)  ┘
     */
    public static UUID uuid7(String source, String id, long timestamp) {
        byte[] hash = sha1(source + ":" + id);

        // Pack first 8 bytes of hash into a long (gives us 64 bits to draw from)
        long h = 0;
        for (int i = 0; i < 8; i++) {
            h = (h << 8) | (hash[i] & 0xFF);
        }

        long seq = (h >>> 52) & 0xFFFL;               // top 12 bits → seq field
        long random62 = h & 0x3FFFFFFFFFFFFFFFL;  // low 62 bits → random field

        long msb = (timestamp << 16) | (0x7L << 12) | seq;
        long lsb = (0b10L << 62) | random62;

        return new UUID(msb, lsb);
    }

    /**
     * Extracts a mandatory, non-blank String field from a raw message map.
     *
     * @param raw   the raw message map
     * @param field the field name to look up
     * @return the field value
     * @throws IllegalArgumentException if the field is absent, blank, or not a String
     */
    public static String requireField(Map<String, Object> raw, String field) {
        Object value = raw.get(field);
        if (value == null) throw new IllegalArgumentException("Missing required field: " + field);
        if (!(value instanceof String str))
            throw new IllegalArgumentException(
                    "Field '%s' must be a string but got %s".formatted(field, value.getClass().getSimpleName()));
        if (str.isBlank())
            throw new IllegalArgumentException("Field '%s' must not be blank".formatted(field));
        return str;
    }

    /**
     * Extracts a mandatory nested object field from a raw message map.
     *
     * @param raw   the raw message map
     * @param field the field name to look up
     * @return the nested map
     * @throws IllegalArgumentException if the field is absent or not a Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> requireMap(Map<String, Object> raw, String field) {
        Object value = raw.get(field);
        if (value == null) throw new IllegalArgumentException("Missing required field: " + field);
        if (!(value instanceof Map))
            throw new IllegalArgumentException(
                    "Field '%s' must be an object but got %s".formatted(field, value.getClass().getSimpleName()));
        return (Map<String, Object>) value;
    }

    /**
     * Extracts and validates a set of numeric odds values from a nested map field.
     * Each entry in {@code keyMapping} must be present in the sub-map and must be a {@link Number}.
     *
     * @param raw        the raw message map
     * @param field      the field name containing the odds sub-map
     * @param keyMapping mapping from provider-specific key to canonical {@link MatchResult}
     * @return odds keyed by {@link MatchResult}
     * @throws IllegalArgumentException if the field is missing, wrong type, or any odds entry is absent or non-numeric
     */
    public static Map<MatchResult, Double> extractOdds(Map<String, Object> raw, String field,
                                                        Map<String, MatchResult> keyMapping) {
        Map<String, Object> oddsRaw = requireMap(raw, field);
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

    private static byte[] sha1(String input) {
        try {
            return MessageDigest.getInstance("SHA-1")
                    .digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 not available", e);
        }
    }
}
