package org.se13.server;

import org.se13.game.event.AttackTetrisBlocks;
import org.se13.game.event.TetrisEvent;
import org.se13.game.tetris.DefaultTetrisGame;

public class TetrisSession {

    private TetrisClient player;
    private DefaultTetrisGame playerGame;

    private boolean isPlayerReady;

    public TetrisSession(TetrisClient player, DefaultTetrisGame playerGame) {
        this.player = player;
        this.playerGame = playerGame;
    }

    public void startGame(TetrisEventHandler handler) {
        playerGame.startGame();
        playerGame.subscribe((event) -> handler.handle(player.getUserId(), event));
    }

    public void response(TetrisEvent event) {
        player.response(event);
    }

    public void setReady(boolean isPlayerReady) {
        this.isPlayerReady = isPlayerReady;
    }

    public boolean isPlayerReady() {
        return isPlayerReady;
    }

    public void attack(AttackTetrisBlocks blocks) {
        playerGame.attacked(blocks);
    }
}
