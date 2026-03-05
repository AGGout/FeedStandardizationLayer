package com.sporty.service.feed.standardization.model;

/**
 * The canonical set of message types supported by the standardisation layer.
 * Each provider may use different names for these types in their raw payloads;
 * normalizers are responsible for mapping provider-specific names to these values.
 */
public enum MessageType {
    ODDS_CHANGE, BET_SETTLEMENT
}
