package com.a710.cs6310;

import com.a710.cs6310.common.Direction;
import com.a710.cs6310.common.Point;
import com.a710.cs6310.common.ScanResultType;
import com.a710.cs6310.model.*;
import com.a710.cs6310.service.MowerOptimalStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MowerOptimalStrategyTest {
    Mower currentMower;
    Mower otherMower;
    List<ScanResultType> scanResultListCurrentMower1 = new ArrayList<>();
    List<ScanResultType> scanResultListCurrentMower2 = new ArrayList<>();
    List<ScanResultType> scanResultListOtherMower1 = new ArrayList<>();
    List<ScanResultType> scanResultListOtherMower2 = new ArrayList<>();
    Point position = new Point(2,2);
    Item[][] grids = new Item[3][3];
    SharedData sharedData = new SharedData();

    @BeforeEach
    public void setup() {
        currentMower = new Mower(Direction.WEST, 0, 20);
        scanResultListCurrentMower1.add(ScanResultType.GRASS);
        scanResultListCurrentMower1.add(ScanResultType.EMPTY);
        scanResultListCurrentMower1.add(ScanResultType.FENCE);

        scanResultListCurrentMower2.add(ScanResultType.GRASS);
        scanResultListCurrentMower2.add(ScanResultType.GRASS);
        scanResultListCurrentMower2.add(ScanResultType.FENCE);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Point pos = new Point(i, j);
                grids[i][j] = new Grass(pos);
            }
        }
    }

    @Test
    public void pollMowerForAction() {
        Lawn lawn = new Lawn(grids);
        lawn.setMowerPos(position);
        lawn.setGopherPos(new Point(0,0));
        MowerOptimalStrategy mos = new MowerOptimalStrategy(currentMower, lawn, sharedData);
        mos.pollMowerForAction();
    }
}
