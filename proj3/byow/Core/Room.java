package byow.Core;

import byow.TileEngine.Tileset;
import java.util.LinkedList;
import java.util.List;

import static byow.Core.DirectionSet.*;

public class Room {
    /** Position of the Room in the World. */
    private final Coordinate pos;
    /** Size of the  */
    private final Coordinate size;

    public Room(int posX, int posY, int width, int height) {
        this(new Coordinate(posX, posY), new Coordinate(width, height));
    }

    public Room(Coordinate pos, Coordinate size) {
        this.pos = pos;
        this.size = size;
    }

    /** Put the room into the World by replacing the tiles of World with floor. */
    public boolean putIn(World world) {
        if (boundaryError(world) || collision(world) || wallCollision(world)) {
            return false;
        }
        for (Coordinate floor : getFloors()) {
            world.set(floor, Tileset.FLOOR);
        }
        return true;
    }

    /** Return the value of the border in the specified direction. */
    public int border(Coordinate direction) {
        if (direction.x == 0 && direction.y == 0) {
            // throw error("(0, 0) is not a valid direction.");
            return -1;
        } else if (direction.x == 0) {
            return pos.y + direction.y / Math.abs(direction.y);
        } else {
            return pos.x + direction.x / Math.abs(direction.x);
        }
    }

    /** Return true if the room will be (partially) outside of the world. */
    public boolean boundaryError(World world) {
        return (border(RIGHT) >= world.width() || border(UP) >= world.height());
    }

    /** Return true if the room's floor space will collide with any existing tiles in the
     * given world. */
    public boolean collision(World world) {
        for (int x = 1; x <= size.x - 1; x += 1) {
            for (int y = 1; y <= size.y - 1; y += 1) {
                if (world.collision(pos.x + x, pos.y + y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Get the location of the floor tiles of the room. */
    public List<Coordinate> getFloors() {
        LinkedList<Coordinate> floors = new LinkedList<>();
        for (int x = 1; x <= size.x - 1; x += 1) {
            for (int y = 1; y <= size.y - 1; y += 1) {
                floors.add(new Coordinate(pos.x + x, pos.y + y));
            }
        }
        return floors;
    }

    /** Get the location of the wall tiles of the room.
     * layer is the layer of the wall. layer = 0 is the default layer. */
    public List<Coordinate> getWalls() {
        return getWalls(0);
    }

    public List<Coordinate> getWalls(int layer) {
        LinkedList<Coordinate> walls = new LinkedList<>();
        /* Top and bottom wall */
        for (int x = -layer; x <= size.x + layer; x += 1) {
            walls.add(new Coordinate(pos.x + x, pos.y - layer));
            walls.add(new Coordinate(pos.x + x, pos.y + size.y + layer));
        }
        /* Left and right wall */
        for (int y = -layer; y <= size.y + layer; y += 1) {
            walls.add(new Coordinate(pos.x - layer, pos.y + y));
            walls.add(new Coordinate(pos.x + size.x + layer, pos.y + y));
        }
        return walls;
    }

    /** Walls are separated into three layers:
     * 1) there can be no floor tiles in the wall section of the current room.
     * 2) if there is a floor tile in the 2nd layer, there is guaranteed to be a connector.
     * A collisionCount is used to prevent room being on the edge of one another.
     * 3) if there is a floor tile in the 3rd layer, but not the 2nd, there is double-side wall. */
    public boolean wallCollision(World world) {
        for (Coordinate wall : getWalls()) {
            if (world.collision(wall)) {
                return true;
            }
        }
        /*int collisionCount = 0;
        for (Coordinate wall : getWalls(1)) {
            if (world.collision(wall)) {
                collisionCount += 1;
            }
        }
        if (collisionCount > 5) {
            return false;
        }*/
        for (Coordinate wall : getWalls(2)) {
            if (world.collision(wall)) {
                return true;
            }
        }
        return false;
    }
}
