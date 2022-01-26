package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static byow.Core.DirectionSet.*;

public class World {
    /** A 2D arrays of tile, representing the game world. */
    private final TETile[][] board;
    /** Size of the world. */
    private final Coordinate size;
    /** Seed of the game; used to generate random numbers. */
    private final long SEED;
    /** Random generator of the world. */
    private final Random rand;
    /** Switches between restricted view and whole view. */ 
    private boolean changeView = true;
    /** List of references to Room objects. */
    private final List<Room> rooms = new LinkedList<>();
    /** Default amount of attempt to generate rooms. */
    private final int ATTEMPT_ROOM_GEN;
    /** Minimum amount of rooms. */
    private final int MIN_ROOM_GEN = 2;

    /** List of references to Player objects. */
    private final List<Player> players = new LinkedList<>();
    /** Sight length of player. Used to determine BFS limit. */
    private final int SIGHT_LENGTH = 5;

    /** Generate an empty World. */
    public World(long seed) {
        this.SEED = seed;
        this.rand = new Random(seed);
        this.size = new Coordinate(70 + 5 * randInt(5), 30 + 5 * randInt(3));
        this.board = new TETile[width()][height()];
        for (int x = 0; x < width(); x += 1) {
            for (int y = 0; y < height(); y += 1) {
                set(x, y, Tileset.NOTHING);
            }
        }
        this.ATTEMPT_ROOM_GEN = 100 + 10 * randInt(10);
    }

    public World(int width, int height, long seed) {
        this(new Coordinate(width, height), seed);
    }

    public World(Coordinate size, long seed) {
        this.size = size;
        this.SEED = seed;
        this.rand = new Random(seed);
        this.board = nothingBoard(width(), height());
        this.ATTEMPT_ROOM_GEN = 100 + 10 * randInt(10);
    }

    /** Fill in the World with rooms and hallways. */
    public void generate() {
        int attempt = 0;
        while (attempt < ATTEMPT_ROOM_GEN || rooms.size() < MIN_ROOM_GEN) {
            makeRoom();
            attempt += 1;
        }
        Hallway hallway = new Hallway();
        hallway.modify(this);
        makeWall();
    }

    /** Make a room of a random size and position and attempt to place it in the World. */
    public void makeRoom() {
        Coordinate roomSize = new Coordinate(4 + randInt(7), 4 + randInt(7));
        Coordinate roomPos = new Coordinate(randInt(width() - roomSize.x),
                randInt(height() - roomSize.y));
        Room room = new Room(roomPos, roomSize);
        if (room.putIn(this)) {
            rooms.add(room);
        }
    }

    /** For every floor tiles, makes sure that its neighbors are not NOTHING tiles. */
    public void makeWall() {
        for (int x = 0; x < width(); x += 1) {
            for (int y = 0; y < height(); y += 1) {
                if (get(x, y).equals(Tileset.FLOOR)) {
                    for (Coordinate neighbor : getAllNeighbors(new Coordinate(x, y))) {
                        if (!collision(neighbor)) {
                            set(neighbor, Tileset.WALL);
                        }
                    }
                }
            }
        }
    }

    /** Put Player in a Room. */
    public Player makePlayer() {
        Room room = getRooms().get(randInt(getRooms().size()));
        List<Coordinate> floors = room.getFloors();
        Coordinate floor = floors.get(randInt(floors.size()));
        if (playerCollision(floor)) {
            return makePlayer();
        }
        char tileVal = Integer.toString(getPlayers().size() + 1).charAt(0);
        String name = "Player " + tileVal;
        TETile playerTile = new TETile(tileVal, Color.white, Color.red, name);
        Player player = new Player(floor, this, playerTile, name);
        players.add(player);
        return player;
    }

    /** Make a flower in a random room tile. Must be outiside the restricted view of players. */
    public void makeFlower() {
        Room flowerRoom = getRooms().get(randInt(getRooms().size()));
        List<Coordinate> floors = flowerRoom.getFloors();
        Coordinate randFloor = floors.get(randInt(floors.size()));
        boolean goodPos = true;
        if (!floorCollision(randFloor)) {
            goodPos = false;
        }
        for (Player player : getPlayers()) {
            if (Math.floor(Coordinate.dist(player.pos(), randFloor)) <= SIGHT_LENGTH) {
                goodPos = false;
            }
        }
        if (goodPos) {
            set(randFloor, Tileset.FLOWER);
        } else {
            makeFlower();
        }
    }
    /** Creates a trap tile on the player's position who is setting it. */
    public void makeTrap(int posX, int posY) {
        makeTrap(new Coordinate(posX, posY));
    }
    public void makeTrap(Coordinate pos) {
        set(pos, Tileset.TRAP);
    }

