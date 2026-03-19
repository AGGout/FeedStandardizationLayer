package com.sporty.service.feed.standardization.util;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void uuid7IsDeterministic() {
        long timestamp = 1_700_000_000_000L;
        UUID first  = Util.uuid7("alpha", "ev123", timestamp);
        UUID second = Util.uuid7("alpha", "ev123", timestamp);
        assertEquals(first, second);
    }

    @Test
    void uuid7DiffersForDifferentInputs() {
        long timestamp = 1_700_000_000_000L;
        assertNotEquals(
                Util.uuid7("alpha", "ev123", timestamp),
                Util.uuid7("beta",  "ev123", timestamp)
        );
        assertNotEquals(
                Util.uuid7("alpha", "ev123", timestamp),
                Util.uuid7("alpha", "ev456", timestamp)
        );
        assertNotEquals(
                Util.uuid7("alpha", "ev123", timestamp),
                Util.uuid7("alpha", "ev123", timestamp + 1)
        );
    }

    @Test
    void uuid7EncodesTimestampInTopBits() {
        long timestamp = 1_700_000_000_000L;
        UUID uuid = Util.uuid7("alpha", "ev123", timestamp);
        long encodedTimestamp = uuid.getMostSignificantBits() >>> 16;
        assertEquals(timestamp, encodedTimestamp);
    }

    @Test
    void requireStringFieldReturnsValueWhenPresent() {
        Map<String, Object> raw = Map.of("key", "value");
        assertEquals("value", Util.requireStringField(raw, "key"));
    }

    @Test
    void requireStringFieldThrowsWhenAbsent() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Util.requireStringField(Map.of(), "missingKey"));
        assertTrue(ex.getMessage().contains("missingKey"));
    }

    @Test
    void requireStringFieldThrowsWhenBlank() {
        Map<String, Object> raw = new java.util.HashMap<>();
        raw.put("key", "   ");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Util.requireStringField(raw, "key"));
        assertTrue(ex.getMessage().contains("key"));
    }

    @Test
    void requireStringFieldThrowsWhenWrongType() {
        Map<String, Object> raw = Map.of("key", 42);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Util.requireStringField(raw, "key"));
        assertTrue(ex.getMessage().contains("key"));
        assertTrue(ex.getMessage().contains("string"));
    }

    @Test
    void requireMapReturnsMapWhenPresent() {
        Map<String, Object> nested = Map.of("a", 1);
        Map<String, Object> raw = Map.of("child", nested);
        assertEquals(nested, Util.requireMap(raw, "child"));
    }

    @Test
    void requireMapThrowsWhenAbsent() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Util.requireMap(Map.of(), "child"));
        assertTrue(ex.getMessage().contains("child"));
    }

    @Test
    void requireMapThrowsWhenWrongType() {
        Map<String, Object> raw = Map.of("child", "not-a-map");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Util.requireMap(raw, "child"));
        assertTrue(ex.getMessage().contains("child"));
        assertTrue(ex.getMessage().contains("object"));
    }
}
