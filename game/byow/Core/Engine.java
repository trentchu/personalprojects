package byow.Core;


import byow.InputDemo.Game;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.awt.Font;

public class Engine {
    TERenderer ter = new TERenderer();
    Game game;
    String name = "Avatar";
    World oldWorld;
    /* Feel free to change the width and height. */
    public static final int WIDTH = 40;
    public static final int HEIGHT = 40;
    private static final int PIXEL = 16;

    private boolean inSeedMode = false;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public void interactWithKeyboard() {
        drawMenu();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());

                if (key == 'N') {
                    // Start the game
                    drawFrame("Please enter a seed (s to enter)");
                    String input = solicitNCharsInput();
                    long s = Long.parseLong(input);

                    game = new Game(WIDTH, HEIGHT, s, name, null);
                    game.startGame();
                    break;
                } else if (key == 'L') {

                    loadGameState();


                } else if (key == 'C') {

                    drawFrame("Please enter a name (. to enter)");
                    name = solicitNameCharsInput();
                    drawMenu();


                } else if (key == 'Q') {
                    // Quit the game
                    System.exit(0);
                }
            }
        }

    }
    public void loadGameState() {
        In file = new In("save-file.txt");
        String[] fileList = file.readAllLines();
        long seed = Long.parseLong(fileList[0]);
        String[] posS = fileList[1].split(" ");
        int xPos = Integer.parseInt(posS[0]);
        int yPos = Integer.parseInt(posS[1]);
        if (name.equals("Avatar")) {
            name = fileList[3];
        }

        game = new Game(WIDTH, HEIGHT, seed, name, null);

        oldWorld = game.getWorld();
        Avatar avatar = oldWorld.getAvatar();
        TETile[][] gameTile = oldWorld.getTiles();

        // Set the old avatar position to FLOOR
        gameTile[avatar.getX()][avatar.getY()] = Tileset.FLOOR;

        // Update the avatar's position
        avatar.setX(xPos);
        avatar.setY(yPos);

        // Set the new avatar position to AVATAR
        gameTile[xPos][yPos] = Tileset.AVATAR;

        game.startGame();
    }

    public String loadGameStateString(String input) {
        In file = new In("save-file.txt");
        String[] fileList = file.readAllLines();
        long seed = Long.parseLong(fileList[0]);
        String[] posS = fileList[1].split(" ");
        int xPos = Integer.parseInt(posS[0]);
        int yPos = Integer.parseInt(posS[1]);
        String incompleteInput = fileList[2].substring(0, fileList[2].length() - 2);

        String fullInput = incompleteInput + input.substring(1);

        // Create a new world with the loaded seed
        return fullInput;
    }

    private void drawMenu() {


        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        int canvasWidth = WIDTH * PIXEL;
        int canvasHeight = HEIGHT * PIXEL;

        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, centerX));

        StdDraw.text(centerX, centerY + 4, "Welcome to the Game!");
        StdDraw.text(centerX, centerY, "New Game (N)");
        StdDraw.text(centerX, centerY - 4, "Load Game (L)");
        StdDraw.text(centerX, centerY - 8, "Choose Name (C)");
        StdDraw.text(centerX, centerY / 4, "Quit (Q)");

        StdDraw.show();
    }
    public String solicitNCharsInput() {
        StringBuilder userInput = new StringBuilder();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 's' || key == 'S') {
                    return userInput.toString();
                }
                userInput.append(key);

                // Display the user input so far
                drawFrame(userInput.toString());
            }
        }
    }
    public String solicitNameCharsInput() {
        StringBuilder userInput = new StringBuilder();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == '.') {
                    return userInput.toString();
                }
                userInput.append(key);

                // Display the user input so far
                drawFrame(userInput.toString());
            }
        }
    }
    public void drawFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.WHITE);
        StdDraw.setPenColor(Color.BLACK);
        Font fontBig = new Font("Monaco", Font.BOLD, HEIGHT / 2);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
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
     * In other words, running both of these:
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
        input = input.toUpperCase();
        int index = 0;
        String actions;
        if (input.charAt(index) == 'N') {
            long seed = seedBuilder(input);
            actions = input.substring(index);
            game = new Game(WIDTH, HEIGHT, seed, name, input);

        } else if (input.charAt(index) == 'L') {
            String placeholder = loadGameStateString(input);
            System.out.println(placeholder);
            TETile[][] tiles = interactWithInputString(placeholder);
            return tiles;
        } else {
            throw new IllegalArgumentException("Invalid input string: " + input);
        }

        // Return the current game state
        game.processActions(actions);

        return game.getWorld().getTiles();
    }

    public long seedBuilder(String input) {
        input = input.toUpperCase();
        int index = 0;
        long seed;

        StringBuilder seedBuilder = new StringBuilder();
        index++;
        while (Character.isDigit(input.charAt(index))) {
            seedBuilder.append(input.charAt(index));
            index++;
        }

        if (input.charAt(index) == 'S') {
            index++;
        } else {
            throw new IllegalArgumentException("missing 'S' after seed: " + input);
        }

        seed = Long.parseLong(seedBuilder.toString());
        return seed;
    }

    // @source: Chat GPT4, was used to generate
    // the general structure of the Interact W Keyboard and String
    // methods

}
