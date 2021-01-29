package com.a710.cs6310.common;

import java.util.*;

import static com.a710.cs6310.common.Direction.*;
import static java.lang.Math.abs;

public final class PosDirectUtil {
    public static HashMap<Direction, List<Integer>> NEIGHBOR_OFFSET = new HashMap<Direction, List<Integer>>() {{
        put(Direction.NORTH, Arrays.asList(0, 1));
        put(Direction.NORTHEAST, Arrays.asList(1, 1));
        put(Direction.EAST, Arrays.asList(1, 0));
        put(Direction.SOUTHEAST, Arrays.asList(1, -1));
        put(Direction.SOUTH, Arrays.asList(0, -1));
        put(Direction.SOUTHWEST, Arrays.asList(-1, -1));
        put(Direction.WEST, Arrays.asList(-1, 0));
        put(Direction.NORTHWEST, Arrays.asList(-1, 1));
    }};

    /*
     ** Get new position based on moving with the direction
     */
    public static Point getMoveToPosition(Point curPos, Direction direction) {
        return new Point(curPos.getPosX() + NEIGHBOR_OFFSET.get(direction).get(0),
                curPos.getPosY() + NEIGHBOR_OFFSET.get(direction).get(1));
    }

    /*
     ** Moves from position A to position B
     */
    public static int distanceOfPoints(Point curPos, Point newPos) {
        return Integer.max(abs(curPos.getPosX() - newPos.getPosX()),
                abs(curPos.getPosY() - newPos.getPosY()));
    }

    /*
     ** Get the direction from curPos to nextPos
     * nextPos may not be neighbor
     */
    public static Direction calculateDirection(Point curPos, Point nextPos) {
        if (curPos.getPosX() == nextPos.getPosX()) {
            return curPos.getPosY() > nextPos.getPosY() ? SOUTH : NORTH;
        }

        if (curPos.getPosY() == nextPos.getPosY()) {
            return curPos.getPosX() > nextPos.getPosX() ? WEST : EAST;
        }

        if (curPos.getPosX() > nextPos.getPosX()) {
            return curPos.getPosY() > nextPos.getPosY() ? SOUTHWEST : NORTHWEST;
        } else {
            return curPos.getPosY() > nextPos.getPosY() ? SOUTHEAST : NORTHEAST;
        }
    }

    // For the four directions gopher can not move, we map them to four reachable directions clockwise.
    public static Direction calculateGopherDirection(Point curPos, Point nextPos) {
        if (curPos.getPosX() == nextPos.getPosX()) {
            return curPos.getPosY() > nextPos.getPosY() ? SOUTH : NORTH;
        }

        if (curPos.getPosY() == nextPos.getPosY()) {
            return curPos.getPosX() > nextPos.getPosX() ? WEST : EAST;
        }

        if (curPos.getPosX() > nextPos.getPosX()) {
            return curPos.getPosY() > nextPos.getPosY() ? WEST : NORTH;
        } else {
            return curPos.getPosY() > nextPos.getPosY() ? SOUTH : EAST;
        }
    }
}
