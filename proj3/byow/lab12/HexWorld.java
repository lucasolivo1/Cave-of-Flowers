package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 * @source: designed in collaboration with Lucas Olivo, who is my project partner for proj3.
 */
public class HexWorld {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;
    private static final int SIZE = 5;

    /** @param: posX and posY are bottom left position. */
    public static void addHexagon(TETile[][] world, int posX, int posY, int size, TETile tile) {
        for (int y = 0; y < size; y += 1) {
            for (int x = 0; x < size + 2 * y; x += 1) {
                world[posX + x + size - y - 1][posY + y] = tile;
                world[posX + x + size - y - 1][posY + 2 * size - y - 1] = tile;
            }
        }
    }

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        addHexagon(world, SIZE*2, SIZE*2, SIZE, Tileset.WALL);
        /* bottom left */
        addHexagon(world, SIZE*2 - (SIZE*2 - 1), SIZE*2 - (SIZE), SIZE, Tileset.FLOOR);
        /* bottom right */
        addHexagon(world, SIZE*2 - (-SIZE*2 + 1), SIZE*2 - (-SIZE), SIZE, Tileset.FLOWER);

        Random r = new Random(1000);
        for (int i=0; i < 10; i++) {
            System.out.println(r.nextInt(30));
        }
        // draws the world to the screen
        ter.renderFrame(world);
    }
}
