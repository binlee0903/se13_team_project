package org.se13.online;

import org.se13.game.action.TetrisAction;
import org.se13.server.TetrisActionHandler;
import org.se13.server.TetrisActionPacket;
import org.se13.view.tetris.TetrisActionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ClientActionController {
    public ClientActionController(int userId, Socket socket, TetrisActionHandler handler) throws IOException {
        this.userId = userId;
        this.socket = socket;
        this.handler = handler;
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void write(int userId, TetrisAction action) {
//        handleInputAction(userId, action);
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

    private int userId;
    private ExecutorService service;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private TetrisActionHandler handler;
    private Socket socket;
}
