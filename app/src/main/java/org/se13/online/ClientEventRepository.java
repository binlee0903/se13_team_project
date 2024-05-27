package org.se13.online;

import org.se13.game.event.TetrisEvent;
import org.se13.server.TetrisEventHandler;
import org.se13.utils.Observer;
import org.se13.utils.Subscriber;
import org.se13.view.tetris.TetrisActionRepository;
import org.se13.view.tetris.TetrisEventRepository;
import org.se13.view.tetris.TetrisGameEndData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ClientEventRepository implements TetrisEventRepository {
    public ClientEventRepository(ObjectOutputStream oos, ObjectInputStream ois) {
        this.out = oos;
        this.in = ois;
        this.observer = new Observer<>();
        this.isGameOver = new Observer<>();
    }

    public void read() {
        while (true) {
            try {
                OnlineEventPacket packet = (OnlineEventPacket) in.readObject();
                this.userId = packet.userId();
                if (packet.event() != null) {
                    response(packet.event());
                } else {
                    gameOver(packet.endData());
                }
            } catch (IOException | ClassNotFoundException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void gameOver(TetrisGameEndData endData) {
        isGameOver.setValue(endData);
    }

    @Override
    public void response(TetrisEvent event) {
        this.observer.setValue(event);
    }

    @Override
    public void subscribe(Subscriber<TetrisEvent> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
        this.observer.subscribe(subscriber);
        this.isGameOver.subscribe(isGameOver);
    }

    private final Observer<TetrisEvent> observer;
    private final Observer<TetrisGameEndData> isGameOver;

    private static final Logger log = LoggerFactory.getLogger(OnlineActionRepository.class);
    private int userId;
    private ObjectOutputStream out;
    private ObjectInputStream in;
}
