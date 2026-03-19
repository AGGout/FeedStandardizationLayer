package com.sporty.service.feed.standardization.normalizer.beta;

import com.sporty.service.feed.standardization.model.FeedProvider;
import com.sporty.service.feed.standardization.model.MatchResult;
import com.sporty.service.feed.standardization.model.NormalizedMessage;
import com.sporty.service.feed.standardization.model.NormalizedOddsChangeMessage;
import com.sporty.service.feed.standardization.normalizer.FeedNormalizer;
import com.sporty.service.feed.standardization.normalizer.OddsExtractor;
import com.sporty.service.feed.standardization.util.Util;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Normalizes odds change messages from provider Beta ({@code type: "ODDS"}).
 * Expects an {@code odds} map keyed by word ({@code "home"}, {@code "draw"}, {@code "away"}).
 */
@Component
public class BetaOddsChangeNormalizer implements FeedNormalizer {

    private static final Map<String, MatchResult> KEY_MAP = Map.of(
            "home", MatchResult.HOME,
            "draw", MatchResult.DRAW,
            "away", MatchResult.AWAY
    );

    @Override
    public String getSource() {
        return FeedProvider.BETA.getId();
    }

    @Override
    public String getRawMessageType() { return "ODDS"; }

    @Override
    public String getMessageTypeKey() { return "type"; }

    @Override
    public NormalizedMessage normalize(Map<String, Object> raw) {
        String eventId = Util.requireStringField(raw, "event_id");
        Map<MatchResult, Double> odds = OddsExtractor.extractOdds(raw, "odds", KEY_MAP);
        return NormalizedOddsChangeMessage.from(getSource(), eventId, odds);
    }
}
