package com.a710.cs6310.common;

public class Pair<U, V> {
    public final U _first;
    public final V _second;

    private Pair(U first, V second) {
        this._first = first;
        this._second = second;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?,?>) other;

        return _first.equals(pair._first) && _second.equals(pair._second);
    }

    @Override
    public String toString() {
        return "(" + _first + ", " + _second + ")";
    }

    public final V getValue() {
        return _second;
    }

    public final U getKey() {
        return _first;
    }
    public static <U, V> Pair <U, V> of(U first, V second) {
        return new Pair<>(first, second);
    }
}
