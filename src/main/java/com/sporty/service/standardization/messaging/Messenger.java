package com.sporty.service.standardization.messaging;

import com.sporty.service.standardization.model.NormalizedMessage;

public interface Messenger {
    void send(NormalizedMessage message);
}
