package com.sporty.service.standardization.model;

public enum Result {
    HOME("1"),
    DRAW("X"),
    AWAY("2");

    public final String symbol;

    private Result(String symbol){
        this.symbol = symbol;
    }
}
