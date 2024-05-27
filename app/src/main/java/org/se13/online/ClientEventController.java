package org.se13.online;

import org.se13.server.TetrisEventHandler;
import org.se13.view.tetris.TetrisEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ClientEventController {
    private TetrisEventRepository eventRepository;

    public ClientEventController(TetrisEventRepository repository, Socket socket, ExecutorService service) throws IOException {
        this.eventRepository = repository;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.service = service;
    }

    public void read() {
        while (true) {
            try {
                OnlineEventPacket packet = (OnlineEventPacket) in.readObject();
                this.userId = packet.userId();
                if (packet.event() != null) {
                    eventRepository.response(packet.event());
                } else {
                    eventRepository.gameOver(packet.endData());
                }
            } catch (IOException | ClassNotFoundException e) {
                log.error(e.getMessage());
            }
        }
    }

    private static final Logger log = LoggerFactory.getLogger(OnlineActionRepository.class);
    private int userId;
    private TetrisEventHandler handler;
    private ExecutorService service;
    private ObjectOutputStream out;
    private ObjectInputStream in;
}
