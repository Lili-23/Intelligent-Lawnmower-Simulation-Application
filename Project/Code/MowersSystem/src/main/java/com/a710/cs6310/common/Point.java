package com.a710.cs6310.common;

public class Point {
    private int _x;
    private int _y;

    public Point(int x, int y) {
        this._x = x;
        this._y = y;
    }

    public Point(Point other) {
        this._x = other._x;
        this._y = other._y;
    }

    public int getPosX() {
        return _x;
    }

    public int getPosY() {
        return _y;
    }

    public Point setPosX(int x) {
        _x = x;
        return this;
    }

    public Point setPosY(int y) {
        _y = y;
        return this;
    }

    public boolean equals(Object other) {
        return _x == ((Point) other)._x && _y == ((Point) other)._y;
    }

    public String toString() {
        return _x + " + " + _y;
    }
}
