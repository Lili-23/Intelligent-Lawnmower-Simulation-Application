package com.a710.cs6310.model;

import com.a710.cs6310.common.ItemType;
import com.a710.cs6310.common.Point;
import com.a710.cs6310.common.ScanResultType;

public class Grass extends Item {
    public Grass(Point position) {
        super(ItemType.GRASS, position);
    }

    @Override
    public Item clone() {
        Grass grass = new Grass(this.getPosition());
        grass.setMowerFlag(this.hasMower());
        grass.setGopherFlag(this.hasGopher());
        return grass;
    }

    @Override
    public ScanResultType toScanResult() {
        if (hasMower()) {
            return ScanResultType.MOWER;
        } else {
            return hasGopher() ? ScanResultType.GOPHER_GRASS : ScanResultType.GRASS;
        }
    }
}
