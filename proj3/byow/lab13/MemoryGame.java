package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        this.rand = new Random(seed);
    }

    /** For the string concatenation warning, see this document:
     * https://stackoverflow.com/questions/18561424/
     * using-for-strings-in-a-loop-is-it-bad-practice/18561542 */
    public String generateRandomString(int n) {
        String str = "";
        for (int i = 0; i < n; i += 1) {
            str += CHARACTERS[rand.nextInt(CHARACTERS.length)];
        }
        return str;
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.black);
        StdDraw.text(width / 2, height / 2, s);
        StdDraw.show();
    }

    /** Source: String.toCharArray solution is given by Dave Cheney on StackOverflow at
     * https://stackoverflow.com/questions/196830/
     * what-is-the-easiest-best-most-correct-way-to-iterate-through-the-characters-of-a*/
    public void flashSequence(String letters) {
        for (char x : letters.toCharArray()) {
            drawFrame(Character.toString(x));
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        String str = "";
        while (str.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                str += StdDraw.nextKeyTyped();
                drawFrame(str);
            }
        }
        return str;
    }

    public void startGame() {
        round = 1;
        while (!gameOver) {
            drawFrame("Round: " + Integer.toString(round));
            StdDraw.pause(1000);
            String randStr = generateRandomString(round);
            flashSequence(randStr);
            String userStr = solicitNCharsInput(round);
            StdDraw.pause(500);
            if (userStr.equals(randStr)) {
                round += 1;
            } else {
                drawFrame("Game Over! You made it to round: " + Integer.toString(round));
                gameOver = true;
            }
        }
    }
}
