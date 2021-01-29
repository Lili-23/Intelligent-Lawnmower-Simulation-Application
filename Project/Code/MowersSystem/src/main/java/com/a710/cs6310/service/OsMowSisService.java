package com.a710.cs6310.service;

import com.a710.cs6310.common.*;
import com.a710.cs6310.model.Item;
import com.a710.cs6310.model.Lawn;
import com.a710.cs6310.model.Mower;
import com.a710.cs6310.model.form.SystemStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.List;

@Service
public class OsMowSisService implements AbstractSystemService {
    private MowersSystem _mowersSystem = MowersSystem.getInstance();
    private final static boolean SHOW_STATE = true;
    int _mowerTurnIndex = 0;
    int _gopherTurnIndex = 0;


    // upload file and return the origin system status object to controller layer
    @Override
    public SystemStatus readFile(String filename) {
        if (!StringUtils.isEmpty(filename)) {
            _mowersSystem.load(filename);
        }

        _mowerTurnIndex = 0;
        _gopherTurnIndex = 0;
        SystemStatus ss = generateSystemStatus(GameState.LOADED, "Simulation Start");
        return ss;
    }

    // request next TurnStatus, and return system status
    //if reach max turn, send message, and change state to "end"
    @Override
    public SystemStatus getNextTurnStatus() {
        if (_mowersSystem.isGameOver()) {
            SimpleLog.getInstance().log("Game over from single step!!!");
            return generateSystemStatus(GameState.END, "");
        }

        SimpleLog.getInstance().log("Current turn " + _mowersSystem.getCurrentTurn());
        String message = runCurrentStep();

        SystemStatus ss = generateSystemStatus(GameState.RUNNING, message);
        return ss;

    }

    @Override
    public SystemStatus quitSimulation() {
        // TODO need to modify
        // to do : final report
        SystemStatus ss = generateSystemStatus(GameState.END, "");
        SimpleLog.getInstance().log("Game quit !!!");
//        ss.setSummaryInfo("final report: ");
        return ss;
    }

    @Override
    public SystemStatus uploadFile(InputStream fileStream) {
        _mowersSystem.load(fileStream);
        _mowerTurnIndex = 0;
        _gopherTurnIndex = 0;

        SystemStatus ss = generateSystemStatus(GameState.LOADED, "");
        return ss;
    }

