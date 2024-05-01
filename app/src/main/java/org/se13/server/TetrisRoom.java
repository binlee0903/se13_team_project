package org.se13.server;

import org.se13.game.tetris.DefaultTetrisGame;

public class TetrisRoom {

    private TetrisClient player;
    private DefaultTetrisGame playerGame;

    private boolean isPlayerReady;

    public TetrisRoom(TetrisClient player, DefaultTetrisGame playerGame) {
        this.player = player;
        this.playerGame = playerGame;
    }

    public TetrisActionHandler connect() {
        return request -> {
            switch (request.action()) {
                case START -> isPlayerReady = true;
                case EXIT_GAME -> isPlayerReady = false;
            }
        };
    }

    public boolean isPlayerReady() {
        return isPlayerReady;
    }

    public void startGame() {
        playerGame.startGame();
    }
}
