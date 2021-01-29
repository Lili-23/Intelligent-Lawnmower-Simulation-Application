package com.a710.cs6310;

import com.a710.cs6310.common.*;
import com.a710.cs6310.model.Mower;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MowerTest {
    private Mower mower;
    List<ScanResultType> scanResultList = new ArrayList<>();

    @BeforeEach
    public void setup() {
        mower = new Mower(Direction.NORTH, 1, 20);
        scanResultList.add(ScanResultType.GRASS);
        scanResultList.add(ScanResultType.EMPTY);
        scanResultList.add(ScanResultType.FENCE);
    }

    @Test
    public void testSetAndGetResult() {
        mower.setCScanResult(scanResultList);
        Assert.assertEquals(mower.getScanResult(), scanResultList);
    }

    @Test
    public void testMoveAndStatusChange() {
        int preEnergy =  mower.getCurrentEnergy();
        Point prePosition = mower.getCurrentPos();
        mower.move();
        Assert.assertEquals(mower.getCurrentEnergy(), preEnergy - Mower.ACTION_COST.get(MowerAction.MOVE));
        Assert.assertEquals(mower.getCurrentPos(), PosDirectUtil.getMoveToPosition(prePosition, mower.getCurrentDirect()));
    }

}
