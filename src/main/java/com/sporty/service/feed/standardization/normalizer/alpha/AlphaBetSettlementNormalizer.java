package com.sporty.service.feed.standardization.normalizer.alpha;

import com.sporty.service.feed.standardization.model.MatchResult;
import com.sporty.service.feed.standardization.model.NormalizedBetSettlementMessage;
import com.sporty.service.feed.standardization.model.NormalizedMessage;
import com.sporty.service.feed.standardization.normalizer.FeedNormalizer;
import com.sporty.service.feed.standardization.util.Util;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Normalizes bet settlement messages from provider Alpha ({@code msg_type: "settlement"}).
 * Expects an {@code outcome} field containing a 1X2 symbol ({@code "1"}, {@code "X"}, {@code "2"}).
 */
@Component
public class AlphaBetSettlementNormalizer implements FeedNormalizer {

    @Override
    public String getSource() { return "alpha"; }

    @Override
    public String getRawMessageType() { return "settlement"; }

    @Override
    public String getMessageTypeKey() { return "msg_type"; }

    @Override
    public NormalizedMessage normalize(Map<String, Object> raw) {
        String eventId = Util.requireField(raw, "event_id");
        String outcome = Util.requireField(raw, "outcome");
        return new NormalizedBetSettlementMessage("alpha", eventId, MatchResult.fromSymbol(outcome));
    }
}
