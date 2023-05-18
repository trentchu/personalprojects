package byow.GameState;

import byow.InputDemo.Game;

public class GameState {
    private final Game game;

    public GameState(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}

