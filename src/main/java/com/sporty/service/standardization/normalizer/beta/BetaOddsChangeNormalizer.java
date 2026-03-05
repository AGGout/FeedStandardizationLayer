package com.sporty.service.standardization.normalizer.beta;

import com.sporty.service.standardization.model.MessageType;
import com.sporty.service.standardization.model.NormalizedMessage;
import com.sporty.service.standardization.normalizer.FeedNormalizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BetaOddsChangeNormalizer implements FeedNormalizer {

    @Override
    public String getSource() { return "beta"; }

    @Override
    public String getRawMessageType() { return "ODDS"; }  // beta uses a different name

    @Override
    public NormalizedMessage normalize(Map<String, Object> raw) {
        // TODO: map beta ODDS fields to NormalizedMessage
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
