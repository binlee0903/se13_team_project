package org.se13.server;

import org.se13.game.action.TetrisAction;
import org.se13.game.event.AttackTetrisBlocks;
import org.se13.game.event.TetrisEvent;
import org.se13.game.tetris.TetrisGame;

public class TetrisSession {

    private TetrisClient player;
    private TetrisGame playerGame;

    private boolean isPlayerReady;

    public TetrisSession(TetrisClient player, TetrisGame playerGame) {
        this.player = player;
        this.playerGame = playerGame;
    }

    public void startGame(TetrisEventHandler handler) {
        playerGame.subscribe((event) -> handler.handle(player.getUserId(), event));
        playerGame.startGame();
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

    public void requestInput(TetrisAction action) {
        playerGame.requestInput(action);
    }

    public void pulse(long nanoTime) {
        playerGame.pulse(nanoTime);
    }
}
