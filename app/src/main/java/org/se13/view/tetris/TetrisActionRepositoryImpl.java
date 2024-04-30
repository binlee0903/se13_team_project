package org.se13.view.tetris;

import org.se13.game.action.TetrisAction;
import org.se13.server.TetrisServer;

public class TetrisActionRepositoryImpl implements TetrisActionRepository {

    private TetrisServer server;

    public TetrisActionRepositoryImpl(TetrisServer server) {
        this.server = server;
    }

    @Override
    public void connect() {
        server.handle(TetrisAction.CONNECT);
    }

    @Override
    public void immediateBlockPlace() {
        server.handle(TetrisAction.IMMEDIATE_BLOCK_PLACE);
    }

    @Override
    public void moveBlockDown() {
        server.handle(TetrisAction.MOVE_BLOCK_DOWN);
    }

    @Override
    public void moveBlockLeft() {
        server.handle(TetrisAction.MOVE_BLOCK_LEFT);
    }

    @Override
    public void moveBlockRight() {
        server.handle(TetrisAction.MOVE_BLOCK_RIGHT);
    }

    @Override
    public void rotateBlockCW() {
        server.handle(TetrisAction.ROTATE_BLOCK_CW);
    }

    @Override
    public void togglePauseState() {
        server.handle(TetrisAction.TOGGLE_PAUSE_STATE);
    }

    @Override
    public void exitGame() {
        server.handle(TetrisAction.EXIT_GAME);
    }
}
