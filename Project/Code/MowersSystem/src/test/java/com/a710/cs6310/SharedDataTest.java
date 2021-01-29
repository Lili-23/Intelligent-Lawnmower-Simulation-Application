package com.a710.cs6310;

import com.a710.cs6310.common.Direction;
import com.a710.cs6310.common.Point;
import com.a710.cs6310.common.ScanResultType;
import com.a710.cs6310.model.Mower;
import com.a710.cs6310.model.SharedData;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class SharedDataTest {
    private Mower currentMower;
    private Mower otherMower;
    List<ScanResultType> scanResultListCurrentMower1 = new ArrayList<>();
    List<ScanResultType> scanResultListCurrentMower2 = new ArrayList<>();
    List<ScanResultType> scanResultListOtherMower1 = new ArrayList<>();
    List<ScanResultType> scanResultListOtherMower2 = new ArrayList<>();
    Point position = new Point(3,2);
    SharedData sd = new SharedData();

    @BeforeEach
    public void setup() {
        currentMower = new Mower(Direction.WEST, 1, 20);
        otherMower = new Mower(Direction.WEST, 0, 20);
        scanResultListCurrentMower1.add(ScanResultType.GRASS);
        scanResultListCurrentMower1.add(ScanResultType.EMPTY);
        scanResultListCurrentMower1.add(ScanResultType.FENCE);

        scanResultListCurrentMower2.add(ScanResultType.GRASS);
        scanResultListCurrentMower2.add(ScanResultType.GRASS);
        scanResultListCurrentMower2.add(ScanResultType.FENCE);

        scanResultListOtherMower1.add(ScanResultType.MOWER);
        scanResultListOtherMower1.add(ScanResultType.GRASS);
        scanResultListOtherMower1.add(ScanResultType.EMPTY);
        scanResultListOtherMower1.add(ScanResultType.FENCE);

        scanResultListOtherMower2.add(ScanResultType.GRASS);
        scanResultListOtherMower2.add(ScanResultType.GRASS);
        scanResultListOtherMower2.add(ScanResultType.FENCE);
    }
    @Test
    public void isUsedByOtherMowerTest() {
        sd.registerMower(currentMower);
        sd.registerMower(otherMower);
        currentMower.setLScanResult(scanResultListCurrentMower1, Direction.WEST);
        currentMower.setLScanResult(scanResultListCurrentMower2, Direction.SOUTH);

        otherMower.setLScanResult(scanResultListOtherMower1, Direction.WEST);
        otherMower.setLScanResult(scanResultListOtherMower2, Direction.SOUTH);

        Boolean isUsedByOtherMower = sd.isUsedByOtherMower(position, currentMower);
        Assert.assertEquals(true, isUsedByOtherMower);
    }

    @Test
    public void isInChargingPadTest() {
        otherMower.setLScanResult(scanResultListOtherMower1, Direction.WEST);
        otherMower.setLScanResult(scanResultListOtherMower2, Direction.SOUTH);
        Boolean isChargingPad = sd.isInChargingPad(position, otherMower);
        Assert.assertEquals(true, isChargingPad);
    }
}
