package com.a710.cs6310.model;

import com.a710.cs6310.common.ItemType;
import com.a710.cs6310.common.Point;
import com.a710.cs6310.common.ScanResultType;

public class EmptyLand extends Item {
    public EmptyLand(Point position) {
        super(ItemType.EMPTY, position);
    }

    @Override
    public Item clone() {
        EmptyLand emptyLand = new EmptyLand(this.getPosition());
        emptyLand.setMowerFlag(this.hasMower());
        emptyLand.setGopherFlag(this.hasGopher());
        return emptyLand;
    }

    @Override
    public ScanResultType toScanResult() {
        if (hasMower()) {
            return ScanResultType.MOWER;
        } else {
            return hasGopher() ? ScanResultType.GOPHER_EMPTY : ScanResultType.EMPTY;
        }
    }
}
