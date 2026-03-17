package com.sporty.service.feed.standardization.normalizer.beta;

import com.sporty.service.feed.standardization.model.FeedProvider;
import com.sporty.service.feed.standardization.model.MatchResult;
import com.sporty.service.feed.standardization.model.NormalizedBetSettlementMessage;
import com.sporty.service.feed.standardization.model.NormalizedMessage;
import com.sporty.service.feed.standardization.normalizer.FeedNormalizer;
import com.sporty.service.feed.standardization.util.Util;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Normalizes bet settlement messages from provider Beta ({@code type: "SETTLEMENT"}).
 * Expects a {@code result} field with a word value ({@code "home"}, {@code "draw"}, {@code "away"}).
 */
@Component
public class BetaBetSettlementNormalizer implements FeedNormalizer {

    private static final Map<String, MatchResult> KEY_MAP = Map.of(
            "home", MatchResult.HOME,
            "draw", MatchResult.DRAW,
            "away", MatchResult.AWAY
    );

    @Override
    public String getSource() { return FeedProvider.BETA.getId(); }

    @Override
    public String getRawMessageType() { return "SETTLEMENT"; }

    @Override
    public String getMessageTypeKey() { return "type"; }

    @Override
    public NormalizedMessage normalize(Map<String, Object> raw) {
        String eventId = Util.requireStringField(raw, "event_id");
        String result = Util.requireStringField(raw, "result");

        MatchResult matchResult = KEY_MAP.get(result);
        if (matchResult == null) throw new IllegalArgumentException("Unknown result: " + result);

        return new NormalizedBetSettlementMessage(getSource(), eventId, matchResult);
    }
}
