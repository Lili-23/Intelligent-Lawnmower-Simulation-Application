package com.a710.cs6310.service;

import com.a710.cs6310.common.*;
import com.a710.cs6310.model.Lawn;
import com.a710.cs6310.model.Mower;
import com.a710.cs6310.model.SharedData;


import java.util.*;
import java.util.stream.Collectors;

import static com.a710.cs6310.common.MowerAction.*;

/*
 ** Current guideline for strategy is:
 **  1. Use lscan and steer Get absolute position in the lawn
 **      In the start point which has charging pad as well, any action will not consume energy.
 **  2. Use cscan to the all neighbors
 **  3. Check the scan result, to make sure move to the right direction.
 **
 */
public class MowerOptimalStrategy implements MowerMovingInterface {
    Mower _mower;
    Lawn _lawn;
    SharedData _sharedData;
    boolean _inLowEnergy;
    Map<String, ScanResultType> _validScanResult;
    Set<String> _scanPositions;

    class ActionResult {
        MowerAction _action;
        Direction _direct;

        ActionResult() {
            _action = MowerAction.NONE;
        }
    }

    public MowerOptimalStrategy(Mower mover, Lawn lawn, SharedData sharedData) {
        this._mower = mover;
        this._lawn = lawn;
        this._sharedData = sharedData;
        this._inLowEnergy = false;
        this._validScanResult = new HashMap<>();
        this._scanPositions = new HashSet<>();
    }

    @Override
    public void pollMowerForAction() {
        ActionResult actionResult = new ActionResult();

        checkPosInLawn(actionResult);
        checkMowerAction(actionResult);
        execMowerAction(actionResult);
        checkForSharedData();
    }

    @Override
    public final Mower getMower() {
        return _mower;
    }

    @Override
    public SharedData getSharedData() {
        return _sharedData;
    }

    /*
     ** Check for shared information
     */
    private void checkForSharedData() {
        // Only after know its absolute position then put in the sharedData
        Point posInLawn = _mower.getPosInLawn();
        if ((posInLawn.getPosX() != Integer.MAX_VALUE)
                && (posInLawn.getPosY() != Integer.MAX_VALUE)) {
            _sharedData.registerMower(_mower);
        }
    }

    /*
     **  Check the absolute position in lawn
     */
    private void checkPosInLawn(ActionResult actionResult) {
        Point posInLawn = _mower.getPosInLawn();

        // Already have absolute position in lawn, no more action for this step
        if ((Integer.MAX_VALUE != posInLawn.getPosX())
                && (Integer.MAX_VALUE != posInLawn.getPosY())) {
            return;
        }

        // Check whether need to steer to get the position
        if (_mower.getPosInLawn().getPosX() == Integer.MAX_VALUE
                && _mower.getCurrentDirect().equals(Direction.WEST)) {
            actionResult._action = MowerAction.LSCAN;
            actionResult._direct = Direction.WEST;
            return;
        }

        if (_mower.getPosInLawn().getPosY() == Integer.MAX_VALUE
                && _mower.getCurrentDirect().equals(Direction.SOUTH)) {
            actionResult._action = MowerAction.LSCAN;
            actionResult._direct = Direction.SOUTH;
            return;
        }

        // Steer to south to get y
        if (_mower.getPosInLawn().getPosX() == Integer.MAX_VALUE) {
            actionResult._action = MowerAction.STEER;
            actionResult._direct = Direction.WEST;
            return;
        }

        // Steer to west to get x
        if (_mower.getPosInLawn().getPosY() == Integer.MAX_VALUE) {
            actionResult._action = MowerAction.STEER;
            actionResult._direct = Direction.SOUTH;
            return;
        }
    }

    /*
     ** Check the necessary for cscan
     */
    private void checkMowerAction(ActionResult result) {
        if (!result._action.equals(MowerAction.NONE)) {
            return;
        }

        if (_mower.getCurrentAction().equals(LSCAN)) {
            result._action = CSCAN;
        } else if (_mower.getCurrentAction().equals(STEER)) {
            // Maybe need second steer, in case of other mower is next position
            handleDirectMove(result);
        } else if (_mower.getCurrentAction().equals(CSCAN)) {
            handleScanResult(result);
        } else if (_mower.getCurrentAction().equals(MOVE)) {
            if (_validScanResult.isEmpty()) {
                result._action = CSCAN;
            } else {
                handleNeighbors(result);
            }
        }
    }

    /*
     ** Handle for the scan result
     */
    private void handleScanResult(ActionResult result) {
        if (!result._action.equals(MowerAction.NONE)) {
            return;
        }

        // Need to check for move back
        if (_validScanResult.isEmpty()) {
            if ((_mower.getTrack().size() > 1)
                && _mower.getTrack().peek().equals(_mower.getCurrentPos())) {
                _mower.getTrack().pop();
                result._direct = PosDirectUtil.calculateDirection(_mower.getCurrentPos(),  _mower.getTrack().peek());
                result._action = STEER;
            }
        } else {
            handleDirectMove(result);
        }
    }

