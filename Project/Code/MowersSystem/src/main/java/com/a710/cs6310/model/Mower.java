package com.a710.cs6310.model;

import com.a710.cs6310.common.*;
import com.google.common.annotations.VisibleForTesting;

import java.util.*;

import static com.a710.cs6310.common.PosDirectUtil.NEIGHBOR_OFFSET;

public class Mower {
    @VisibleForTesting
    public final static Map<MowerAction, Integer> ACTION_COST = new HashMap<MowerAction, Integer>() {
        {
            put(MowerAction.LSCAN, 3);
            put(MowerAction.MOVE, 2);
            put(MowerAction.STEER, 1);
            put(MowerAction.CSCAN, 1);
            put(MowerAction.PASS, 0);
        }
    };

    private int _id;
    private int _maxEnergy;
    private int _currentEnergy;

    // absolution position in lawn, will use lscan to get it
    private Point _startPosInLawn;

    private Point _lastPos;
    private Point _currentPos;
    private Direction _currentDirect;
    private MowerAction _currentAction;
    private MowerState _currentState;
    private List<Pair<Point, ScanResultType>> _scanResult = new ArrayList<>();
    private List<Point> _path = new ArrayList<>();

    // For back tracing
    private Stack<Point> _track = new Stack<>();
    private HashSet<String> _history = new HashSet<>();

    public Mower(Direction direction, int id, int maxEnergy) {
        _currentDirect = direction;
        _currentState = MowerState.ACTIVE;
        _startPosInLawn = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        _currentPos = _startPosInLawn;
        _currentAction = MowerAction.NONE;
        _id = id;
        _maxEnergy = maxEnergy;
        _currentEnergy = maxEnergy;
    }

    /*
     ** Get mower index
     */
    public final int getId() {
        return _id;
    }

    /*
     ** Execute steer action
     */
    public void steer(Direction direction, boolean inChargePad) {
        _currentAction = MowerAction.STEER;
        _currentDirect = direction;
        if (!inChargePad) {
            _currentEnergy -= ACTION_COST.get(MowerAction.STEER);
        }
    }

    /*
     ** Execute pass action
     */
    public void pass() {
        _currentAction = MowerAction.PASS;
    }

    /*
     ** Execute lscan action
     */
    public void lscan(boolean inChargePad) {
        _currentAction = MowerAction.LSCAN;
        if (!inChargePad) {
            _currentEnergy -= ACTION_COST.get(MowerAction.LSCAN);
        }
    }

    /*
     ** Execute cscan action
     */
    public void cscan(boolean inChargePad) {
        _currentAction = MowerAction.CSCAN;
        if (!inChargePad) {
            _currentEnergy -= ACTION_COST.get(MowerAction.CSCAN);
        }
    }

    /*
     ** Execute move action
     */
    public void move() {
        _currentAction = MowerAction.MOVE;
        _lastPos = _currentPos;
        _currentEnergy -= ACTION_COST.get(MowerAction.MOVE);
        _currentPos = PosDirectUtil.getMoveToPosition(_currentPos, _currentDirect);
        _path.add(_currentPos);
        if (!_history.contains(_currentPos.toString())) {
            _track.push(_currentPos);
            _history.add(_currentPos.toString());
        }
    }

    /*
     ** Set start pos in lawn after using lscan
     */
    private Mower setStartPos() {
        _currentPos = _startPosInLawn;
        _path.add(_currentPos);
        _track.push(_currentPos);
        _history.add(_currentPos.toString());
        return this;
    }


    /*
     ** Get the scan result of this mower
     */
    public final List<ScanResultType> getScanResult() {
        List<ScanResultType> result = new ArrayList<>();
        _scanResult.forEach(item ->
                result.add(item.getValue()));
        return result;
    }

    /*
     ** Get the scan result of this mower with their positions
     */
    public final List<Pair<Point, ScanResultType>> getMappedScanResult() {
        return _scanResult;
    }

