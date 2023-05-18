package byow.Core;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private TETile[][] tiles;
    private boolean[][] occupied;
    private int width = 40;
    private int height = 40;
    private int maxWidth = 9;
    private int minWidth = 2;
    private int minHeight = 2;
    private int maxHeight = 9;
    private int maxRooms;
    private long seed;
    private Avatar avatar;
    private Random RANDOM;
    private boolean[][] visible;

    private List<Room> rooms = new ArrayList<>();

    public World(long seed) {
        visible = new boolean[width][height];
        this.seed = seed;
        RANDOM = new Random(seed);
        tiles = new TETile[width][height];
        occupied = new boolean[width][height];
        maxRooms = RandomUtils.uniform(RANDOM, 6, 12);
        createWorld();
        createRooms();
        connectRooms(rooms);
        fillWalls();
        Room firstRoom = rooms.get(0);
        int avatarX = firstRoom.getCenterX();
        int avatarY = firstRoom.getCenterY();
        avatar = new Avatar(avatarX, avatarY);
        tiles[avatarX][avatarY] = Tileset.AVATAR;

    }

    public World(long seed, int xPos, int yPos) {
        visible = new boolean[width][height];
        this.seed = seed;
        RANDOM = new Random(seed);
        tiles = new TETile[width][height];
        occupied = new boolean[width][height];
        maxRooms = RandomUtils.uniform(RANDOM, 8, 10);
        createWorld();
        createRooms();
        connectRooms(rooms);
        fillWalls();

        avatar = new Avatar(xPos, yPos);
        tiles[xPos][yPos] = Tileset.AVATAR;
    }

    public static void main(String[] args) {
        World world = new World(31423);
        TERenderer ter = new TERenderer();
        ter.initialize(world.getWidth(), world.getHeight());
        ter.renderFrame(world.getTiles());
    }

    private void createWorld() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
                occupied[x][y] = false;
            }
        }
    }

    private void createRooms() {
        while (rooms.size() < maxRooms) {
            int w = RandomUtils.uniform(RANDOM, minWidth, maxWidth);
            w += (w % 2) + 1;
            int h = RandomUtils.uniform(RANDOM, minHeight, maxHeight);
            h += (h % 2) + 1;
            int x = RandomUtils.uniform(RANDOM, 1, width - w);
            int y = RandomUtils.uniform(RANDOM, 1, height - h);
            Room newRoom = new Room(w, h, x, y);
            if (!overlap(newRoom)) {
                rooms.add(newRoom);
                for (int i = x; i < x + w; i++) {
                    for (int j = y; j < y + h; j++) {
                        tiles[i][j] = Tileset.FLOOR;
                        occupied[i][j] = true;
                        occupied[i + 1][j] = true;
                        occupied[i - 1][j] = true;
                        occupied[i][j + 1] = true;
                        occupied[i][j - 1] = true;
                    }
                }
            }
        }
    }

    private boolean overlap(Room newRoom) {
        for (int i = newRoom.getxPos(); i < newRoom.getxPos() + newRoom.getWidth(); i++) {
            for (int j = newRoom.getyPos(); j < newRoom.getyPos() + newRoom.getHeight(); j++) {
                if (occupied[i][j] == true) {
                    return true;
                }
            }
        }
        return false;
    }

    private void connect(Room room1, Room room2) {
        int startX = room1.getCenterX();
        int startY = room1.getCenterY();
        int endX = room2.getCenterX();
        int endY = room2.getCenterY();

        if (startX <= endX) {
            for (int i = startX - 1; i < endX; i++) {
                tiles[i][startY] = Tileset.FLOOR;
                occupied[i][startY - 1] = true;
                occupied[i][startY + 1] = true;
            }
            occupied[endX][startY - 1] = true;
            occupied[endX][startY + 1] = true;
        } else {
            for (int i = startX - 1; i > endX; i--) {
                tiles[i][startY] = Tileset.FLOOR;
                occupied[i][startY - 1] = true;
                occupied[i][startY + 1] = true;
            }
            occupied[endX][startY - 1] = true;
            occupied[endX][startY + 1] = true;
        }
        if (startY <= endY) {
            for (int i = startY; i < endY; i++) {
                tiles[endX][i] = Tileset.FLOOR;
                occupied[endX + 1][i] = true;
                occupied[endX - 1][i] = true;
            }
        } else {
            for (int i = startY; i > endY; i--) {
                tiles[endX][i] = Tileset.FLOOR;
                occupied[endX + 1][i] = true;
                occupied[endX - 1][i] = true;
            }
        }
    }

    private void connectRooms(List<Room> rooms) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room curr = rooms.get(i);
            Room next = rooms.get(i + 1);
            connect(curr, next);
        }
    }

    public void fillWalls() {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                if(tiles[i][j] == Tileset.NOTHING && occupied[i][j]) {
                    tiles[i][j] = Tileset.WALL;
                }
            }
        }
    }

//    private void spawnAvatar() {
//        boolean spawned = false;
//        while (!spawned) {
//            int x = RandomUtils.uniform(RANDOM, 0, width);
//            int y = RandomUtils.uniform(RANDOM, 0, height);
//            if(tiles[x][y].equals(Tileset.FLOOR)) {
//
//                spawned = true;
//            }
//        }
//    }
    public void computeLineOfSight(int sightRadius) {
        int avatarX = avatar.getX();
        int avatarY = avatar.getY();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (Math.abs(x - avatarX) <= sightRadius && Math.abs(y - avatarY) <= sightRadius) {
                    visible[x][y] = true;
                } else {
                    visible[x][y] = false;
                }
            }
        }
    }
    public boolean[][] getVisible() {
        return visible;
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Add this method to the World class
    public Avatar getAvatar() {
        return avatar;
    }

    public long getSeed() {
        return seed;
    }

    //@source chat gpt4 helped with the original idea, however code was
    //then rewritten for this World class
}
