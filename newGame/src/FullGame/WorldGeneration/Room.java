package FullGame.WorldGeneration;

import FullGame.TileEngine.TETile;

public class Room {
    private int width;
    private int length;
    private TETile[][] room;

    public Room(int width, int length, TETile[][] room) {
        this.width = width;
        this.length = length;
        this.room = room;
    }
    
}
