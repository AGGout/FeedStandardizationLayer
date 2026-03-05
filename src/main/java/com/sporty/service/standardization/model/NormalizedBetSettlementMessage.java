package com.sporty.service.standardization.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NormalizedBetSettlementMessage implements NormalizedMessage {
    String source;
    String sourceId;
    MatchResult matchResult;

    @Override
    public String getMessageType() {
        return MessageType.BET_SETTLEMENT.name();
    }
}
