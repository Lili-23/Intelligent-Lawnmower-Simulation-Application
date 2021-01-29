package com.a710.cs6310.model;

import com.a710.cs6310.common.*;

import java.util.ArrayList;
import java.util.List;

public class Lawn {
    private int _width;
    private int _height;
    private int _grassTotal;
    private int _grassRemains;
    private Item[][] _grids;
    private List<Point> _mowerPos = new ArrayList<>();
    private List<Point> _chargePadPos = new ArrayList<>();
    private List<Point> _gopherPos = new ArrayList<>();

    public Lawn(Item[][] grids) {
        _grids = grids;
        _height = grids.length;
        _width = grids[0].length;
        _grassTotal = _grassRemains = 0;
        init();
    }

    /*
     ** Calculation of grass and crater in initial state
     */
    public void init() {
        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                if (ItemType.GRASS.equals(_grids[y][x].getType())) {
                    _grassTotal++;
                    _grassRemains++;
                }
            }
        }
    }

    /*
     ** Place mower on this position
     */
    public void setMowerPos(Point pos) {
        EmptyLand land = new EmptyLand(pos);
        land.setMowerFlag(true);

        _grassTotal--;
        _grassRemains--;
        _grids[pos.getPosY()][pos.getPosX()] = land;
        _mowerPos.add(pos);
        _chargePadPos.add(new Point(pos));
    }

    /*
     ** Place mower on this position
     */
    public void setGopherPos(Point pos) {
        _grids[pos.getPosY()][pos.getPosX()].setGopherFlag(true);
        _gopherPos.add(pos);
    }

    /*
     ** Update grid information when mower move out this grid
     */
    public void updateForMowerMove(int index, Point newPosInLawn) {
        Point oldPos = _mowerPos.get(index);
        getGrid(oldPos).setMowerFlag(false);
        _mowerPos.set(index, newPosInLawn);

        Item item;
        if (ItemType.EMPTY.equals(getGrid(newPosInLawn).getType())) {
            item = getGrid(newPosInLawn);
        } else {//grass -> empty land
            item = new EmptyLand(newPosInLawn);
            _grassRemains--;
        }

        item.setMowerFlag(true);
        _grids[newPosInLawn.getPosY()][newPosInLawn.getPosX()] = item;
    }

    /*
     ** Update grid information for gopher move
     */
    public void updateForGopherMove(int idx, Point newPosInLawn) {
        Point oldPos = _gopherPos.get(idx);
        getGrid(oldPos).setGopherFlag(false);
        _gopherPos.set(idx, newPosInLawn);

        _grids[newPosInLawn.getPosY()][newPosInLawn.getPosX()].setGopherFlag(true);
    }

    /*
     ** Update grid information when mower crash
     */
    public void updateForMowerCrash(Point crashPos) {
        if (isPosValid(crashPos)) {
            _grids[crashPos.getPosY()][crashPos.getPosX()].setMowerFlag(false);
        }
    }


    /*
     ** Get total number of grass
     */
    public final int getGrassTotal() {
        return _grassTotal;
    }


    /*
     ** Get number of uncut grass
     */
    public final int getGrassRemains() {
        return _grassRemains;
    }

    /*
     ** Get the width of this lawn
     */
    public final int getWidth() {
        return _width;
    }

    /*
     ** Get the height of this lawn
     */
    public final int getHeight() {
        return _height;
    }

    /*
     ** Whether have any grass uncut
     */
    public final boolean hasGrass() {
        return _grassRemains > 0;
    }


    /*
     ** Get surrounding grids based on given position
     ** Make sure the scan result is a copy of surrounding current grids.
     */
    public final List<ScanResultType> cscanForMower(int mowerIdx) {
        List<ScanResultType> result = new ArrayList<>();
        Point pos = _mowerPos.get(mowerIdx);

        for (Direction direction : Direction.values()) {
            int x = pos.getPosX() + PosDirectUtil.NEIGHBOR_OFFSET.get(direction).get(0);
            int y = pos.getPosY() + PosDirectUtil.NEIGHBOR_OFFSET.get(direction).get(1);

            if (x >= _width || x < 0 || y < 0 || y >= _height) {
                result.add(ScanResultType.FENCE);
            } else {
                result.add(_grids[y][x].toScanResult());
            }
        }
        return result;
    }

    /*
     ** Get surrounding grids based on given position
     ** Make sure the scan result is a copy of surrounding current grids.
     */
    public final List<ScanResultType> lscanForMower(int mowerIdx, Direction direction) {
        List<ScanResultType> result = new ArrayList<>();
        Point pos = _mowerPos.get(mowerIdx);

        List<Integer> offset = PosDirectUtil.NEIGHBOR_OFFSET.get(direction);
        Point next = new Point(pos.getPosX() + offset.get(0), pos.getPosY() + offset.get(1));
        while (isPosValid(next)) {
            result.add(getGrid(next).toScanResult());
            next.setPosX(next.getPosX() + offset.get(0))
                    .setPosY(next.getPosY() + offset.get(1));
        }

        result.add(ScanResultType.FENCE);
        return result;
    }

    /*
     ** Get current grid of this lawn based on given position
     */
    public final Item getGrid(Point pos) {
        return _grids[pos.getPosY()][pos.getPosX()];
    }


    /*
     ** Get all the grids of this lawn
     */
    public final Item[][] getAllGrid() {
        return _grids;
    }

    /*
     ** Check whether given position is valid for this lawn
     */
    public boolean isPosValid(Point pos) {
        if (pos == null) {
            return false;
        }

        int x = pos.getPosX();
        int y = pos.getPosY();
        return x >= 0 && x < _width && y >= 0 && y < _height;
    }

    /*
     ** Get mower position in this lawn
     */
    public final List<Point> getMowerPos() {
        return _mowerPos;
    }

    /*
     ** Get gopher position in this lawn
     */
    public final List<Point> getGopherPos() {
        return _gopherPos;
    }

    /*
     ** Get charging pad position in this lawn
     */
    public final List<Point> getChargePadPos() {
        return _chargePadPos;
    }
}
