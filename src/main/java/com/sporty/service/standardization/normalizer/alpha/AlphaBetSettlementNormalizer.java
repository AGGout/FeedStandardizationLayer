package com.sporty.service.standardization.normalizer.alpha;

import com.sporty.service.standardization.model.MatchResult;
import com.sporty.service.standardization.model.NormalizedBetSettlementMessage;
import com.sporty.service.standardization.model.NormalizedMessage;
import com.sporty.service.standardization.normalizer.FeedNormalizer;
import com.sporty.service.standardization.util.Util;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Component
public class AlphaBetSettlementNormalizer implements FeedNormalizer {

    @Override
    public String getSource() {
        return "alpha";
    }

    @Override
    public String getRawMessageType() { return "settlement"; }

    @Override
    public String getMessageTypeKey() { return "msg_type"; }

    @Override
    public NormalizedMessage normalize(Map<String, Object> raw) {
        String eventId = Util.requireField(raw, "event_id");
        String outcome = Util.requireField(raw, "outcome");

        MatchResult matchResult = Arrays.stream(MatchResult.values())
                .filter(r -> r.symbol.equals(outcome))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown outcome: " + outcome));

        return new NormalizedBetSettlementMessage("alpha", eventId, matchResult);
    }

}
