package com.sporty.service.standardization.messaging;

import com.sporty.service.standardization.model.NormalizedMessage;
import com.sporty.service.standardization.util.Util;

import java.time.Instant;
import java.util.Map;

public class JsonLogMessenger implements Messenger {
    private static final String HEADER_INTERNAL_ID = "InternalId";

    @Override
    public void send(NormalizedMessage message) {
        // Just as an example. If the message gets send to a message broker, non business logic data could be added to the headers.
        // for example a idempotency key with timestamp.
        String uuid = Util.uuid7(message.getSource(), message.getSourceId(), Instant.now().toEpochMilli()).toString();
        Map<String, Object> headers = Map.of(HEADER_INTERNAL_ID, uuid);

    }
}
