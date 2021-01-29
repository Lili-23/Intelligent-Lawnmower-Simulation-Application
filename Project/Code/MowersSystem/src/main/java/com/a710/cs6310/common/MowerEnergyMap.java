package com.a710.cs6310.common;

import java.util.HashMap;
import java.util.Map;

public final class MowerEnergyMap {
    public final static Map<MowerAction, Integer> ACTION_COST = new HashMap<MowerAction, Integer>() {
        {
            put(MowerAction.LSCAN, 3);
            put(MowerAction.MOVE, 2);
            put(MowerAction.STEER, 1);
            put(MowerAction.CSCAN, 1);
            put(MowerAction.PASS, 0);
        }
    };

    private MowerEnergyMap() {
    }
}
