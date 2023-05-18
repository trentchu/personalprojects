package byow.Core;

public class Avatar {
    private int xPos;
    private int yPos;

    public Avatar(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getX() {
        return xPos;
    }

    public int getY() {
        return yPos;
    }

    public void setX(int xPos) {
        this.xPos = xPos;
    }

    public void setY(int yPos) {
        this.yPos = yPos;
    }
    //@source chat gpt helped create and structure this class
}
