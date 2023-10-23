import javax.swing.*;

public class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Chessboard board, int startX, int startY, int endX, int endY) {
        int direction = isWhite() ? -1 : 1;
        int deltaY = endY - startY;
        int deltaX = Math.abs(endX - startX);

        // Check if the pawn is moving forward one square
        if (deltaX == 0 && deltaY == direction && board.getPiece(endX, endY) == null) {
            return true;
        }

        // Check if the pawn is moving forward two squares (only allowed from starting position)
        if (deltaX == 0 && deltaY == 2 * direction && board.getPiece(endX, endY) == null
                && ((isWhite() && startY == 6) || (!isWhite() && startY == 1))
                && board.getPiece(startX, startY + direction) == null) {
            return true;
        }

        // Check if the pawn is capturing an opponent's piece diagonally
        if (deltaX == 1 && deltaY == direction) {
            Piece destinationPiece = board.getPiece(endX, endY);
            if (destinationPiece != null && destinationPiece.isWhite() != this.isWhite()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ImageIcon getImageIcon() {
        String imagePath = isWhite() ? "chesspieces/wP.png" : "chesspieces/bP.png";
        return new ImageIcon(imagePath);
    }
}


// Implement other piece classes similarly

