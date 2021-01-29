package com.a710.cs6310.common;

public enum MowerStrategy {
    RANDOM(0),
    OPTIMAL(1);

    private int _val;

    MowerStrategy(int val) {
        this._val = val;
    }

    public int getValue() {
        return this._val;
    }
}
