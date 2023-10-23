import javax.swing.*;

public class King extends Piece {
    public King(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Chessboard board, int startX, int startY, int endX, int endY) {
        int deltaX = Math.abs(endX - startX);
        int deltaY = Math.abs(endY - startY);

        // Check if the king is moving one square in any direction (horizontally, vertically, or diagonally)
        if ((deltaX <= 1) && (deltaY <= 1)) {
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
        String imagePath = isWhite() ? "chesspieces/wK.png" : "chesspieces/bK.png";
        return new ImageIcon(imagePath);
    }
}

