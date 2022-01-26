package byow.Core;

import byow.TileEngine.Tileset;
import java.util.LinkedList;

public class Hallway {
    /** A tileset to store a marked version of the World.
     * Number system:
     * [0, infinity) represents a different region of the board, either a room or a hallway.
     * [-1] represents an unvisited tile.
     * [-2] represents a visited tile.
     * [-3] represents an error, tile does not exists. */
    private int[][] board;
    /** The World that this world represents. */
    private World world;
    /** Total number of region in the board. */
    private int regionCount;

    public Hallway() {
        clear();
    }

    /** Modify the given World with Hallways. */
    public void modify(World givenWorld) {
        this.world = givenWorld;
        board = new int[world.width()][world.height()];
        for (int x = 0; x < world.width(); x += 1) {
            for (int y = 0; y < world.height(); y += 1) {
                this.set(x, y, -1);
            }
        }
        createMaze();
        connectRooms();
        fillDeadEnd();
        clear();
    }

    /** Clear the Hallway object. */
    private void clear() {
        board = null;
        regionCount = 0;
        world = null;
    }

    /** For all unmarked space on the board, fill in the World using DFS. */
    private void createMaze() {
        LinkedList<Coordinate> queue = new LinkedList<>();
        Coordinate startPos = findUnvisitedTile();
        while (startPos != null) {
            queue.addFirst(startPos);
            queue.addFirst(new Coordinate(-100, -100));
            queue.addFirst(new Coordinate(-100, -100));
            while (!queue.isEmpty()) {
                /* Parent1 is the older parent. */
                Coordinate parent1 = queue.removeFirst();
                Coordinate parent2 = queue.removeFirst();
                Coordinate pos = queue.removeFirst();
                if (visited(pos)) {
                    continue;
                }
                boolean neighborCollided = false;
                for (Coordinate neighbor : world.getAllNeighbors(pos)) {
                    if (!neighbor.equals(parent1) && !neighbor.equals(parent2)
                            && this.collision(neighbor) && !boundaryError(neighbor)) {
                        neighborCollided = true;
                    }
                }
                if (!boundaryError(pos) && !neighborCollided) {
                    this.set(pos, regionCount);
                    world.set(pos, Tileset.FLOOR);
                    for (Coordinate neighbor : world.getNeighbors(pos)) {
                        queue.addFirst(neighbor);
                        queue.addFirst(pos);
                        queue.addFirst(parent2);
                    }
                } else {
                    this.set(pos, -2);
                }
            }
            regionCount += 1;
            startPos = findUnvisitedTile();
        }
    }

    /** Place Rooms on top of the maze, connecting them because the maze ensure that all tiles
     * on the board are connected. */
    private void connectRooms() {
        for (Room room : world.getRooms()) {
            for (Coordinate floor : room.getFloors()) {
                set(floor, regionCount);
            }
            regionCount += 1;
        }
    }

    /** Find the first dead end tile, if it exists. */
    private Coordinate findDeadEnd() {
        for (int x = 1; x < world.width() - 1; x += 1) {
            for (int y = 1; y < world.height() - 1; y += 1) {
                Coordinate pos = new Coordinate(x, y);
                if (isDeadEnd(pos)) {
                    return pos;
                }
            }
        }
        return null;
    }

    /** Determine if a tile is a dead end, which is a tile with 3 or more adjacent walls. */
    private boolean isDeadEnd(int posX, int posY) {
        return isDeadEnd(new Coordinate(posX, posY));
    }

    private boolean isDeadEnd(Coordinate pos) {
        if (boundaryError(pos) || !collision(pos)) {
            return false;
        }
        int wallCount = 0;
        for (Coordinate neighbor : world.getNeighbors(pos)) {
            if (isWall(neighbor)) {
                wallCount += 1;
            }
        }
        return wallCount >= 3;
    }

    /** Remove dead ends from the completed hallways. */
    private void fillDeadEnd() {
        Coordinate pos = findDeadEnd();
        while (pos != null) {
            boolean deadEndExist = true;
            while (deadEndExist) {
                world.set(pos, Tileset.NOTHING);
                this.set(pos, -2);
                for (Coordinate neighbor : world.getNeighbors(pos)) {
                    if (isDeadEnd(neighbor)) {
                        pos = neighbor;

                    } else {
                        deadEndExist = false;
                    }
                }
            }
            pos = findDeadEnd();
        }
    }


    /** Replace the position on the board with the specified number. */
    private void set(int posX, int posY, int num) {
        set(new Coordinate(posX, posY), num);
    }

    private void set(Coordinate pos, int num) {
        if (!boundaryError(pos)) {
            board[pos.x][pos.y] = num;
        }
    }

    /** Return the tile value at the specified position. */
    private int get(int posX, int posY) {
        return get(new Coordinate(posX, posY));
    }

    private int get(Coordinate pos) {
        if (boundaryError(pos)) {
            return -3;
        }
        return board[pos.x][pos.y];
    }

    /** Check if tile is outside of the World or on the of the World */
    private boolean boundaryError(int posX, int posY) {
        return boundaryError(new Coordinate(posX, posY));
    }

    private boolean boundaryError(Coordinate pos) {
        return pos.x < 1 || pos.x >= world.width() - 1 || pos.y < 1 || pos.y >= world.height() - 1;
    }

    /** Return true if the tile is a filled region; ie: num >= 0. */
    private boolean collision(int posX, int posY) {
        return collision(new Coordinate(posX, posY));
    }

    private boolean collision(Coordinate pos) {
        return (boundaryError(pos) || this.get(pos) >= 0);
    }

    /** Return true if the tile is not an unmarked region */
    private boolean visited(int posX, int posY) {
        return visited(new Coordinate(posX, posY));
    }

    private boolean visited(Coordinate pos) {
        return (boundaryError(pos) || this.get(pos) != -1);
    }

    /** Return true if the current tile is a visited tile, but is not a region tile. */
    private boolean isWall(int posX, int posY) {
        return isWall(new Coordinate(posX, posY));
    }

    private boolean isWall(Coordinate pos) {
        return boundaryError(pos) || get(pos) == -2;
    }

    /** Find the first Unvisited tile in the board and returns it. */
    private Coordinate findUnvisitedTile() {
        /** To randomize the world creation, a test location will be used
         * before tracing the board. */
        Coordinate pos = new Coordinate(world.randInt(1 + world.width() - 2),
                1 + world.height() - 2);
        if (!collision(pos)) {
            return pos;
        }
        for (int x = 0; x < world.width(); x += 1) {
            for (int y = 0; y < world.height(); y += 1) {
                if (!visited(x, y)) {
                    return new Coordinate(x, y);
                }
            }
        }
        return null;
    }
}
