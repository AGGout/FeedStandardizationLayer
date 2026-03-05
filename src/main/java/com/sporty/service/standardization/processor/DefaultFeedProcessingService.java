package com.sporty.service.standardization.processor;

import com.sporty.service.standardization.messaging.Messenger;
import com.sporty.service.standardization.model.NormalizedMessage;
import com.sporty.service.standardization.normalizer.FeedNormalizerRegistry;
import com.sporty.service.standardization.util.Util;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class DefaultFeedProcessingService implements FeedProcessingService {
    private static final String HEADER_IDEMPOTENCY_KEY  = "IdempotencyKey";

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

        // Just as an example. If the message gets send to a message broker, non business logic data could be added to the headers.
        // for example a idempotency key with timestamp.
        String uuid = Util.uuid7(normalized.getSrc(),normalized.getSrcId(), Instant.now().toEpochMilli()).toString();
        Map<String, Object> headers = Map.of(HEADER_IDEMPOTENCY_KEY, uuid);

        messenger.send(normalized, headers);
    }
}
