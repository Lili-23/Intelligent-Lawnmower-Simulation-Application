package com.a710.cs6310.service;


import com.a710.cs6310.common.*;
import com.a710.cs6310.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MowersSystem {
    private static final MowersSystem _instance = new MowersSystem();
    private static final String RESOURCE_DATA = "src/main/resources/data/";

    final String DELIMITER = ",";
    private int _turnLimit;
    private int _numberOfMowers;
    private int _numberOfGophers;
    private int _lawnWidth;
    private int _lawnHeight;
    private Lawn _lawn;
    private int _currentTurn;
    private int _gopherTurnGap;
    private List<MowerMovingInterface> _mowerStrategy;
    private GopherMovingStrategy _gopherStrategy;
    private boolean validateResult;


    private MowersSystem() {
        init();
    }

    public int getCurrentTurn() {
        return _currentTurn;
    }

    public MowersSystem nextTurn() {
        _currentTurn++;
        return this;
    }

    public int getTurnLimit() {
        return _turnLimit;
    }

    public Lawn getLawn() {
        return _lawn;
    }

    public int getGopherTurnGap() {
        return _gopherTurnGap;
    }

    public int getNumberOfGophers() {
        return _numberOfGophers;
    }

    public List<Point> getMowerPosInLawn() {
        return _lawn.getMowerPos();
    }

    public List<MowerMovingInterface> getMowerStrategy() {
        return _mowerStrategy;
    }

    public static MowersSystem getInstance() {
        return _instance;
    }

    private void init() {
        _currentTurn = 1;
        validateResult = true;
        _mowerStrategy = new ArrayList<>();
    }

    /*
     ** Get the validation result
     */
    public String getValidateResult() {
        return validateResult ? "ok" : "crash";
    }

    /*
     ** Load the information from the given csv file
     */
    public void load(String fileName) {
        init();
        Scanner takeCommand = null;
        try {
            takeCommand = new Scanner(new File(RESOURCE_DATA + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        loadWithFileStream(takeCommand);
    }

    /*
     ** Load the information from the given csv file
     */
    public void load(InputStream fileStream) {
        init();
        loadWithFileStream(new Scanner(fileStream));
    }


    /*
     ** Load the information from the given csv file
     */
    public void loadWithFileStream(Scanner takeCommand) {
        String[] tokens;

        // read in the lawn information
        tokens = takeCommand.nextLine().split(DELIMITER);
        _lawnWidth = Integer.parseInt(tokens[0]);
        tokens = takeCommand.nextLine().split(DELIMITER);
        _lawnHeight = Integer.parseInt(tokens[0]);

        // generate the lawn information
        Item[][] lawnInfo = new Item[_lawnHeight][_lawnWidth];
        for (int y = 0; y < _lawnHeight; y++) {
            for (int x = 0; x < _lawnWidth; x++) {
                lawnInfo[y][x] = new Grass(new Point(x, y));
            }
        }
        _lawn = new Lawn(lawnInfo);

        // read in the lawnmower starting information
        tokens = takeCommand.nextLine().split(DELIMITER);
        _numberOfMowers = Integer.parseInt(tokens[0]);

        SharedData sharedData = new SharedData();
        for (int k = 0; k < _numberOfMowers; k++) {
            tokens = takeCommand.nextLine().split(DELIMITER);
            int x = Integer.parseInt(tokens[0]);
            int y = Integer.parseInt(tokens[1]);
            int strategy = Integer.parseInt(tokens[3]);
            Point pos = new Point(x, y);
            Direction direction = Direction.valueOf(tokens[2].toUpperCase());

            //TODO for other strategy
            if (MowerStrategy.OPTIMAL.getValue() == strategy) {
                _mowerStrategy.add(new MowerOptimalStrategy(new Mower(direction, k, 0), _lawn, sharedData));
            } else {
                _mowerStrategy.add(new MowerRandomStrategy(new Mower(direction, k, 0), _lawn));
            }
            // mow the grass at the initial location
            _lawn.setMowerPos(pos);
        }

        // read in the maximal energy information
        tokens = takeCommand.nextLine().split(DELIMITER);
        int maxEnergy = Integer.parseInt(tokens[0]);
        _mowerStrategy.forEach(item ->
                item.getMower().setMaxEnergy(maxEnergy)
                        .setCurrentEnergy(maxEnergy));

        // read in the gopher information
        tokens = takeCommand.nextLine().split(DELIMITER);
        _numberOfGophers = Integer.parseInt(tokens[0]);
        List<Gopher> gophers = new ArrayList<>();
        for (int k = 0; k < _numberOfGophers; k++) {
            tokens = takeCommand.nextLine().split(DELIMITER);
            int x = Integer.parseInt(tokens[0]);
            int y = Integer.parseInt(tokens[1]);
            Point pos = new Point(x, y);

            // place a crater at the given location
            _lawn.setGopherPos(pos);
            gophers.add(new Gopher(pos));
        }
        _gopherStrategy = new GopherMovingStrategy(_lawn, _mowerStrategy, gophers);

        // number of turns between gopher moves
        tokens = takeCommand.nextLine().split(DELIMITER);
        _gopherTurnGap = Integer.parseInt(tokens[0]);

        // maximum number of turns
        tokens = takeCommand.nextLine().split(DELIMITER);
        _turnLimit = Integer.parseInt(tokens[0]);

        takeCommand.close();
    }

    /*
     ** Poll the current mower for action and validate the action
     */
    public void pollMowerForAction(int index) {
        Mower mower = _mowerStrategy.get(index).getMower();
        Point prePos = new Point(mower.getCurrentPos());
        int preEnergy = mower.getCurrentEnergy();

        _mowerStrategy.get(index).pollMowerForAction();
        validateResult = true;
        if (mower.getCurrentEnergy() < 0) {
            validateResult = false;
        } else {
            switch (mower.getCurrentAction()) {
                case LSCAN:
                case CSCAN:
                    validateResult = checkForScan(_mowerStrategy.get(index), mower.getCurrentAction(),
                                                    preEnergy);
                    break;

                case MOVE:
                    validateResult = checkForMove(_mowerStrategy.get(index), mower.getCurrentDirect(),
                                                    preEnergy);
                    break;

                case PASS:
                    validateResult = checkForPass(_mowerStrategy.get(index), mower.getCurrentAction(),
                            mower.getCurrentDirect(), preEnergy, prePos);
                    break;

                case STEER:
                    validateResult = checkForSteer(_mowerStrategy.get(index), preEnergy, prePos);
                    break;
            }
        }

        if (!validateResult) {
            mower.setCurrentState(MowerState.CRASH);
            _lawn.updateForMowerCrash(_lawn.getMowerPos().get(index));
        } else if (!crashWithGopher(index) && !crashWithMower(index)) {
            updateMowerInfo(index);
        }
    }

    public void pollGopherForAction(int idx) {
        _gopherStrategy.gopherAction(idx);
    }

    /*
     ** Get action result of the current gopher
     */
    public String getGopherActionResult() {
        return _gopherStrategy.getActionMessage();
    }


    /*
     ** Get action result of the current mower
     */
    public String getMowerActionResult(int index) {
        return _mowerStrategy.get(index).getActionMessage();
    }

    /*
     ** Get the total active mower
     */
    public int getActiveMowerNumber() {
        return _mowerStrategy.stream().filter(item -> item.getMower().getCurrentState()
                .equals(MowerState.ACTIVE)).collect(Collectors.toList()).size();
    }

    /*
     ** Check whether game is over or not
     */
    public boolean isGameOver() {
        if (getActiveMowerNumber() == 0) {
            SimpleLog.getInstance().log("No active mower");
            return true;
        }

        if (_lawn.getGrassRemains() == 0) {
            SimpleLog.getInstance().log("No more grass");
            return true;
        }

        if (_currentTurn >= _turnLimit) {
            SimpleLog.getInstance().log("Exceed turn limit");
            return true;
        }

        return false;
    }

    /*
     ** Check of energy usage for scan
     */
    private boolean checkForScan(MowerMovingInterface strategy, MowerAction action, int preEnergy) {
        Mower mower = strategy.getMower();

        if (!mower.getCurrentAction().equals(action)) {
            return false;
        }

        if (isInChargingPad(mower.getId())) {
            return mower.getCurrentEnergy() == mower.getMaxEnergy();
        }

        return preEnergy == mower.getCurrentEnergy() + MowerEnergyMap.ACTION_COST.get(action);
    }

    /*
     ** Check of move for mower
     */
    private boolean checkForMove(MowerMovingInterface strategy, Direction direction, int preEnergy) {
        Mower mower = strategy.getMower();

        Point prePos1 = mower.getLastPos();
        if (!direction.equals(mower.getCurrentDirect())
                || !mower.getCurrentAction().equals(MowerAction.MOVE)) {
            return false;
        }

        Point expectedPos = PosDirectUtil.getMoveToPosition(prePos1, direction);
        if (!expectedPos.equals(mower.getCurrentPos())) {
            return false;
        }


        Point posInLawn = _lawn.getMowerPos().get(mower.getId());
        Point newPosInLawn = PosDirectUtil.getMoveToPosition(posInLawn, mower.getCurrentDirect());
        if (!_lawn.isPosValid(newPosInLawn)) {
            return false;
        }
//        if (mower.getPosInLawn().equals(expectedPos) || strategy.getSharedData().isInChargingPad(expectedPos, mower)) {
//            return mower.getCurrentEnergy() == mower.getMaxEnergy();
//        }
        if (crashWithGopher(strategy.getMower().getId())) {
            return false;
        }

        return preEnergy == mower.getCurrentEnergy() + MowerEnergyMap.ACTION_COST.get(MowerAction.MOVE);
    }

    /*
     ** Check of pass for mower
     */
    private boolean checkForPass(MowerMovingInterface strategy, MowerAction action, Direction direction,
                                 int preEnergy, Point prePos) {
        Mower mower = strategy.getMower();


        return preEnergy == mower.getCurrentEnergy()
                && action.equals(mower.getCurrentAction())
                && prePos.equals(mower.getCurrentPos())
                && direction.equals(mower.getCurrentDirect());
    }

    /*
     ** Check of steer for mower
     */
    private boolean checkForSteer(MowerMovingInterface strategy, int preEnergy, Point prePos) {
        Mower mower = strategy.getMower();
        if (!prePos.equals(mower.getCurrentPos())
                || !mower.getCurrentAction().equals(MowerAction.STEER)) {
            return false;
        }

        if (isInChargingPad(mower.getId())) {
            return mower.getCurrentEnergy() == mower.getMaxEnergy();
        }

        return preEnergy == mower.getCurrentEnergy() + MowerEnergyMap.ACTION_COST.get(MowerAction.STEER);
    }

    /*
    ** Update mower info for successful move
     */
    private void updateMowerInfo(int mowerIndex) {
        Mower mower = _mowerStrategy.get(mowerIndex).getMower();
        if (!validateResult || !mower.getCurrentAction().equals(MowerAction.MOVE)) {
            return;
        }

        Point mowerNewPos = PosDirectUtil.getMoveToPosition(_lawn.getMowerPos().get(mower.getId()),
                mower.getCurrentDirect());
        _lawn.updateForMowerMove(mowerIndex, mowerNewPos);

        // reach the charging pad
        if (_mowerStrategy.stream().anyMatch(item ->
                item.getMower().getPosInLawn().equals(mowerNewPos))) {
            mower.setCurrentEnergy(mower.getMaxEnergy());
        }
    }

    /*
    ** Check for moving to gopher position
     */
    private boolean crashWithGopher(int currMowerIndex) {
//        Point curMowerPos = _lawn.getMowerPos().get(currMowerIndex);
        Mower mower = _mowerStrategy.get(currMowerIndex).getMower();
        Point curMowerPos = mower.getCurrentPos();
        for (Point gopherPos : _lawn.getGopherPos()) {
            if (gopherPos.equals(curMowerPos)) {
                // Only for move case, if not move then for this case gopher should already crack it.
                mower.setCurrentState(MowerState.CRASH);
                _lawn.updateForMowerCrash(mower.getLastPos());
                return true;
            }
        }
        return false;
    }

    /*
     ** Check for collision with other gopher if have
     */
    private boolean crashWithMower(int currMowerIndex) {
        for (int i = 0; i < _lawn.getMowerPos().size(); i ++) {
            if (currMowerIndex == i ||
                    !_mowerStrategy.get(i).getMower().getCurrentState().equals(MowerState.ACTIVE)) {
                continue;
            }

            // Collide with other mower
            if (_mowerStrategy.get(currMowerIndex).getMower().getCurrentPos()
                    .equals(_lawn.getMowerPos().get(i))) {
                _lawn.updateForMowerCrash(_lawn.getMowerPos().get(i));
                _mowerStrategy.get(i).getMower().setCurrentState(MowerState.CRASH);
                _mowerStrategy.get(currMowerIndex).getMower().setCurrentState(MowerState.CRASH);
                return true;
            }
        }
        return false;
    }

    /*
     ** Check for collision with other gopher if have
     */
    private boolean isInChargingPad(int mowerIndex) {
        Point mowerPos = _lawn.getMowerPos().get(mowerIndex);
        return _lawn.getChargePadPos().stream().anyMatch(item ->
                item.equals(mowerPos));
    }
}
