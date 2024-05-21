package org.se13.online;

import org.se13.game.action.TetrisAction;
import org.se13.server.TetrisActionHandler;
import org.se13.server.TetrisActionPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class OnlineActionRepository {
    private static final Logger log = LoggerFactory.getLogger(OnlineActionRepository.class);
    private int userId;
    private TetrisActionHandler handler;
    private Socket socket;
    private BufferedReader in;

    public OnlineActionRepository(int userId, Socket socket, TetrisActionHandler handler) throws IOException {
        this.userId = userId;
        this.socket = socket;
        this.handler = handler;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void read() {
        // TODO: 직렬화 코드 필요
        while (true) {
            try {
                int responseCode = in.read();
                TetrisAction code = TetrisAction.fromCode(responseCode);
                TetrisActionPacket packet = new TetrisActionPacket(userId, code);
                handler.request(packet);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
