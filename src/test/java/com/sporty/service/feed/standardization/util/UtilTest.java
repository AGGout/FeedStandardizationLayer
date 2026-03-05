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
    void requireFieldReturnsValueWhenPresent() {
        Map<String, Object> raw = Map.of("key", "value");
        assertEquals("value", Util.requireField(raw, "key"));
    }

    @Test
    void requireFieldThrowsWhenAbsent() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Util.requireField(Map.of(), "missingKey"));
        assertTrue(ex.getMessage().contains("missingKey"));
    }
}
