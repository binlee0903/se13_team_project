package org.se13.online;

import org.se13.server.TetrisActionHandler;
import org.se13.server.TetrisActionPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OnlineActionRepository {
    public OnlineActionRepository(TetrisServerSocket socket, TetrisActionHandler handler) throws IOException {
        this.socket = socket;
        this.handler = handler;
    }

    public void read() {
        while (true) {
            try {
                TetrisActionPacket packet = socket.read();
                handler.request(packet);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    private static final Logger log = LoggerFactory.getLogger(OnlineActionRepository.class);
    private TetrisActionHandler handler;
    private TetrisServerSocket socket;
}
