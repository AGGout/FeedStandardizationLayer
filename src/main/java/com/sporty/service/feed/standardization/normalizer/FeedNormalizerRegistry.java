package com.sporty.service.feed.standardization.normalizer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registry that indexes all available {@link FeedNormalizer} beans at startup.
 * Normalizers are keyed by {@code "source:rawMessageType"} for fast lookup,
 * and a secondary index maps each source to its message-type field name.
 * <p>
 * Duplicate keys or conflicting message-type field names within the same source
 * cause an {@link IllegalStateException} at startup, ensuring misconfiguration
 * is caught early.
 */
@Service
public class FeedNormalizerRegistry {

    private final Map<String, FeedNormalizer> normalizers;
    private final Map<String, String> messageTypeKeys;

    public FeedNormalizerRegistry(List<FeedNormalizer> normalizers) {
        this.normalizers = normalizers.stream()
                .collect(Collectors.toMap(
                        n -> key(n.getSource(), n.getRawMessageType()),
                        Function.identity()
                ));

        // One message type key per source — fail fast if a source has conflicting keys
        this.messageTypeKeys = normalizers.stream()
                .collect(Collectors.toMap(
                        FeedNormalizer::getSource,
                        FeedNormalizer::getMessageTypeKey,
                        (existing, replacement) -> {
                            if (!existing.equals(replacement)) {
                                throw new IllegalStateException(
                                        "Conflicting message type keys for the same source: '%s' vs '%s'".formatted(existing, replacement));
                            }
                            return existing;
                        }
                ));
    }

    /**
     * Looks up the normalizer for the given source and raw message type.
     *
     * @throws IllegalArgumentException if no normalizer is registered for the combination
     */
    public FeedNormalizer getNormalizer(String source, String rawMessageType) {
        FeedNormalizer normalizer = normalizers.get(key(source, rawMessageType));
        if (normalizer == null) {
            throw new IllegalArgumentException(
                    "No normalizer registered for source '%s' and message type '%s'".formatted(source, rawMessageType));
        }
        return normalizer;
    }

    /**
     * Returns the JSON field name used by the given source to identify the message type.
     *
     * @throws IllegalArgumentException if no normalizer is registered for the source
     */
    public String getMessageTypeKey(String source) {
        String key = messageTypeKeys.get(source);
        if (key == null) {
            throw new IllegalArgumentException("No normalizer registered for source: " + source);
        }
        return key;
    }

    private static String key(String source, String rawMessageType) {
        return source + ":" + rawMessageType;
    }
}
