package com.sporty.service.standardization.model;

public enum MatchResult {
    HOME("1"),
    DRAW("X"),
    AWAY("2");

    public final String symbol;

    private MatchResult(String symbol){
        this.symbol = symbol;
    }
}
