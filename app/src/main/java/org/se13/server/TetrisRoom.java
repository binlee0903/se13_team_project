package org.se13.server;

import org.se13.game.tetris.DefaultTetrisGame;

public class TetrisRoom {

    private TetrisClient player;
    private DefaultTetrisGame playerGame;

    private boolean isPlayerReady;

    public TetrisRoom(TetrisClient player, DefaultTetrisGame playerGame) {
        this.player = player;
        this.playerGame = playerGame;
        playerGame.subscribe(player::response);
    }

    public void startGame() {
        playerGame.startGame();
    }
}
