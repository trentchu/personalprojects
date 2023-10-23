import javax.swing.*;

public class Rook extends Piece {
    public Rook(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Chessboard board, int startX, int startY, int endX, int endY) {
        if (startX == endX && startY == endY) {
            return false; // No movement
        }

        if (startX != endX && startY != endY) {
            return false; // Not moving in a straight line
        }

        int stepX = startX < endX ? 1 : -1;
        int stepY = startY < endY ? 1 : -1;

        int x = startX + stepX;
        int y = startY + stepY;

        while (x != endX || y != endY) {
            if (board.getPiece(x, y) != null) {
                return false; // There is a piece in the way
            }

            x += stepX;
            y += stepY;
        }

        // Check if the destination is occupied by a piece of the same color
        Piece destinationPiece = board.getPiece(endX, endY);
        if (destinationPiece != null && destinationPiece.isWhite() == this.isWhite()) {
            return false;
        }

        return true;
    }

    @Override
    public ImageIcon getImageIcon() {
        String imagePath = isWhite() ? "chesspieces/wR.png" : "chesspieces/bR.png";
        return new ImageIcon(imagePath);
    }
}

