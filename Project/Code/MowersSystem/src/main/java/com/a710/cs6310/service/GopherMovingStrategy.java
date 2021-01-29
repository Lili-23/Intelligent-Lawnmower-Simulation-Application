package com.a710.cs6310.service;


import com.a710.cs6310.common.*;
import com.a710.cs6310.model.Gopher;
import com.a710.cs6310.model.Lawn;
import com.a710.cs6310.model.Mower;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GopherMovingStrategy implements GopherMovingInterface {
    private Lawn lawn;
    private List<MowerMovingInterface> mowerStrategies;
    private List<Gopher> gophers;
    private String _actionResult;

    public GopherMovingStrategy(Lawn lawn, List<MowerMovingInterface> mowerStrategies, List<Gopher> gophers) {
        this.lawn = lawn;
        this.mowerStrategies = mowerStrategies;
        this.gophers = gophers;
        this._actionResult = "";
    }

    @Override
    public void gopherAction(int id) {
        Gopher currentGopher = gophers.get(id);
        Pair<Integer, List<Mower>> closestMowers = findClosetMowers(id);

        if (closestMowers.getValue().isEmpty()) {
            return;
        }

        int selectIndex = 0;
        if (closestMowers.getValue().size() > 1) {
            selectIndex = new Random().nextInt(closestMowers.getValue().size());
        }

        Mower targetMower = closestMowers.getValue().get(selectIndex);
        // Can't use the position from mower which may be wrong.
        // because it can be relative
        Point mowerPos = lawn.getMowerPos().get(targetMower.getId());
        if (closestMowers.getKey().intValue() == 1) {
            // crash the mower
            currentGopher.setCurrentLocation(mowerPos);
            lawn.updateForMowerCrash(mowerPos);
            targetMower.setCurrentState(MowerState.CRASH);
            lawn.updateForGopherMove(id, mowerPos);
            _actionResult = "g" + id + ": crack m:" + targetMower.getId();
        } else {
            Point currentPos = currentGopher.getCurrentLocation();
            Direction direction = PosDirectUtil.calculateGopherDirection(currentPos, mowerPos);

            Point nextPos = PosDirectUtil.getMoveToPosition(currentPos, direction);
            if (!isOccupied(nextPos)) {
                lawn.updateForGopherMove(id, nextPos);
                currentGopher.setCurrentLocation(nextPos);
                _actionResult = "g" + id + ": move " + direction.name();
            } else {
                _actionResult = "g" + id + ": stay";
            }

            SimpleLog.getInstance().log(_actionResult + "," + currentPos.toString() + "->" + nextPos.toString());
        }
    }

    @Override
    public String getActionMessage() {
        return _actionResult;
    }

    /*
    ** Get the closest no crash mowers
     */
    private Pair<Integer, List<Mower>> findClosetMowers(int gopherIndex) {
        int dist = Integer.MAX_VALUE;
        Point gopherPos = gophers.get(gopherIndex).getCurrentLocation();
        List<Mower> candidates = new ArrayList<>();

        for (int i = 0; i < lawn.getMowerPos().size(); i++) {
            Mower mower = mowerStrategies.get(i).getMower();
            if (mower.getCurrentState().equals(MowerState.CRASH)) {
                continue;
            }

            Point mowerPos = lawn.getMowerPos().get(i);
            int curDist = Math.abs(gopherPos.getPosX() - mowerPos.getPosX())+
                    Math.abs(gopherPos.getPosY() - mowerPos.getPosY());
            if (dist > curDist) {
                dist = curDist;
                candidates.clear();
                candidates.add(mower);
            } else if (dist == curDist) {
                candidates.add(mower);
            }
        }

        return Pair.of(dist, candidates);
    }

    /*
     ** Check other gopher is occupying this position or not
     */
    private boolean isOccupied(Point pos) {
        for (Gopher item : gophers) {
            if (item.getCurrentLocation().equals(pos)) {
                return true;
            }
        }
        return false;
    }
}
