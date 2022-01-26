package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import java.io.File;
import java.awt.*;
import static byow.Core.DirectionSet.*;

/** @source InputSource wrapper for StdDraw is written by Joshua Hug. */
public class Engine {
    /** The Renderer object this world. */
    TERenderer ter = new TERenderer();
    /** Feel free to change the width and height. */
    private World world;
    /** Title Phase */
    private boolean titlePhase = true;
    /** Seed Phase */
    private boolean seedPhase = false;
    /** Game Phase */
    private boolean gamePhase = false;
    /** Game Over Phase */
    private boolean gameOverPhase = false;
    /** returns true if previously typed character in game phase was :. */
    private boolean colonSeq = false;
    /** is true if interactWithKeyboard() is called. */
    private boolean displayBoard = false;
    /** is true if collector mode is selected. */
    private boolean collectorMode = true;
    /** is true if race mode is selected. */
    private boolean raceMode = false;
    /** The string version of seed. */
    private String seedStr = "";
    /** The string of the game mode. */
    private String gameModeStr = "Collector";
    /** Offset of the draw screen to put in the HUD. */
    private final int HUD_OFFSET = 2;
    /** Size of the margin around Game phase. */
    private final int DISPLAY_MARGIN = 2;

    /** String of all inputs during the game that can be saved. */
    private String inputStr = "";
    /** Direction to Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File storing the save data. */
    File saveFile = Utils.join(CWD, "save.txt");

    /** The minimum amount of points needed to win the game for collector mode. */
    private final int COLLECTOR_WINNING_POINT = 10;
    /** The minimum amount of points needed to win the game for race mode. */
    private final int RACE_WINNING_POINT = 1;
    /** The victorious player. */
    private Player winner;

    /** The position of the mouse. */
    Coordinate mouse = new Coordinate(0, 0);
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public void interactWithKeyboard() {
        displayBoard = true;
        ter.initialize(70, 70);
        drawTitle();
        TETile[][] board = null;
        while (true) {
            while (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                board = interactWithInputString(Character.toString(c));
                if (titlePhase) {
                    drawTitle();
                } else if (seedPhase) {
                    drawSeed();
                } else if (gamePhase) {
                    drawGame(board);
                } else if (gameOverPhase) {
                    drawGameOver();
                }
            }
            Coordinate currMouse = new Coordinate((int) StdDraw.mouseX() - DISPLAY_MARGIN,
                    (int) StdDraw.mouseY() - DISPLAY_MARGIN);
            if (!mouse.equals(currMouse) && gamePhase) {
                mouse = currMouse;
                drawGame(board);
            }
            mouse = currMouse;
        }
    }

