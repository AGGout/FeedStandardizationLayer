package com.sporty.service.feed.standardization.model;

/**
 * Normalised representation of a bet settlement event, indicating the final
 * outcome of a match. The {@code matchResult} field holds the winning outcome
 * expressed as a canonical {@link MatchResult}.
 */
public record NormalizedBetSettlementMessage(
        String source,
        String sourceId,
        MatchResult matchResult
) implements NormalizedMessage {

    @Override
    public String getSource() { return source; }

    @Override
    public String getSourceId() { return sourceId; }

    @Override
    public String getMessageType() { return MessageType.BET_SETTLEMENT.name(); }
}
