package com.sporty.service.standardization.messaging;

import com.sporty.service.standardization.model.NormalizedMessage;

import java.util.Map;

public interface Messenger {
    void send(NormalizedMessage message, Map<String, Object> headers);
}
