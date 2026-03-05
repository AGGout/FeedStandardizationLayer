package com.sporty.service.standardization.normalizer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FeedNormalizerRegistry {

    private final Map<String, FeedNormalizer> normalizers;

    public FeedNormalizerRegistry(List<FeedNormalizer> normalizers) {
        this.normalizers = normalizers.stream()
                .collect(Collectors.toMap(
                        n -> key(n.getSource(), n.getRawMessageType()),
                        Function.identity()
                ));
    }

    private static String key(String source, String rawMessageType) {
        return source + ":" + rawMessageType;
    }

    public FeedNormalizer getNormalizer(String source, String rawMessageType) {
        FeedNormalizer normalizer = normalizers.get(key(source, rawMessageType));
        if (normalizer == null) {
            throw new IllegalArgumentException(
                    "No normalizer registered for source '%s' and message type '%s'".formatted(source, rawMessageType));
        }
        return normalizer;
    }
}
