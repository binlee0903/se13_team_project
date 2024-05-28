package org.se13.online;

import org.se13.game.event.TetrisEvent;
import org.se13.utils.Observer;
import org.se13.utils.Subscriber;
import org.se13.view.tetris.TetrisEventRepository;
import org.se13.view.tetris.TetrisEventRepositoryImpl;
import org.se13.view.tetris.TetrisGameEndData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ReadNetworkRepository {
    private static final Logger log = LoggerFactory.getLogger(ReadNetworkRepository.class);
    private int playerId;
    private TetrisClientSocket socket;
    private Observer<TetrisEvent> player;
    private Observer<TetrisEvent> opponent;

    private Observer<TetrisGameEndData> playerGameEnd;
    private Observer<TetrisGameEndData> opponentGameEnd;

    public ReadNetworkRepository(TetrisClientSocket client, int playerId) {
        this.socket = client;
        this.playerId = playerId;
        this.player = new Observer<>();
        this.opponent = new Observer<>();
        this.playerGameEnd = new Observer<>();
        this.opponentGameEnd = new Observer<>();
    }

    public void read() {
        new Thread(() -> {
            while (true) {
                try {
                    TetrisEventPacket packet = socket.read();
                    if (packet.userId() == playerId) {
                        player.setValue(packet.event());
                    } else {
                        opponent.setValue(packet.event());
                    }

                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }).start();
    }

    public TetrisEventRepository playerEventRepository() {
        return new TetrisEventRepositoryImpl() {
            @Override
            public void subscribe(Subscriber<TetrisEvent> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
                player.subscribe(subscriber);
                playerGameEnd.subscribe(isGameOver);
            }
        };
    }

    public TetrisEventRepository opponentEventRepository() {
        return new TetrisEventRepositoryImpl() {
            @Override
            public void subscribe(Subscriber<TetrisEvent> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
                opponent.subscribe(subscriber);
                opponentGameEnd.subscribe(isGameOver);
            }
        };
    }
}