    SystemStatus generateSystemStatus(GameState gameState, String message) {
        showState();
        SystemStatus ss = new SystemStatus().setGameState(GameState.END.name());

        try {
            ss.setCurrentLawn(generateLawnInfo())
                    .setGameState(gameState.name())
                    .setCurrentTurn(_mowersSystem.getCurrentTurn())
                    .setTurnLimit(_mowersSystem.getTurnLimit())
                    .setMoverMaxEnergy(_mowersSystem.getMowerStrategy().get(0).getMower().getMaxEnergy())
                    .setMowerNumber(_mowersSystem.getMowerStrategy().size())
                    .setMowerInfo(generateMowerStateEnergy())
                    .setRemainGrass(_mowersSystem.getLawn().getGrassRemains())
                    .setTotalGrass(_mowersSystem.getLawn().getGrassTotal())
                    .setGopherNumber(_mowersSystem.getNumberOfGophers())
                    .setMessage(message)
                    .setLawnHeight(_mowersSystem.getLawn().getHeight())
                    .setLawnWidth(_mowersSystem.getLawn().getWidth());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO for message
        return ss;
    }

    /*
     ** Execute the current step
     */
    String runCurrentStep() {
        String result = "";
        // Gopher's turn
        if (0 == (_mowersSystem.getCurrentTurn() % _mowersSystem.getGopherTurnGap())) {
            _mowersSystem.pollGopherForAction(_gopherTurnIndex);
            result = _mowersSystem.getGopherActionResult();
            SimpleLog.getInstance().log(result);
            _gopherTurnIndex++;
            if (_gopherTurnIndex == _mowersSystem.getNumberOfGophers()) {
                _gopherTurnIndex = 0;
                _mowersSystem.nextTurn();
            }
        } else {
            int activeMowerIndex = getActiveMowerIndex(_mowerTurnIndex);
            _mowersSystem.pollMowerForAction(activeMowerIndex);

            result = _mowersSystem.getMowerActionResult(activeMowerIndex) + "\n" + _mowersSystem.getValidateResult();

            SimpleLog.getInstance().log(result);
            _mowerTurnIndex++;
            if (_mowerTurnIndex == _mowersSystem.getMowerStrategy().size()) {
                _mowerTurnIndex = 0;
                _mowersSystem.nextTurn();
            }
        }

        return result;
    }

    /*
     ** Get active mover from the current index in order
     */
    private int getActiveMowerIndex(int startIndex) {
        List<MowerMovingInterface> mowerStrategy = _mowersSystem.getMowerStrategy();
        int total = mowerStrategy.size();
        for (int index = startIndex; index < total + startIndex; index++) {
            Mower mower = mowerStrategy.get(index % total).getMower();
            if (mower.getCurrentState().equals(MowerState.ACTIVE)) {
                return index % total;
            }
        }

        return 0;
    }

    /*
     ** Generate lawn info for the result
     */
    private String[][] generateLawnInfo() {
        Item[][] items = _mowersSystem.getLawn().getAllGrid();
        String[][] lawnInfo = new String[items.length][items[0].length];

        int height = items.length;
        int width = items[0].length;
        // for non mower cell
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!items[i][j].toScanResult().equals(ScanResultType.MOWER)) {
                    lawnInfo[height - i - 1][j] = items[i][j].toScanResult().name();
                }
            }
        }

        // for mower cell
        List<Point> mowerPosInLawn = _mowersSystem.getMowerPosInLawn();
        List<MowerMovingInterface> mowerStrategy = _mowersSystem.getMowerStrategy();
        for (int i = 0; i < mowerPosInLawn.size(); i++) {
            if (!mowerStrategy.get(i).getMower().getCurrentState().equals(MowerState.CRASH)) {
                lawnInfo[height - 1 - mowerPosInLawn.get(i).getPosY()][mowerPosInLawn.get(i).getPosX()] =
                        ScanResultType.MOWER.name() + "_" +
                                mowerStrategy.get(i).getMower().getCurrentDirect().name();
            }
        }

        // apply charging pad
        for (Point point : _mowersSystem.getLawn().getChargePadPos()) {
            lawnInfo[height - 1 - point.getPosY()][point.getPosX()] += "_CHARGING";
        }

        return lawnInfo;
    }

    /*
     ** Generate mower's state and energy for the result
     */
    private String[][] generateMowerStateEnergy() {
        List<MowerMovingInterface> mowerStrategy = _mowersSystem.getMowerStrategy();
        int mowerNum = mowerStrategy.size();
        String[][] result = new String[mowerNum][2];

        for (int i = 0; i < mowerNum; i++) {
            Mower mower = mowerStrategy.get(i).getMower();
            result[i][0] = mower.getCurrentState().name();
            result[i][1] = mower.getCurrentEnergy() + "," + mower.getCurrentDirect().name();
        }

        return result;
    }

    /*
     ** Render horizontal bar
     */
    private int findMowerIndex(List<Point> mowerPosInLawn, Point point) {
        for (int i = 0; i < mowerPosInLawn.size(); i++) {
            if (mowerPosInLawn.get(i).equals(point)) {
                return i;
            }
        }

        return -1;
    }

    /*
     ** Render the lawn
     */
    public void renderLawn() {
        Lawn lawn = _mowersSystem.getLawn();
        List<Point> mowerPosInLawn = _mowersSystem.getMowerPosInLawn();
        int lawnWidth = lawn.getWidth();
        int lawnHeight = lawn.getHeight();

        int charWidth = 2 * lawnWidth + 2;

        // display the rows of the lawn from top to bottom
        for (int y = lawnHeight - 1; y >= 0; y--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(y);

            // display the contents of each square on this row
            for (int x = 0; x < lawnWidth; x++) {
                System.out.print("|");

                // the mower overrides all other contents
                Point pos = new Point(x, y);
                Item item = lawn.getGrid(pos);
                if (item.hasMower()) {
                    int index = findMowerIndex(mowerPosInLawn, pos);
                    System.out.print(index);
                } else if (item.hasGopher()) {
                    System.out.print("C");
                } else if (ItemType.GRASS.equals(item.getType())) {
                    System.out.print("g");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println("|");
        }

        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (int i = 0; i < lawnWidth; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the mower's directions
        for (int k = 0; k < _mowersSystem.getMowerStrategy().size(); k++) {
            Mower mower = _mowersSystem.getMowerStrategy().get(k).getMower();
            if (MowerState.CRASH.equals(mower.getCurrentState())) {
                continue;
            }
            System.out.println("dir m" + mower.getId() + ": " + mower.getCurrentDirect());
        }
        System.out.println("");
    }

    /*
     ** Render horizontal bar
     */
    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    /*
     ** Show the current mowers and lawn state
     */
    private void showState() {
        if (SHOW_STATE) {
            renderLawn();
        }
    }
}
