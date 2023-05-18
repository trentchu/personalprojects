package FullGame.WorldGeneration;
import FullGame.TileEngine.TETile;
import FullGame.TileEngine.TERenderer;
import FullGame.TileEngine.Tileset;

public class World {
    private TETile[][] world;
    private static final int WORLD_WIDTH = 1000;
    private static final int WORlD_HEIGHT = 1000;
    private TETile[][] viewport;

    private static final int VIEW_WIDTH = 50;
    private static final int VIEW_HEIGHT = 50;

    public World() {

    }

    private void createWorld() {
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORlD_HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private void createRooms() {

    }
}
