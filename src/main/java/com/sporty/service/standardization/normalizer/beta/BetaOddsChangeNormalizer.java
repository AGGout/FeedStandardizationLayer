package com.sporty.service.standardization.normalizer.beta;

import com.sporty.service.standardization.model.MatchResult;
import com.sporty.service.standardization.model.NormalizedMessage;
import com.sporty.service.standardization.model.NormalizedOddsChangeMessage;
import com.sporty.service.standardization.normalizer.FeedNormalizer;
import com.sporty.service.standardization.util.Util;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class BetaOddsChangeNormalizer implements FeedNormalizer {

    private static final Map<String, MatchResult> KEY_MAP = Map.of(
            "home", MatchResult.HOME,
            "draw", MatchResult.DRAW,
            "away", MatchResult.AWAY
    );

    @Override
    public String getSource() {
        return "beta";
    }

    @Override
    public String getRawMessageType() { return "ODDS"; }

    @Override
    public String getMessageTypeKey() { return "type"; }

    @Override
    @SuppressWarnings("unchecked")
    public NormalizedMessage normalize(Map<String, Object> raw) {
        String eventId = Util.requireField(raw, "event_id");
        Map<String, Object> oddsRaw = (Map<String, Object>) raw.get("odds");
        if (oddsRaw == null) throw new IllegalArgumentException("Missing required field: odds");

        Map<MatchResult, Double> odds = new EnumMap<>(MatchResult.class);
        KEY_MAP.forEach((key, result) -> {
            Object val = oddsRaw.get(key);
            if (val == null) throw new IllegalArgumentException("Missing odds for key: " + key);
            odds.put(result, ((Number) val).doubleValue());
        });

        return new NormalizedOddsChangeMessage("beta", eventId, odds);
    }
}