    /*
     ** Set the cscan result of this mower
     */
    public final Mower setCScanResult(List<ScanResultType> scanResult) {
        _scanResult.clear();
        for (int i = 0; i < scanResult.size(); i++) {
            Point neighbor = PosDirectUtil.getMoveToPosition(_currentPos, Direction.getDirection(i));
            _scanResult.add(Pair.of(neighbor, scanResult.get(i)));
        }
        return this;
    }

    /*
     ** Set the lscan result of this mower
     */
    public final Mower setLScanResult(List<ScanResultType> scanResult, Direction direction) {
        _scanResult.clear();
        List<Integer> offset = NEIGHBOR_OFFSET.get(direction);
        Point next = new Point(_startPosInLawn.getPosX() + offset.get(0),
                _startPosInLawn.getPosY() + offset.get(1));

        for (int i = 0; i < scanResult.size(); i++) {
            _scanResult.add(Pair.of(next, scanResult.get(i)));
            next.setPosX(next.getPosX() + offset.get(0));
            next.setPosY(next.getPosY() + offset.get(1));
        }

        if (_path.isEmpty()) {
            if (direction.equals(Direction.WEST)) {
                _startPosInLawn.setPosX(scanResult.size() - 1);
            }

            if (direction.equals(Direction.SOUTH)) {
                _startPosInLawn.setPosY(scanResult.size() - 1);
            }

            // If this is the second lscan, need to set the start pos as absolute
            // position, all the following would be based on absolute position in Lawn
            if ((Integer.MAX_VALUE != _startPosInLawn.getPosX()) &&
                    (Integer.MAX_VALUE != _startPosInLawn.getPosY())) {
                setStartPos();
            }
        }
        return this;
    }

    /*
     ** Get current state of this mower
     */
    public final MowerState getCurrentState() {
        return _currentState;
    }

    /*
     ** Set current state of this mower
     */
    public Mower setCurrentState(MowerState state) {
        SimpleLog.getInstance().log(String.format("setCurrentState: %s -> %s", _currentState, state));
        _currentState = state;
        return this;
    }

    /*
     ** Get current direction of this mower
     */
    public final Direction getCurrentDirect() {
        return _currentDirect;
    }


    /*
     ** Get current action of this mower
     */
    public final MowerAction getCurrentAction() {
        return _currentAction;
    }

    /*
     ** Get last position of this mower
     */
    public final Point getLastPos() {
        return _lastPos;
    }

    /*
     ** Get max energy of this mower
     */
    public final int getMaxEnergy() {
        return _maxEnergy;
    }

    /*
     ** Set max energy of this mower
     */
    public final Mower setMaxEnergy(int maxEnergy) {
        this._maxEnergy = maxEnergy;
        return this;
    }

    /*
     ** Get current energy of this mower
     */
    public final int getCurrentEnergy() {
        return _currentEnergy;
    }

    /*
     ** Set current energy of this mower
     */
    public final Mower setCurrentEnergy(int energy) {
        _currentEnergy = energy;
        return this;
    }

    /*
     ** Get visited cells
     */
    public final HashSet<String> getVisited() {
        return _history;
    }

    /*
     ** Get mover's track
     */
    public final Stack<Point> getTrack() {
        return _track;
    }

    /*
     ** Get mover's path
     */
    public final List<Point> getPath() {
        return _path;
    }

    /*
     ** Get mover's position in the lawn
     */
    public final Point getPosInLawn() {
        return _startPosInLawn;
    }

    /*
     ** Get mover's relative position
     */
    public final Point getCurrentPos() {
        return _currentPos;
    }


    /*
     ** Set mover's relative position
     */
    public final Mower setCurrentPos(Point currentPos) {
        this._currentPos = new Point(currentPos);
        return this;
    }

    /*
     ** Set mover's relative position
     */
    public final Mower setStartPosInLawn(Point currentPos) {
        this._startPosInLawn = new Point(currentPos);
        return this;
    }
}
