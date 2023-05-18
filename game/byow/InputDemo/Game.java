package byow.InputDemo;

import byow.Core.World;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;






import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;




public class Game {
    private int width;
    private int height;
    private String tileDescription = "";
    private Random rand;
    private boolean gameOver;
    private boolean lineOfSightEnabled;
    private World world;;
    private String name;
    private String input;


    private static final String SAVE_FILE = "save-file.txt";

    private TERenderer ter;

    public Game(int width, int height, long seed, String name, String input) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.lineOfSightEnabled = true;
        this.input = input;
        this.ter = new TERenderer();
        this.world = new World(seed);

        this.rand = new Random(seed);

    }



//    public Game(int width, int height, long seed, int xPos, int yPos, String name) {
//        this.name = name;
//        this.width = width;
//        this.height = height;
//        this.lineOfSightEnabled = true;
//        this.world = new World(seed, xPos, yPos);
//        this.ter = new TERenderer();
//        this.rand = new Random(seed);
//
//    }



    public void drawFrame() {
        if (lineOfSightEnabled) {
            world.computeLineOfSight(5);
            TETile[][] visibleTiles = getVisibleTiles();
            ter.renderFrame(visibleTiles);
        } else {
            ter.renderFrame(world.getTiles());
        }

//        ter.renderFrame(world.getTiles());
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 15));
        StdDraw.textLeft(1, height + 2, "Tile: " + tileDescription);
        StdDraw.textRight(width - 1, height + 2, "Name: " + name);
        StdDraw.show();

    }

    private TETile[][] getVisibleTiles() {
        TETile[][] tiles = world.getTiles();
        boolean[][] visible = world.getVisible();
        TETile[][] visibleTiles = new TETile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (visible[x][y]) {
                    visibleTiles[x][y] = tiles[x][y];
                } else {
                    visibleTiles[x][y] = Tileset.NOTHING;
                }
            }
        }

        return visibleTiles;
    }

    public void moveAvatar() {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            int newX = world.getAvatar().getX();
            int newY = world.getAvatar().getY();

            switch (key) {
                case 't':
                    lineOfSightEnabled = !lineOfSightEnabled;
                    break;


                case 'w':
                    newY++;
                    break;
                case 's':
                    newY--;
                    break;
                case 'a':
                    newX--;
                    break;
                case 'd':
                    newX++;
                    break;
                case ':':
                    while (!StdDraw.hasNextKeyTyped()) {
                        // Wait for the next key press
                    }
                    key = StdDraw.nextKeyTyped();
                    if (key == 'Q' || key == 'q') {
                        saveGameState();
                        gameOver = true;
                        System.exit(0);
                        return;
                    }
                    break;
                default:
                    break;
            }

            if (world.getTiles()[newX][newY] != null && world.getTiles()[newX][newY].description().equals("floor")) {
                world.getTiles()[world.getAvatar().getX()][world.getAvatar().getY()] = Tileset.FLOOR;
                world.getAvatar().setX(newX);
                world.getAvatar().setY(newY);
                world.getTiles()[newX][newY] = Tileset.AVATAR;
                // Add this line after the avatar has moved:
                world.computeLineOfSight(5); // 5 is the sight radius, you can change it as needed
            }
        }
    }

    public void processActions(String actions) {
        for (int i = 0; i < actions.length(); i++) {
            char action = actions.charAt(i);
            int newX = world.getAvatar().getX();
            int newY = world.getAvatar().getY();

            if (action == 'W') {
                newY++;
            } else if (action == 'A') {
                newX--;
            } else if (action == 'S') {
                newY--;
            } else if (action == 'D') {
                newX++;
            } else if (action == ':' && i + 1 < actions.length() && actions.charAt(i + 1) == 'Q') {
                saveGameState();
                gameOver = true;
                return;
            } else {
                // Invalid action, skip it
                continue;
            }

            if (world.getTiles()[newX][newY] != null && world.getTiles()[newX][newY].description().equals("floor")) {
                world.getTiles()[world.getAvatar().getX()][world.getAvatar().getY()] = Tileset.FLOOR;
                world.getAvatar().setX(newX);
                world.getAvatar().setY(newY);
                world.getTiles()[newX][newY] = Tileset.AVATAR;
            }
        }
    }

    public void saveGameState() {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE));
            writer.write(world.getSeed() + "\n");
            writer.write(world.getAvatar().getX() + " " + world.getAvatar().getY() + "\n");
            writer.write(getInput() + "\n");
            writer.write(getName() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeName(String name) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE));
            writer.write(world.getSeed() + "\n");
            writer.write(world.getAvatar().getX() + " " + world.getAvatar().getY() + "\n");
            writer.write(getInput() + "\n");
            writer.write(name + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startGame() {

        this.gameOver = false;
        ter.initialize(width, height + 3, 0, 0);
        ter.renderFrame(world.getTiles());

        while (!gameOver) {
            // Get mouse position and update the tile description
            int mouseX = (int) StdDraw.mouseX();
            int mouseY = (int) StdDraw.mouseY();
            if (mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height) {
                tileDescription = world.getTiles()[mouseX][mouseY].description();
            } else {
                tileDescription = "";
            }
            moveAvatar();
            drawFrame();
        }


    }
    public World getWorld() {
        return world;
    }

    public String getInput(){
        return input;
    }
    public String getName() {
        return name;
    }

    public static void main(String[] args) {

        Game game = new Game(40, 40, 999999999999999999L, "name", null);
        System.out.println("hi");
        game.startGame();
    }

    //@source: Chat GPT4 played a heavy influence on all methods in this Class
}

