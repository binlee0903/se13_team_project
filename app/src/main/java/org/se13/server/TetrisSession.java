package org.se13.server;

import org.se13.game.action.TetrisAction;
import org.se13.game.event.*;
import org.se13.game.tetris.TetrisGame;
import org.se13.view.tetris.TetrisGameEndData;

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

    public void stopGame() {
        player.response(new GameEndEvent());
    }

    public TetrisGameEndData stopBattleGame() {
        playerGame.stopBattleGame();
        return new TetrisGameEndData(player.getUserId(), playerGame.getScore(), false, "");
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

    public void attackedByPlayer(AttackingTetrisBlocks blocks) {
        playerGame.attacked(blocks);
    }

    public void attacked(AttackedTetrisBlocks state) {
        player.response(state);
    }

    public void insertAttackedBlocks(InsertAttackBlocksEvent event) {
        player.response(event);
    }

    public void requestInput(TetrisAction action) {
        playerGame.requestInput(action);
    }

    public void pulse(long nanoTime) {
        playerGame.pulse(nanoTime);
    }
}
