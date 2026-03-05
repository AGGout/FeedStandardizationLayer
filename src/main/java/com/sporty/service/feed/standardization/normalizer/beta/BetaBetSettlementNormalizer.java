package com.sporty.service.feed.standardization.normalizer.beta;

import com.sporty.service.feed.standardization.model.MatchResult;
import com.sporty.service.feed.standardization.model.NormalizedBetSettlementMessage;
import com.sporty.service.feed.standardization.model.NormalizedMessage;
import com.sporty.service.feed.standardization.normalizer.FeedNormalizer;
import com.sporty.service.feed.standardization.util.Util;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Normalizes bet settlement messages from provider Beta ({@code type: "SETTLEMENT"}).
 * Expects a {@code result} field with a word value ({@code "home"}, {@code "draw"}, {@code "away"}),
 * which is mapped to the canonical 1X2 symbol via {@link com.sporty.service.feed.standardization.model.MatchResult#fromSymbol}.
 */
@Component
public class BetaBetSettlementNormalizer implements FeedNormalizer {

    private static final Map<String, String> RESULT_TO_SYMBOL = Map.of(
            "home", MatchResult.HOME.symbol,
            "draw", MatchResult.DRAW.symbol,
            "away", MatchResult.AWAY.symbol
    );

    @Override
    public String getSource() { return "beta"; }

    @Override
    public String getRawMessageType() { return "SETTLEMENT"; }

    @Override
    public String getMessageTypeKey() { return "type"; }

    @Override
    public NormalizedMessage normalize(Map<String, Object> raw) {
        String eventId = Util.requireField(raw, "event_id");
        String result = Util.requireField(raw, "result");

        String symbol = RESULT_TO_SYMBOL.get(result);
        if (symbol == null) throw new IllegalArgumentException("Unknown result: " + result);

        return new NormalizedBetSettlementMessage("beta", eventId, MatchResult.fromSymbol(symbol));
    }
}