    /** Return the rooms array which stores a reference to all Room object in World. */
    public List<Room> getRooms() {
        return rooms;
    }

    /** Return the Player array which stores a reference to all Player object in World. */
    public List<Player> getPlayers() {
        return players;
    }

    /** Check if tile is outside of the World */
    public boolean boundaryError(int posX, int posY) {
        return boundaryError(new Coordinate(posX, posY));
    }

    public boolean boundaryError(Coordinate pos) {
        return pos.x < 0 || pos.x >= width() || pos.y < 0 || pos.y >= height();
    }

    /** Check if tile is overlapping any type of Tile beside Tileset.NOTHING. */
    public boolean collision(int posX, int posY) {
        return collision(new Coordinate(posX, posY));
    }

    public boolean collision(Coordinate pos) {
        if (boundaryError(pos)) {
            return true;
        }
        return !get(pos).equals(Tileset.NOTHING);
    }

    /** Check if the tile is a Wall tile. */
    public boolean wallCollision(int posX, int posY) {
        return wallCollision(new Coordinate(posX, posY));
    }

    public boolean wallCollision(Coordinate pos) {
        if (boundaryError(pos)) {
            return true;
        }
        return get(pos).equals(Tileset.WALL);
    }

    public boolean trapCollision(Coordinate pos) {
        if (boundaryError(pos)) {
            return true;
        }
        return get(pos).equals(Tileset.TRAP);
    }

    /** Check if the tile is a Player tile. */
    public boolean playerCollision(int posX, int posY) {
        return playerCollision(new Coordinate(posX, posY));
    }

