package byow.Core;

public class Coordinate {
    final int x;
    final int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Sum the values of the two Coordinates. */
    public static Coordinate sum(Coordinate first, Coordinate other) {
        int x = first.x + other.x;
        int y = first.y + other.y;
        return new Coordinate(x, y);
    }

    /** Find the distance between the two Coordinates. */
    public static double dist(Coordinate first, Coordinate other) {
        return Math.sqrt(Math.pow(first.x - other.x, 2) + Math.pow(first.y - other.y, 2));
    }

    public boolean equals(Coordinate other) {
        return (this.x == other.x && this.y == other.y);
    }
}
