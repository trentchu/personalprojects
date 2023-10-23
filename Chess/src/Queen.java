import javax.swing.*;

public class Queen extends Piece {
    public Queen(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Chessboard board, int startX, int startY, int endX, int endY) {
        int deltaX = Math.abs(endX - startX);
        int deltaY = Math.abs(endY - startY);

        // Check if the queen is moving horizontally, vertically, or diagonally
        if (startX == endX || startY == endY || deltaX == deltaY) {
            int stepX = startX < endX ? 1 : startX > endX ? -1 : 0;
            int stepY = startY < endY ? 1 : startY > endY ? -1 : 0;

            int x = startX + stepX;
            int y = startY + stepY;

            while (x != endX || y != endY) {
                if (board.getPiece(x, y) != null) {
                    return false; // There is a piece in the way
                }

                x += stepX;
                y += stepY;
            }

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
        String imagePath = isWhite() ? "chesspieces/wQ.png" : "chesspieces/bQ.png";
        return new ImageIcon(imagePath);
    }
}


