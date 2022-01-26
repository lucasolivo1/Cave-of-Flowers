package byow.Core;

import java.util.Arrays;
import java.util.List;

public class DirectionSet {
    public static final Coordinate UP = new Coordinate(0, 1);
    public static final Coordinate DOWN = new Coordinate(0, -1);
    public static final Coordinate LEFT = new Coordinate(-1, 0);
    public static final Coordinate RIGHT = new Coordinate(1, 0);
    public static final List<Coordinate> DIRECTIONS = Arrays.asList(UP, DOWN, LEFT, RIGHT);

    public static final Coordinate NE = new Coordinate(1, 1);
    public static final Coordinate NW = new Coordinate(-1, 1);
    public static final Coordinate SE = new Coordinate(1, -1);
    public static final Coordinate SW = new Coordinate(-1, -1);
    public static final List<Coordinate> CARDINAL_DIRECTIONS = Arrays.asList(UP, DOWN, LEFT,
            RIGHT, NE, NW, SE, SW);
}
