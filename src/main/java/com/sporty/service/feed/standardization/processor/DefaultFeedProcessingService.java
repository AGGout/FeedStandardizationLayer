package com.sporty.service.feed.standardization.processor;

import com.sporty.service.feed.standardization.messaging.Messenger;
import com.sporty.service.feed.standardization.model.NormalizedMessage;
import com.sporty.service.feed.standardization.normalizer.FeedNormalizerRegistry;
import com.sporty.service.feed.standardization.util.Util;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Default implementation of {@link FeedProcessingService}.
 * Resolves the correct {@link com.sporty.service.feed.standardization.normalizer.FeedNormalizer}
 * from the registry, normalises the raw message, attaches a deterministic idempotency key
 * to the headers, and hands the result off to the {@link com.sporty.service.feed.standardization.messaging.Messenger}.
 */
@Service
public class DefaultFeedProcessingService implements FeedProcessingService {

    private static final String HEADER_IDEMPOTENCY_KEY = "IdempotencyKey";

    private final FeedNormalizerRegistry registry;
    private final Messenger messenger;

    public DefaultFeedProcessingService(FeedNormalizerRegistry registry, Messenger messenger) {
        this.registry = registry;
        this.messenger = messenger;
    }

    @Override
    public void process(String source, Map<String, Object> rawMessage, long timestamp) {
        if (source == null || source.isBlank())
            throw new IllegalArgumentException("source must not be blank");
        if (rawMessage == null || rawMessage.isEmpty())
            throw new IllegalArgumentException("rawMessage must not be null or empty");

        String typeKey = registry.getMessageTypeKey(source);
        String rawMessageType = Util.requireField(rawMessage, typeKey);
        NormalizedMessage normalized = registry.getNormalizer(source, rawMessageType).normalize(rawMessage);

        String idempotencyKey = Util.uuid7(source, normalized.getEventId(), timestamp).toString();
        messenger.send(normalized, Map.of(HEADER_IDEMPOTENCY_KEY, idempotencyKey));
    }
}
