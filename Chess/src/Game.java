import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends JFrame implements ActionListener {
    private Chessboard chessboard;
    private boolean isWhiteTurn;
    private JButton[][] boardButtons;
    private int selectedX = -1;
    private int selectedY = -1;

    private void updateBoard() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = chessboard.getPiece(x, y);
                boardButtons[x][y].setIcon(piece == null ? null : piece.getImageIcon());

                // Set the background color of the buttons to create a checkerboard pattern
                Color backgroundColor = (x + y) % 2 == 0 ? Color.WHITE : Color.GRAY;
                boardButtons[x][y].setBackground(backgroundColor);
            }
        }
    }

    public Game() {
        chessboard = new Chessboard();
        isWhiteTurn = true;
        initUI();
    }

    private void initUI() {
        setTitle("Chess Game");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(8, 8));
        boardPanel.setPreferredSize(new Dimension(800, 800));

        boardButtons = new JButton[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton button = new JButton();
                button.addActionListener(this);
                button.setOpaque(true);
                boardButtons[i][j] = button;

                boardPanel.add(button);

                if (chessboard.getPiece(i, j) != null) {
                    button.setIcon(chessboard.getPiece(i, j).getImageIcon());
                }

                Color tileColor = (i + j) % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY;
                button.setBackground(tileColor);

            }
        }

        getContentPane().add(boardPanel);
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        int clickedX = -1;
        int clickedY = -1;

        // Find the clicked button's coordinates
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (boardButtons[x][y] == clickedButton) {
                    clickedX = x;
                    clickedY = y;
                }
            }
        }

        if (selectedX == -1 && selectedY == -1) {
            // Select a piece if it's the correct player's turn
            Piece clickedPiece = chessboard.getPiece(clickedX, clickedY);
            if (clickedPiece != null && clickedPiece.isWhite() == isWhiteTurn) {
                selectedX = clickedX;
                selectedY = clickedY;
                clickedButton.setBackground(Color.YELLOW);
            }
        } else {
            // Deselect the piece if the same button is clicked again
            if (selectedX == clickedX && selectedY == clickedY) {
                selectedX = -1;
                selectedY = -1;
                updateBoard();
            } else {
                // Move the selected piece if the move is valid
                if (chessboard.movePiece(selectedX, selectedY, clickedX, clickedY)) {
                    isWhiteTurn = !isWhiteTurn;
                }

                selectedX = -1;
                selectedY = -1;
                updateBoard();
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Game());
    }
}

