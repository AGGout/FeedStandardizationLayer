package com.sporty.service.feed.standardization.model;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Normalised representation of an odds change event. Odds are stored as a
 * {@code Map<String, Double>} keyed by 1X2 symbol ({@code "1"}, {@code "X"}, {@code "2"})
 * for straightforward JSON serialization. Use {@link #from} to construct from
 * provider-internal {@link MatchResult} keys.
 */
public record NormalizedOddsChangeMessage(
        String source,
        String eventId,
        Map<String, Double> odds
) implements NormalizedMessage {

    /**
     * Convenience factory that accepts odds keyed by {@link MatchResult} and converts
     * them to 1X2 symbols before constructing the record.
     */
    public static NormalizedOddsChangeMessage from(String source, String eventId, Map<MatchResult, Double> rawOdds) {
        return new NormalizedOddsChangeMessage(source, eventId, rawOdds.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().symbol, Map.Entry::getValue)));
    }

    @Override
    public String getSource() { return source; }

    @Override
    public String getEventId() { return eventId; }

    @Override
    public String getMessageType() { return MessageType.ODDS_CHANGE.name(); }
}
