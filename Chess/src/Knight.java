import javax.swing.*;
public class Knight extends Piece {
    public Knight(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Chessboard board, int startX, int startY, int endX, int endY) {
        int deltaX = Math.abs(endX - startX);
        int deltaY = Math.abs(endY - startY);

        // Check if the knight is making an L-shaped move (2 squares in one direction and 1 square in the other)
        if ((deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2)) {
            Piece destinationPiece = board.getPiece(endX, endY);

            // Check if the destination is unoccupied or occupied by an opponent's piece
            if (destinationPiece == null || destinationPiece.isWhite() != this.isWhite()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ImageIcon getImageIcon() {
        String imagePath = isWhite() ? "chesspieces/wN.png" : "chesspieces/bN.png";
        return new ImageIcon(imagePath);
    }
}