    public boolean playerCollision(Coordinate pos) {
        if (boundaryError(pos)) {
            return true;
        }
        for (Player player : getPlayers()) {
            if (player.pos().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /** Check if the tile is a Floor tile. */
    public boolean floorCollision(int posX, int posY) {
        return floorCollision(new Coordinate(posX, posY));
    }

    public boolean floorCollision(Coordinate pos) {
        if (boundaryError(pos)) {
            return true;
        }
        return get(pos).equals(Tileset.FLOOR);
    }

    public boolean flowerCollision(Coordinate pos) {
        if (boundaryError(pos)) {
            return true;
        }
        if (get(pos).equals(Tileset.FLOWER)) {
            return true;
        }
        return false;
    }

    /** Get all adjacent tiles to the specified tile. */
    public Iterable<Coordinate> getNeighbors(int posX, int posY) {
        return getNeighbors(new Coordinate(posX, posY));
    }

    public Iterable<Coordinate> getNeighbors(Coordinate pos) {
        LinkedList<Coordinate> directions = new LinkedList<>(DIRECTIONS);
        LinkedList<Coordinate> neighbors = new LinkedList<>();
        while (!directions.isEmpty()) {
            int index = randInt(directions.size());
            Coordinate direction = directions.remove(index);
            Coordinate neighbor = Coordinate.sum(pos, direction);
            neighbors.add(neighbor);
        }
        return neighbors;
    }

    /** Get all tiles neighboring to the specified tile, including the diagonal tiles. */
    public Iterable<Coordinate> getAllNeighbors(int posX, int posY) {
        return getAllNeighbors(new Coordinate(posX, posY));
    }

    public Iterable<Coordinate> getAllNeighbors(Coordinate pos) {
        LinkedList<Coordinate> directions = new LinkedList<>(CARDINAL_DIRECTIONS);
        LinkedList<Coordinate> neighbors = new LinkedList<>();
        while (!directions.isEmpty()) {
            int index = randInt(directions.size());
            Coordinate direction = directions.remove(index);
            Coordinate neighbor = Coordinate.sum(pos, direction);
            neighbors.add(neighbor);
        }
        return neighbors;
    }

    /** Replace the position on the board with the specified tile. */
    public void set(int posX, int posY, TETile tile) {
        set(new Coordinate(posX, posY), tile);
    }

    public void set(Coordinate pos, TETile tile) {
        if (!boundaryError(pos)) {
            board[pos.x][pos.y] = tile;
        }
    }

    /** Return the tile at the specified position. */
    public TETile get(int posX, int posY) {
        return get(new Coordinate(posX, posY));
    }

    public TETile get(Coordinate pos) {
        if (boundaryError(pos)) {
            return null;
        }
        for (Player player : getPlayers()) {
            if (pos.equals(player.pos())) {
                return player.tile();
            }
        }
        return board[pos.x][pos.y];
    }

    /** Returns the size of the board as Coordinate. */
    public Coordinate shape() {
        return size;
    }

    /** Return the width of the board. */
    public int width() {
        return shape().x;
    }

    /** Return the height of the board. */
    public int height() {
        return shape().y;
    }

    /** Returns a random number from 0 to num using the World's seed. */
    public int randInt(int num) {
        return rand.nextInt(num);
    }

    /** Render the board using the supplied TERenderer. */
    public void render(TERenderer ter) {
        ter.renderFrame(board);
    }

    /** Find the first NOTHING tile in the board and returns it. */
    public Coordinate findNothingTile() {
        for (int x = 0; x < width(); x += 1) {
            for (int y = 0; y < height(); y += 1) {
                if (!collision(x, y)) {
                    return new Coordinate(x, y);
                }
            }
        }
        return null;
    }

    /** Clone and return the copy of the board.
     * @source The 2D array clone method is given by NawaMan at
     * https://stackoverflow.com/questions/1686425/copy-a-2d-array-in-java. */
    public TETile[][] cloneBoard() {
        TETile[][] temp = new TETile[width()][];
        for (int i = 0; i < width(); i += 1) {
            temp[i] = board[i].clone();
        }
        return temp;
    }

    /** Process the board by placing the players onto the board. Then, set visible light. */
    public TETile[][] processBoard() {
        TETile[][] disBoard;
        if (changeView) {
            disBoard = cloneBoard();
        } else {
            disBoard = visibleBoard();
        }

        for (Player player : getPlayers()) {
            Coordinate pos = player.pos();
            disBoard[pos.x][pos.y] = player.tile();
        }
        return disBoard;
    }

    /** Return a board of size width x height filled with NOTHING tiles. */
    public TETile[][] nothingBoard(int width, int height) {
        TETile[][] temp = new TETile[width][height];
        for (int x = 0; x < width(); x += 1) {
            for (int y = 0; y < height(); y += 1) {
                temp[x][y] = Tileset.NOTHING;
            }
        }
        return temp;
    }

    /** Return a board which is visible to the players. */
    public TETile[][] visibleBoard() {
        TETile[][] temp = nothingBoard(width(), height());
        LinkedList<Coordinate> queue = new LinkedList<>();
        for (Player player : getPlayers()) {
            queue.addLast(player.pos());
        }
        LinkedList<Coordinate> neighbors = new LinkedList<>();
        int distance = 0;
        while (distance < SIGHT_LENGTH) {
            while (!queue.isEmpty()) {
                Coordinate pos = queue.removeFirst();
                if (!temp[pos.x][pos.y].equals(Tileset.NOTHING)) {
                    continue;
                }
                temp[pos.x][pos.y] = get(pos);
                if (wallCollision(pos)) {
                    continue;
                }
                for (Coordinate neighbor : getAllNeighbors(pos)) {
                    neighbors.addLast(neighbor);
                }
            }
            queue = new LinkedList<>(neighbors);
            neighbors.clear();
            distance += 1;
        }
        return temp;
    }
    
    /** Toggle whether the user have restricted view or full world view. */
    public void toggleView() {
        changeView = !changeView;
    }

    /** Check that each Tile in the board is equivalent. */
    public boolean equals(World other) {
        if (this.shape() != other.shape()) {
            return false;
        }
        for (int x = 0; x < width(); x += 1) {
            for (int y = 0; y < height(); y += 1) {
                if (!this.get(x, y).equals(other.get(x, y))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String[] inputs = {"n5467397544107330504s", "n9056129480306637831s",
            "n7095889102109223638s", "n4301662833262921571s"};
        long[] seeds = new long[inputs.length];
        for (int i = 0; i < inputs.length; i += 1) {
            seeds[i] = Long.parseLong(inputs[i].substring(1, inputs[i].length() - 1));
            // System.out.println(seeds[i]);
        }

        World[] worlds = new World[seeds.length];
        for (int i = 0; i < seeds.length; i += 1) {
            worlds[i] = new World(seeds[i]);
            worlds[i].generate();
            System.out.println("Case " + Integer.toString(i) + ": "
                    + Boolean.toString(worlds[0].equals(worlds[i])));
        }

        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT.
        TERenderer ter = new TERenderer();
        ter.initialize(worlds[1].width(), worlds[1].height());
        worlds[1].render(ter);
    }
}
