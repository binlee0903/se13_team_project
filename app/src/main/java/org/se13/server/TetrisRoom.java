package org.se13.server;

import org.se13.game.tetris.TetrisGame;

public class TetrisRoom {

    private TetrisClient player;
    private TetrisGame playerGame;

    private boolean isPlayerReady;

    public TetrisRoom(TetrisClient player, TetrisGame playerGame) {
        this.player = player;
        this.playerGame = playerGame;
        playerGame.subscribe(player::response);
    }

    public void startGame() {
        playerGame.startGame();
    }
}
