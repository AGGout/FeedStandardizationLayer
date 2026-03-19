package com.sporty.service.feed.standardization.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The three possible outcomes of a match, each mapped to the industry-standard
 * 1X2 symbol used in the normalised odds representation.
 */
public enum MatchResult {
    HOME("1"),
    DRAW("X"),
    AWAY("2");

    private final String symbol;

    MatchResult(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    private static final Map<String, MatchResult> BY_SYMBOL = Arrays.stream(values())
            .collect(Collectors.toMap(MatchResult::symbol, r -> r));

    /**
     * Looks up a {@code MatchResult} by its 1X2 symbol.
     *
     * @param symbol one of {@code "1"}, {@code "X"}, or {@code "2"}
     * @return the matching enum constant
     * @throws IllegalArgumentException if the symbol is not recognised
     */
    public static MatchResult fromSymbol(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("symbol must not be null");
        MatchResult result = BY_SYMBOL.get(symbol);
        if (result == null)
            throw new IllegalArgumentException(
                    "Unknown symbol: '%s'. Expected one of: %s".formatted(symbol, BY_SYMBOL.keySet()));
        return result;
    }
}
