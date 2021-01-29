package com.a710.cs6310.model;

import com.a710.cs6310.common.Point;

import java.util.HashMap;
import java.util.Map;

//TODO need to use this data for better strategy
public final class SharedData {
    Map<Integer, Mower> _mowers = new HashMap<>();

    public Map<Integer, Mower> getMowers() {
        return _mowers;
    }

    public Boolean isInShared(Integer mowerId) {
        return _mowers.containsKey(mowerId);
    }

    public void registerMower(Mower mower) {
        _mowers.putIfAbsent(mower.getId(), mower);
    }

    /*
     **  Check the position is used by other mower or not
     */
    public boolean isUsedByOtherMower(Point position, Mower curMower) {
        return _mowers.values().stream().anyMatch(item ->
                item.getId() != curMower.getId()
                        && item.getCurrentPos().equals(position));
    }

    /*
     ** Check current position is in charging pad or not
     */
    public boolean isInChargingPad(Point point, Mower curMower) {
        return point.equals(curMower.getPosInLawn())
                || _mowers.values().stream().anyMatch(item ->
                    point.equals(item.getPosInLawn()));
    }
}
