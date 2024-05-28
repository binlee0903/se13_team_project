package org.se13.online;

import org.se13.game.action.TetrisAction;
import org.se13.view.tetris.TetrisActionRepository;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class ClientActionRepository implements TetrisActionRepository {
    public ClientActionRepository(ExecutorService service, TetrisClientSocket client) {
        this.service = service;
        this.socket = client;
    }

    private void write(TetrisAction action) {
        service.execute(() -> {
            try {
                socket.write(action);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void connect() {
        write(TetrisAction.START);
    }

    @Override
    public void immediateBlockPlace() {
        write(TetrisAction.IMMEDIATE_BLOCK_PLACE);
    }

    @Override
    public void moveBlockDown() {
        write(TetrisAction.MOVE_BLOCK_DOWN);
    }

    @Override
    public void moveBlockLeft() {
        write(TetrisAction.MOVE_BLOCK_LEFT);
    }

    @Override
    public void moveBlockRight() {
        write(TetrisAction.MOVE_BLOCK_RIGHT);
    }

    @Override
    public void rotateBlockCW() {
        write(TetrisAction.ROTATE_BLOCK_CW);
    }

    @Override
    public void togglePauseState() {
        write(TetrisAction.TOGGLE_PAUSE_STATE);
    }

    @Override
    public void exitGame() {
        write(TetrisAction.EXIT_GAME);
    }

    private ExecutorService service;
    private TetrisClientSocket socket;
}