    /** Draw the title phase of the Game. */
    public void drawTitle() {
        String gameMode = "";
        if (collectorMode) {
            gameMode = "Collector Mode: Get 10 Flowers!";
        } else {
            gameMode = "Race Mode: Find The Hidden Flower In The Maze!";
        }
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.pink);
        Font titleFont = new Font("Monaco", Font.BOLD, 70);
        Font textFont = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(titleFont);
        StdDraw.text(35, 45, "Cavern of Flowers");
        StdDraw.setFont(textFont);
        StdDraw.text(35, 40, gameMode);
        StdDraw.text(35, 32, "N: Start New Game");
        StdDraw.text(35, 30, "L: Load Last Saved Game");
        StdDraw.text(35, 28, "Q: Quit Game");
        StdDraw.text(35, 26, "M: Toggle Game Mode");
        //StdDraw.text(35, 20, "inputStr: " + inputStr);
        StdDraw.show();
    }

    /** Draw the seed phase of the Game */
    public void drawSeed() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.pink);
        Font textFont = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(textFont);
        String str = "Enter Seed: " + seedStr;
        StdDraw.text(35, 45, str);
        StdDraw.text(35, 43, "S: Create world.");
        StdDraw.show();
    }

    /** Check if the game is Over, and set Winner. */
    public boolean gameOverCheck() {
        int winningPoint = -1;
        if (collectorMode) {
            winningPoint = COLLECTOR_WINNING_POINT;
        } else if (raceMode) {
            winningPoint = RACE_WINNING_POINT;
        }

        if (player1().point() == winningPoint) {
            winner = player1();
            return true;
        } else if (player2().point() == winningPoint) {
            winner = player2();
            return true;
        }
        return false;
    }

    /** Draw the game phase of the Game */
    public void drawGame(TETile[][] board) {
        ter.renderFrame(board);
        StdDraw.setPenColor(Color.pink);
        Font textFont = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(textFont);
        if (!world.boundaryError(mouse)) {
            StdDraw.text(width() / 8, height() + HUD_OFFSET + DISPLAY_MARGIN,
                    "Tile: " + world.get(mouse).description());
        } else {
            StdDraw.text(width() / 8, height() + HUD_OFFSET + DISPLAY_MARGIN, "Tile: nothing");
        }
        StdDraw.text(2 * width() / 8, height() + HUD_OFFSET + DISPLAY_MARGIN,
                "Mode: " + gameModeStr);
        StdDraw.text(4 * width() / 8, height() + HUD_OFFSET + DISPLAY_MARGIN, "Seed: " + seedStr);
        StdDraw.text(6 * width() / 8, height() + HUD_OFFSET + DISPLAY_MARGIN,
                "Player 1: " + Integer.toString(player1().point()));
        StdDraw.text(7 * width() / 8, height() + HUD_OFFSET + DISPLAY_MARGIN,
                "Player 2: " + Integer.toString(player2().point()));
        StdDraw.show();
    }

    /** Draw the game over phase of the Game */
    public void drawGameOver() {
        StdDraw.setPenColor(Color.pink);
        Font titleFont = new Font("Monaco", Font.BOLD, 70);
        Font textFont = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(titleFont);
        StdDraw.text(width() / 2, height() / 2 + 5, "Victory! " + winner.name() + " Wins!");
        StdDraw.setFont(textFont);
        StdDraw.text(3 * width() / 5, height() + HUD_OFFSET + DISPLAY_MARGIN,
                "Player 1: " + Integer.toString(player1().point()));
        StdDraw.text(width() / 5, height() + HUD_OFFSET + DISPLAY_MARGIN, "Seed: " + seedStr);
        StdDraw.text(4 * width() / 5, height() + HUD_OFFSET + DISPLAY_MARGIN,
                "Player 2: " + Integer.toString(player2().point()));
        StdDraw.text(width() / 2, height() / 2, "R: Restart Game");
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        /** @source https://stackabuse.com/java-convert-string-to-integer/
         * Used to convert String to integer. */
        for (char x : input.toUpperCase().toCharArray()) {
            if (x != 'Q' && x != ':') {
                inputStr += x;
            }
            if (titlePhase) {
                titlePhaseInputHelper(x);
            } else if (seedPhase) {
                seedPhaseInputHelper(x);
            } else if (gamePhase) {
                gamePhaseInputHelper(x);
            } else if (gameOverPhase) {
                gameOverPhaseInputHelper(x);
            }
        }
        if (world == null) {
            return null;
        }
        TETile[][] finalWorldFrame = world.processBoard();
        return finalWorldFrame;
    }

    /** Handles the inputs in title phase. */
    public void titlePhaseInputHelper(char x) {
        if (x == 'N') { // create new world.
            titlePhase = false;
            seedPhase = true;
            if (displayBoard) {
                ter.initialize(70, 70);
            }
        } else if (x == 'L') { // load existing world.
            if (saveFile.exists()) {
                seedStr = "";
                inputStr = "";
                collectorMode = true;
                raceMode = false;
                gameModeStr = "Collector";
                String str = Utils.readContentsAsString(saveFile);
                interactWithInputString(str);
            }
        } else if (x == 'Q') { // Quit the application.
            System.exit(0);
        } else if (x == 'M') { // Toggle the game mode.
            toggleGameMode();
        }
    }

    /** Handles the inputs in seed phase. */
    public void seedPhaseInputHelper(char x) {
        if (x == 'S') { // Create the world.
            seedPhase = false;
            gamePhase = true;
            world = new World(Long.parseLong(seedStr));
            world.generate();
            world.makePlayer();
            world.makePlayer();
            int flowerCount = 0;
            if (raceMode) {
                flowerCount = 1;
            } else if (collectorMode) {
                flowerCount = 10;
            }
            for (int i = 0; i < flowerCount; i += 1) {
                world.makeFlower();
            }
            if (displayBoard) {
                ter.initialize(world.width() + 2 * DISPLAY_MARGIN,
                    world.height() + HUD_OFFSET + 2 * DISPLAY_MARGIN,
                    DISPLAY_MARGIN, DISPLAY_MARGIN);
            }
        } else { //  Add to the seed.
            seedStr += x;
        }
    }

    /** Handles the inputs in game phase. */
    public void gamePhaseInputHelper(char x) {
        if (x == 'W') { //  Move player 1 up.
            colonSeq = false;
            player1().move(UP);
        } else if (x == 'A') {  //  Move player 1 left.
            colonSeq = false;
            player1().move(LEFT);
        } else if (x == 'S') { //  Move player 1 down.
            colonSeq = false;
            player1().move(DOWN);
        } else if (x == 'D') { //  Move player 1 right.
            colonSeq = false;
            player1().move(RIGHT);
        } else if (x == 'I') { //  Move player 2 up.
            colonSeq = false;
            player2().move(UP);
        } else if (x == 'J') { //  Move player 2 left.
            colonSeq = false;
            player2().move(LEFT);
        } else if (x == 'K') { //  Move player 2 down.
            colonSeq = false;
            player2().move(DOWN);
        } else if (x == 'L') { //  Move player 2 right.
            colonSeq = false;
            player2().move(RIGHT);
        } else if (x == ':') {
            colonSeq = true;
        } else if (x == 'Q' && colonSeq) { // Save and quit the game.
            if (displayBoard) {
                ter.initialize(70, 70);
            }
            titlePhase = true;
            gamePhase = false;
            colonSeq = false;
            Utils.writeContents(saveFile, inputStr);
            seedStr = "";
            inputStr = "";
            gameModeStr = "Collector";
            collectorMode = true;
            raceMode = false;
        } else if (x == 'V') { // Change view.
            world.toggleView();
        } else if (x == 'E') { // Make trap at player1's position.
            player1().placeTrap();
        } else if (x == 'O') { // Make trap at player2's position.
            player2().placeTrap();
        } else {
            colonSeq = false;
        }
        if (gameOverCheck()) {
            gamePhase = false;
            gameOverPhase = true;
            if (displayBoard) {
                ter.initialize(world.width() + 2 * DISPLAY_MARGIN,
                        world.height() + HUD_OFFSET + 2 * DISPLAY_MARGIN,
                        DISPLAY_MARGIN, DISPLAY_MARGIN);
            }
        }
    }

    public void gameOverPhaseInputHelper(char x) {
        if (x == 'R') { //  Move player 1 up.
            titlePhase = true;
            gameOverPhase = false;
            seedStr = "";
            inputStr = "";
            if (displayBoard) {
                ter.initialize(70, 70);
            }
        }
    }

    /** Toggle the game mode between collector mode and raceMode. */
    public void toggleGameMode() {
        if (collectorMode) {
            collectorMode = false;
            raceMode = true;
            gameModeStr = "Race";
        } else {
            collectorMode = true;
            raceMode = false;
            gameModeStr = "Collector";
        }
    }

    /** Get the width of the world. */
    public int width() {
        if (world == null) {
            System.out.println("World is not initialized");
        }
        return world.width();
    }

    /** Get the height of the world. */
    public int height() {
        if (world == null) {
            System.out.println("World is not initialized");
        }
        return world.height();
    }

    /** Return the reference to the player1. */
    public Player player1() {
        return world.getPlayers().get(0);
    }

    /** Return the reference to the player2. */
    public Player player2() {
        return world.getPlayers().get(1);
    }

    public static void main(String[] args) {
        String[] inputs = {"n5467397544107330504s",
            "n9056129480306637831swwwwwwwwddddwwwwaaaiiiiiiiiiijjjiiii",
            "n7095889102109223638s", "n4301662833262921571s"};
        Engine engine = new Engine();
        // TETile[][] world = engine.interactWithInputString(inputs[1]);
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        // drawGame();
        engine.interactWithKeyboard();
    }
}
