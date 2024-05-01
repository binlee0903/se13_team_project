package org.se13.view.tetris;

import org.se13.game.action.TetrisAction;
import org.se13.server.TetrisActionHandler;
import org.se13.server.TetrisActionPacket;

public class TetrisActionRepositoryImpl implements TetrisActionRepository {

    private int userId;
    private TetrisActionHandler handler;

    public TetrisActionRepositoryImpl(int userId, TetrisActionHandler handler) {
        this.userId = userId;
        this.handler = handler;
    }

    @Override
    public void connect() {
        handler.request(new TetrisActionPacket(userId, TetrisAction.START));
    }

    @Override
    public void immediateBlockPlace() {
        handler.request(new TetrisActionPacket(userId, TetrisAction.IMMEDIATE_BLOCK_PLACE));
    }

    @Override
    public void moveBlockDown() {
        handler.request(new TetrisActionPacket(userId, TetrisAction.MOVE_BLOCK_DOWN));
    }

    @Override
    public void moveBlockLeft() {
        handler.request(new TetrisActionPacket(userId, TetrisAction.MOVE_BLOCK_LEFT));
    }

    @Override
    public void moveBlockRight() {
        handler.request(new TetrisActionPacket(userId, TetrisAction.MOVE_BLOCK_RIGHT));
    }

    @Override
    public void rotateBlockCW() {
        handler.request(new TetrisActionPacket(userId, TetrisAction.ROTATE_BLOCK_CW));
    }

    @Override
    public void togglePauseState() {
        handler.request(new TetrisActionPacket(userId, TetrisAction.TOGGLE_PAUSE_STATE));
    }

    @Override
    public void exitGame() {
        handler.request(new TetrisActionPacket(userId, TetrisAction.EXIT_GAME));
    }
}
