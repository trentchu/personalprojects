public class Chessboard {
    private Piece[][] board;

    public Chessboard() {
        board = new Piece[8][8];
        initializeBoard();
    }

    public void initializeBoard() {
        // Add pawns
        for (int i = 0; i < 8; i++) {
            board[i][1] = new Pawn(false);
            board[i][6] = new Pawn(true);
        }

        // Add rooks
        board[0][0] = new Rook(false);
        board[7][0] = new Rook(false);
        board[0][7] = new Rook(true);
        board[7][7] = new Rook(true);

        // Add knights
        board[1][0] = new Knight(false);
        board[6][0] = new Knight(false);
        board[1][7] = new Knight(true);
        board[6][7] = new Knight(true);

        // Add bishops
        board[2][0] = new Bishop(false);
        board[5][0] = new Bishop(false);
        board[2][7] = new Bishop(true);
        board[5][7] = new Bishop(true);

        // Add queens
        board[3][0] = new Queen(false);
        board[3][7] = new Queen(true);

        // Add kings
        board[4][0] = new King(false);
        board[4][7] = new King(true);
    }


    public boolean movePiece(int startX, int startY, int endX, int endY) {
        if (startX < 0 || startX >= 8 || startY < 0 || startY >= 8 ||
                endX < 0 || endX >= 8 || endY < 0 || endY >= 8) {
            throw new IllegalArgumentException("Invalid coordinates.");
        }

        Piece pieceToMove = getPiece(startX, startY);

        if (pieceToMove != null && pieceToMove.canMove(this, startX, startY, endX, endY)) {
            setPiece(startX, startY, null);
            setPiece(endX, endY, pieceToMove);
            return true;
        }

        return false;
    }

    public void setPiece(int x, int y, Piece piece) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            board[x][y] = piece;
        } else {
            throw new IllegalArgumentException("Invalid coordinates: (" + x + ", " + y + ")");
        }
    }

    public Piece getPiece(int x, int y) {
        return board[x][y];
    }
}

