package byow.Core;

public class Room {
    private int xPos;
    private int yPos;
    private int width;
    private int height;
    private int centerX;
    private int centerY;

    public Room(int w, int h, int x, int y) {
        xPos = x;
        yPos = y;
        width = w;
        height = h;
        centerX = x + (width / 2) + 1;
        centerY = y  + (height / 2) + 1;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getCenterX() {
        return centerX;
    }

    public boolean overlapsWith(Room other) {
        int xDist = Math.abs(this.getxPos() - other.getxPos());
        int yDist = Math.abs(this.getyPos() - other.getyPos());
        return (xDist <= this.getWidth() && yDist <= this.getHeight());
    }

    //@source chat gpt helped create and structure this class
}
