package com.a710.cs6310.model;

import com.a710.cs6310.common.ItemType;
import com.a710.cs6310.common.Point;
import com.a710.cs6310.common.ScanResultType;

public abstract class Item implements Cloneable {
    private ItemType _type;
    private Point _position;
    private Boolean _hasMower;
    private Boolean _hasGopher;

    Item(ItemType type, Point position) {
        _type = type;
        _position = position;
        _hasMower = false;
        _hasGopher = false;
    }

    public boolean hasMower() {
        return _hasMower;
    }

    public void setMowerFlag(Boolean hasMower) {
        _hasMower = hasMower;
    }

    public boolean hasGopher() {
        return _hasGopher;
    }

    public void setGopherFlag(Boolean hasGopher) {
        _hasGopher = hasGopher;
    }

    public ItemType getType() {
        return _type;
    }

    public Point getPosition() {
        return _position;
    }


    abstract public ScanResultType toScanResult();

    public String toSting() {
        return _type.toString() + "," + _position.toString()
                + "," + _hasMower.toString() + "," + _hasGopher.toString();
    }
}
