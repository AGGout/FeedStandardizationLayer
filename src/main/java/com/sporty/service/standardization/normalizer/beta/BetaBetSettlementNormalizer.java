package com.sporty.service.standardization.normalizer.beta;

import com.sporty.service.standardization.model.MatchResult;
import com.sporty.service.standardization.model.NormalizedBetSettlementMessage;
import com.sporty.service.standardization.model.NormalizedMessage;
import com.sporty.service.standardization.normalizer.FeedNormalizer;
import com.sporty.service.standardization.util.Util;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BetaBetSettlementNormalizer implements FeedNormalizer {

    private static final Map<String, MatchResult> RESULT_MAP = Map.of(
            "home", MatchResult.HOME,
            "draw", MatchResult.DRAW,
            "away", MatchResult.AWAY
    );

    @Override
    public String getSource() {
        return "beta";
    }

    @Override
    public String getRawMessageType() {
        return "SETTLEMENT";
    }

    @Override
    public NormalizedMessage normalize(Map<String, Object> raw) {
        String eventId = Util.requireField(raw, "event_id");
        String result = Util.requireField(raw, "result");

        MatchResult matchResult = RESULT_MAP.get(result);
        if (matchResult == null) throw new IllegalArgumentException("Unknown result: " + result);

        return new NormalizedBetSettlementMessage("beta", eventId, matchResult);
    }
}
