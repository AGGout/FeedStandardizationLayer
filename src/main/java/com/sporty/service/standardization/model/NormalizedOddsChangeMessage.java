package com.sporty.service.standardization.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class NormalizedOddsChangeMessage implements NormalizedMessage {
    String source;
    String sourceId;
    @Getter(lombok.AccessLevel.NONE)
    Map<MatchResult, Double> odds;

    @Override
    public String getMessageType() {
        return MessageType.ODDS_CHANGE.name();
    }

    public Map<String, Double> getOdds() {
        return odds.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(e -> e.getKey().symbol, Map.Entry::getValue));
    }
}
