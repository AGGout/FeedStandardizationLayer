package com.sporty.service.standardization.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NormalizedMessage {
    String src;
    String srcId;
    MessageType messageType;
    MatchResult matchResult;
}
