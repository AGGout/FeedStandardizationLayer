package com.sporty.service.feed.standardization.normalizer;

/**
 * Describes the identity of a feed normalizer: which provider it handles,
 * which raw message type it processes, and which JSON field names the provider
 * uses to identify the message type.
 * <p>
 * Separated from {@link FeedNormalizer} so that registry infrastructure
 * (indexing, validation) can depend only on metadata without coupling to
 * the normalization behaviour.
 */
public interface FeedNormalizerDescriptor {

    /** @return the provider identifier this normalizer handles, e.g. {@code "alpha"} */
    String getSource();

    /** @return the raw message type string as sent by the provider, e.g. {@code "odds_update"} */
    String getRawMessageType();

    /** @return the JSON field name used by this provider to identify the message type, e.g. {@code "msg_type"} */
    String getMessageTypeKey();
}
