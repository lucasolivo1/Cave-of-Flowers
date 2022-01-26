package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Player {
    /** The position of the object in the World. */
    private Coordinate pos;
    /** The World, which the Player is in. */
    private World world;
    /** The point accumulated by the Player. */
    private int point;
    /** The display tile of the Player. */
    private TETile tile;
    /** The name of the Player. */
    private String name;

    public Player(int posX, int posY, World world, TETile tile, String name) {
        this(new Coordinate(posX, posY), world, tile, name);
    }

    public Player(Coordinate pos, World world, TETile tile, String name) {
        this.pos = pos;
        this.world = world;
        this.tile = tile;
        this.point = 0;
        this.name = name;
    }

    /** Get the position of the player. */
    public Coordinate pos() {
        return pos;
    }

    /** Get the total point of the player. */
    public int point() {
        return point;
    }

    /** Get the display tile of the player. */
    public TETile tile() {
        return tile;
    }

    /** Get the display tile of the player. */
    public String name() {
        return name;
    }

    /** Set a trap at the player's position if they have the point to afford it. */
    public void placeTrap() {
        Coordinate trapPos = pos();
        pos = new Coordinate(-1, -1);
        if (point > 0 && !world.trapCollision(trapPos)) {
            world.makeTrap(trapPos);
            point -= 1;
        }
        pos = trapPos;
    }

    /** Move the Player in the given direction, if possible. */
    public void move(Coordinate direction) {
        Coordinate newPos = Coordinate.sum(pos, direction);
        if (world.boundaryError(newPos)) {
            return;
        } else if (world.flowerCollision(newPos)) {
            world.set(newPos, Tileset.FLOOR);
            world.makeFlower();
            point += 1;
        } else if (world.trapCollision(newPos)) {
            world.set(newPos, Tileset.FLOOR);
            point = Math.max(0, point - 3);
        }
        if (!world.wallCollision(newPos) && !world.playerCollision(newPos)) {
            pos = newPos;
        }
    }
}
