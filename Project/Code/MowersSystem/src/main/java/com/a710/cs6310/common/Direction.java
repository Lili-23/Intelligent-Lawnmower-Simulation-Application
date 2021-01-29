package com.a710.cs6310.common;

import java.util.HashMap;
import java.util.Map;

// enum including the 8 directions use in the system.
public enum Direction {
    NORTH(0),
    NORTHEAST(1),
    EAST(2),
    SOUTHEAST(3),
    SOUTH(4),
    SOUTHWEST(5),
    WEST(6),
    NORTHWEST(7);

    static final Map<Integer, Direction> _dataMap = new HashMap<Integer, Direction>() {
        {
            for (Direction type : Direction.values()) {
                put(type.getValue(), type);
            }
        }
    };

    private int _val;

    Direction(int val) {
        this._val = val;
    }

    public int getValue() {
        return this._val;
    }

    public static Direction getDirection(int val) {
        return _dataMap.get(val);
    }
}