    /*
     ** Check whether next move is valid or not based on current direction
     */
    private boolean isNextMoveValid(Direction direction) {
        Point next = PosDirectUtil.getMoveToPosition(_mower.getCurrentPos(), direction);

        // Use shared information to check whether other mower occupy it or not
        if (_sharedData.isUsedByOtherMower(next, _mower)) {
            return false;
        }

        // Move back
        if (!_mower.getTrack().empty() && _mower.getTrack().peek().equals(next)) {
            return true;
        }

        //check other unvisited grid
        return !_mower.getVisited().contains(next.toString())
                && _mower.getMappedScanResult().stream()
                .map(Pair::getKey).map(Point::toString).collect(Collectors.toList()).contains(next.toString())
                && canMoveto(next);
    }

    /*
     ** Check whether can move to pos
     */
    private boolean canMoveto(Point pos) {
        Pair<Point, ScanResultType> candidate = _mower.getMappedScanResult()
                .stream().filter(item -> item.getKey().equals(pos)).findFirst().get();

        return candidate.getValue().equals(ScanResultType.GRASS)
                || candidate.getValue().equals(ScanResultType.EMPTY);
    }

    /*
     ** Get the available direction for steer
     */
    private Direction getAvailableDirection() {
        Point mowerPos = _mower.getCurrentPos();
        Map<String, ScanResultType> scanResult = _mower.getMappedScanResult()
                .stream().collect(Collectors.toMap(item -> item.getKey().toString(), Pair::getValue));

        for (Direction direction: PosDirectUtil.NEIGHBOR_OFFSET.keySet()) {
            Point neighbor = new Point(mowerPos.getPosX() + PosDirectUtil.NEIGHBOR_OFFSET.get(direction).get(0),
                    mowerPos.getPosY() + PosDirectUtil.NEIGHBOR_OFFSET.get(direction).get(1));

            if (!_mower.getVisited().contains(neighbor.toString())
                    && !_sharedData.isUsedByOtherMower(neighbor, _mower)
                    && scanResult.containsKey(neighbor.toString()) && canMoveto(neighbor)) {
                return direction;
            }
        }

        Direction direction = _mower.getCurrentDirect();
        //steer to last grid in the track
        if (_mower.getTrack().size() > 1) {
            _mower.getTrack().pop();
            direction = PosDirectUtil.calculateDirection(_mower.getCurrentPos(),  _mower.getTrack().peek());
        }
        return direction;
    }

    /*
     ** Execute mower for action
     */
    private void execMowerAction(ActionResult actionResult) {
        boolean inChangePad = _sharedData.isInChargingPad(_mower.getCurrentPos(), _mower)
                || _mower.getPosInLawn().getPosX() == Integer.MAX_VALUE
                || _mower.getPosInLawn().getPosY() == Integer.MAX_VALUE;
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
                buildValidScanResult(_mower.getMappedScanResult());
                _scanPositions.add(_mower.getCurrentPos().toString());
                break;

            case MOVE:
                _mower.move();
                _validScanResult.remove(_mower.getCurrentPos().toString());
                break;
        }
    }

    /*
     ** Handle neighbor strategy
     */
    private void handleNeighbors(ActionResult result) {
        if (!result._action.equals(NONE)) {
            return;
        }

        Point pos = _mower.getCurrentPos();
        for (Direction direction: PosDirectUtil.NEIGHBOR_OFFSET.keySet()) {
            Point neighbor = new Point(pos.getPosX() + PosDirectUtil.NEIGHBOR_OFFSET.get(direction).get(0),
                    pos.getPosY() + PosDirectUtil.NEIGHBOR_OFFSET.get(direction).get(1));

            if (!_mower.getVisited().contains(neighbor.toString())
                    && _validScanResult.containsKey(neighbor.toString())) {
                result._action = STEER;
                result._direct = getAvailableDirection();
                return;
            }
        }

        result._action = CSCAN;
    }

    /*
     ** Build the valid scan result based on the cscan
     */
    private void buildValidScanResult(List<Pair<Point, ScanResultType>> scanResult) {
        _validScanResult.clear();
        scanResult.stream().filter(item -> (item.getValue().equals(ScanResultType.EMPTY)
                || item.getValue().equals(ScanResultType.GRASS))
                && !_mower.getVisited().contains(item.getKey().toString()))
                .forEach(item ->
                        _validScanResult.putIfAbsent(item.getKey().toString(), item.getValue()));
    }

    /*
     ** For handle the direct move strategy
     */
    private void handleDirectMove(ActionResult result) {
        if (!result._action.equals(NONE)) {
            return;
        }

        //TODO need to improve
        if (isNextMoveValid(_mower.getCurrentDirect())) {
            result._action = MOVE;
        } else {
            result._action = STEER;
            result._direct = getAvailableDirection();
        }
    }
}

