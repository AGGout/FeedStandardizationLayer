package com.sporty.service.standardization.normalizer.alpha;

import com.sporty.service.standardization.model.MessageType;
import com.sporty.service.standardization.model.NormalizedMessage;
import com.sporty.service.standardization.normalizer.FeedNormalizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AlphaOddsChangeNormalizer implements FeedNormalizer {

    @Override
    public String getSource() { return "alpha"; }

    @Override
    public String getRawMessageType() { return "odds_update"; }

    @Override
    public NormalizedMessage normalize(Map<String, Object> raw) {
        // TODO: map alpha odds_update fields to NormalizedMessage
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
