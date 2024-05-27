package org.se13.online;

import org.se13.game.action.TetrisAction;
import org.se13.server.TetrisActionHandler;
import org.se13.server.TetrisActionPacket;
import org.se13.view.tetris.TetrisActionRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ClientActionRepository implements TetrisActionRepository {
    public ClientActionRepository(int userId, Socket socket) throws IOException {
        this.userId = userId;
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void write(int userId, TetrisAction action) {
        TetrisActionPacket packet = new TetrisActionPacket(userId, action);
        service.execute(() -> {
            try {
                out.writeObject(packet);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void connect() {
        write(userId, TetrisAction.START);
    }

    @Override
    public void immediateBlockPlace() {
        write(userId, TetrisAction.IMMEDIATE_BLOCK_PLACE);
    }

    @Override
    public void moveBlockDown() {
        write(userId, TetrisAction.MOVE_BLOCK_DOWN);
    }

    @Override
    public void moveBlockLeft() {
        write(userId, TetrisAction.MOVE_BLOCK_LEFT);
    }

    @Override
    public void moveBlockRight() {
        write(userId, TetrisAction.MOVE_BLOCK_RIGHT);
    }

    @Override
    public void rotateBlockCW() {
        write(userId, TetrisAction.ROTATE_BLOCK_CW);
    }

    @Override
    public void togglePauseState() {
        write(userId, TetrisAction.TOGGLE_PAUSE_STATE);
    }

    @Override
    public void exitGame() {
        write(userId, TetrisAction.EXIT_GAME);
    }

    private int userId;
    private Socket socket;
    private ExecutorService service;
    private ObjectOutputStream out;
    private ObjectInputStream in;
}
