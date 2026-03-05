package com.sporty.service.standardization.processor;

import com.sporty.service.standardization.messaging.Messenger;
import com.sporty.service.standardization.model.NormalizedMessage;
import com.sporty.service.standardization.normalizer.FeedNormalizerRegistry;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultFeedProcessingService implements FeedProcessingService {
    private final FeedNormalizerRegistry registry;
    private final Messenger messenger;

    public DefaultFeedProcessingService(FeedNormalizerRegistry registry, Messenger messenger) {
        this.registry = registry;
        this.messenger = messenger;
    }

    @Override
    public void process(String source, Map<String, Object> rawMessage) {
        String rawMessageType = (String) rawMessage.get("msg_type");
        NormalizedMessage normalized = registry.getNormalizer(source, rawMessageType).normalize(rawMessage);

        messenger.send(normalized);
    }
}
