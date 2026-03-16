package com.sporty.service.feed.standardization.normalizer.alpha;

import com.sporty.service.feed.standardization.model.MatchResult;
import com.sporty.service.feed.standardization.model.NormalizedMessage;
import com.sporty.service.feed.standardization.model.NormalizedOddsChangeMessage;
import com.sporty.service.feed.standardization.normalizer.FeedNormalizer;
import com.sporty.service.feed.standardization.util.Util;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Normalizes odds change messages from provider Alpha ({@code msg_type: "odds_update"}).
 * Expects a {@code values} map keyed by 1X2 symbol ({@code "1"}, {@code "X"}, {@code "2"}).
 */
@Component
public class AlphaOddsChangeNormalizer implements FeedNormalizer {

    private static final Map<String, MatchResult> KEY_MAP = Map.of(
            "1", MatchResult.HOME,
            "X", MatchResult.DRAW,
            "2", MatchResult.AWAY
    );

    @Override
    public String getSource() { return "alpha"; }

    @Override
    public String getRawMessageType() { return "odds_update"; }

    @Override
    public String getMessageTypeKey() { return "msg_type"; }

    @Override
    public NormalizedMessage normalize(Map<String, Object> raw) {
        String eventId = Util.requireField(raw, "event_id");
        Map<MatchResult, Double> odds = Util.extractOdds(raw, "values", KEY_MAP);
        return NormalizedOddsChangeMessage.from(getSource(), eventId, odds);
    }
}
