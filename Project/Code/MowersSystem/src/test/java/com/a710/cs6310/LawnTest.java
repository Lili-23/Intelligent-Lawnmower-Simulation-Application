package com.a710.cs6310;

import com.a710.cs6310.common.Direction;
import com.a710.cs6310.common.ItemType;
import com.a710.cs6310.common.Point;
import com.a710.cs6310.common.ScanResultType;
import com.a710.cs6310.model.Grass;
import com.a710.cs6310.model.Item;
import com.a710.cs6310.model.Lawn;
import com.a710.cs6310.model.Mower;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class LawnTest {
    Mower currentMower;
    Mower otherMower;
    List<ScanResultType> scanResultListCurrentMower1 = new ArrayList<>();
    List<ScanResultType> scanResultListCurrentMower2 = new ArrayList<>();
    List<ScanResultType> scanResultListOtherMower1 = new ArrayList<>();
    List<ScanResultType> scanResultListOtherMower2 = new ArrayList<>();
    Point newPosition = new Point(1,2);
    Item[][] grids = new Item[3][3];

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
    public void updateForMowerMoveTest() {
        Lawn lawn = new Lawn(grids);
        lawn.setMowerPos(new Point(2,2));
        lawn.updateForMowerMove(0, newPosition);
        Assert.assertEquals(7, lawn.getGrassRemains());
        Assert.assertEquals(ItemType.EMPTY,grids[newPosition.getPosY()][newPosition.getPosX()].getType());
    }

    @Test
    public void updateForGopherMoveTest() {
        Lawn lawn = new Lawn(grids);
        lawn.setGopherPos(newPosition);
        lawn.updateForGopherMove(0,new Point(1,1));
        Assert.assertEquals(false, grids[newPosition.getPosY()][newPosition.getPosX()].hasGopher());
        Assert.assertEquals(true,grids[1][1].hasGopher());
    }

    @Test
    public void lscanForMowerTest() {
        Lawn lawn = new Lawn(grids);
        lawn.setMowerPos(new Point(2,2));
        List<ScanResultType> lScan = new ArrayList<>();
        lScan.add(ScanResultType.GRASS);
        lScan.add(ScanResultType.GRASS);
        lScan.add(ScanResultType.FENCE);
        List<ScanResultType> result = lawn.lscanForMower(0, Direction.WEST);
        Assert.assertEquals(lScan,result);
    }

}
