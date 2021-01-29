package com.a710.cs6310.model;

import com.a710.cs6310.common.ItemType;
import com.a710.cs6310.common.Point;
import com.a710.cs6310.common.ScanResultType;

public class Fence extends Item {
    public Fence(Point position) {
        super(ItemType.FENCE, position);
    }

    @Override
    public Item clone() {
        Fence fence = new Fence(this.getPosition());
        return fence;
    }

    @Override
    public ScanResultType toScanResult() {
        return ScanResultType.FENCE;
    }
}
