package com.sporty.service.feed.standardization.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MatchResultTest {

    @ParameterizedTest
    @CsvSource({"1, HOME", "X, DRAW", "2, AWAY"})
    void fromSymbolReturnsCorrectResult(String symbol, MatchResult expected) {
        assertEquals(expected, MatchResult.fromSymbol(symbol));
    }

    @Test
    void fromSymbolThrowsForUnknownSymbol() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> MatchResult.fromSymbol("home"));
        assertTrue(ex.getMessage().contains("home"));
    }
}
