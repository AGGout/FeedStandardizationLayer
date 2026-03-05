package com.sporty.service.standardization.normalizer;

import com.sporty.service.standardization.model.NormalizedMessage;

import java.util.Map;

public interface FeedNormalizer {
    NormalizedMessage normalize(Map<String, Object> sourceMessage);

    String getSource();

    String getRawMessageType();
}
