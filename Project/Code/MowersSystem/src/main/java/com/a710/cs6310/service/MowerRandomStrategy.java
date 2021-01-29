package com.a710.cs6310.service;

import com.a710.cs6310.common.Direction;
import com.a710.cs6310.common.MowerAction;
import com.a710.cs6310.common.Point;
import com.a710.cs6310.model.Lawn;
import com.a710.cs6310.model.Mower;
import com.a710.cs6310.model.SharedData;

import java.util.*;


public class MowerRandomStrategy implements MowerMovingInterface {
    Mower _mower;
    Lawn _lawn;

    //only use for check charging pad only
    List<Point> _mowerOriPos = null;
    private static Random randGenerator = new Random();

    class ActionResult {
        MowerAction _action;
        Direction _direct;

        ActionResult() {
            _action = MowerAction.NONE;
        }
    }

    public MowerRandomStrategy(Mower mover, Lawn lawn) {
        this._mower = mover;
        this._lawn = lawn;
    }


    @Override
    public Mower getMower() {
        return _mower;
    }

    @Override
    public SharedData getSharedData() {
        return null;
    }

    @Override
    public void pollMowerForAction() {
        if (_mowerOriPos == null) {
            _mowerOriPos = new ArrayList<>(_lawn.getMowerPos());
            _mower.setCurrentPos(_mowerOriPos.get(_mower.getId()));
            _mower.setStartPosInLawn(_mowerOriPos.get(_mower.getId()));
        }

        ActionResult actionResult = getRandomAction();
        execMowerAction(actionResult);
    }

    /*
     ** Generate random action for the mower, if steer generate random direction based's sample code
     */
    private ActionResult getRandomAction() {
        int choice = randGenerator.nextInt(100);
        ActionResult result = new ActionResult();

        if (choice < 10) {
            // do nothing
            result._action = MowerAction.PASS;
        } else if (choice < 15) {
            // check your surroundings
            result._action = MowerAction.LSCAN;
        } else if (choice < 35) {
            // check your surroundings
            result._action = MowerAction.CSCAN;
        } else if (choice < 60) {
            // change direction
            result._action = MowerAction.STEER;
        } else {
            // move forward
            result._action = MowerAction.MOVE;
        }

        choice = randGenerator.nextInt(100);
        Direction curDirect = _mower.getCurrentDirect();

        if (MowerAction.STEER.equals(result._action) && choice < 85) {
            int ptr = 0;

            while(!curDirect.equals((Direction.values()[ptr])) && ptr < Direction.values().length) {
                ptr++;
            }
            result._direct = Direction.values()[(ptr + 1) % Direction.values().length];
        } else {
            result._direct = curDirect;
        }

        return result;
    }

    /*
     ** Execute mower for action
     */
    private void execMowerAction(ActionResult actionResult) {
        boolean inChangePad = isInChargingPad();

        switch (actionResult._action) {
            case STEER:
                _mower.steer(actionResult._direct, inChangePad);
                break;

            case LSCAN:
                _mower.setLScanResult(_lawn.lscanForMower(_mower.getId(), actionResult._direct), actionResult._direct)
                        .lscan(inChangePad);
                break;

            case CSCAN:
                _mower.setCScanResult(_lawn.cscanForMower(_mower.getId()))
                        .cscan(inChangePad);
                break;

            case MOVE:
                _mower.move();
                break;
        }
    }

    /*
     ** Check current position is in charging pad or not
     */
    public boolean isInChargingPad() {
        Point curPosInLawn = _lawn.getMowerPos().get(_mower.getId());
        return _mowerOriPos.stream().anyMatch(item ->
                curPosInLawn.equals(item));
    }
}
