package org.se13.online;

import org.se13.server.TetrisActionHandler;
import org.se13.server.TetrisActionPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OnlineActionRepository {
    public OnlineActionRepository(int userId, Socket socket, TetrisActionHandler handler) throws IOException {
        this.userId = userId;
        this.socket = socket;
        this.handler = handler;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void read() {
        while (true) {
            try {
                TetrisActionPacket packet = (TetrisActionPacket) in.readObject();
                handler.request(packet);
            } catch (IOException | ClassNotFoundException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void sendAction(TetrisActionPacket packet) {
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            log.error("Failed to send action: ", e);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(OnlineActionRepository.class);
    private int userId;
    private TetrisActionHandler handler;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
}
