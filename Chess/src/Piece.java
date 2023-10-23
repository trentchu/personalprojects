import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class Piece {
    private boolean white;

    public Piece(boolean white) {
        this.white = white;
    }

    public boolean isWhite() {
        return white;
    }

    public abstract boolean canMove(Chessboard board, int startX, int startY, int endX, int endY);

    public abstract ImageIcon getImageIcon();
}

