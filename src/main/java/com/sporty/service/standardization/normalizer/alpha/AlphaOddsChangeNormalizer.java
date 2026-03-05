package com.sporty.service.standardization.normalizer.alpha;

import com.sporty.service.standardization.model.MatchResult;
import com.sporty.service.standardization.model.NormalizedMessage;
import com.sporty.service.standardization.model.NormalizedOddsChangeMessage;
import com.sporty.service.standardization.normalizer.FeedNormalizer;
import com.sporty.service.standardization.util.Util;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class AlphaOddsChangeNormalizer implements FeedNormalizer {

    @Override
    public String getSource() {
        return "alpha";
    }

    @Override
    public String getRawMessageType() { return "odds_update"; }

    @Override
    public String getMessageTypeKey() { return "msg_type"; }

    @Override
    @SuppressWarnings("unchecked")
    public NormalizedMessage normalize(Map<String, Object> raw) {
        String eventId = Util.requireField(raw, "event_id");
        Map<String, Object> values = (Map<String, Object>) raw.get("values");
        if (values == null) throw new IllegalArgumentException("Missing required field: values");

        Map<MatchResult, Double> odds = new EnumMap<>(MatchResult.class);
        for (MatchResult result : MatchResult.values()) {
            Object val = values.get(result.symbol);
            if (val == null) throw new IllegalArgumentException("Missing odds for symbol: " + result.symbol);
            odds.put(result, ((Number) val).doubleValue());
        }

        return new NormalizedOddsChangeMessage("alpha", eventId, odds);
    }
}
