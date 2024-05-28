package org.se13.online;

import org.se13.game.event.TetrisEvent;
import org.se13.utils.Subscriber;
import org.se13.view.tetris.TetrisEventRepository;
import org.se13.view.tetris.TetrisGameEndData;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class OnlineEventRepository implements TetrisEventRepository {
    public OnlineEventRepository(TetrisServerSocket socket, ExecutorService service) throws IOException {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void gameOver(TetrisGameEndData endData) {
        service.execute(() -> {
            try {
                socket.gameOver(endData.userID());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void response(TetrisEvent event, int userId) {
        service.execute(() -> {
            try {
                socket.write(event, userId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void response(TetrisEvent event) {
        service.execute(() -> {
            try {
                socket.write(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void subscribe(Subscriber<TetrisEvent> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
        throw new UnsupportedOperationException("클라이언트가 서버의 OnlineEventRepository에 구독할 수 없습니다.");
    }

    private TetrisServerSocket socket;
    private ExecutorService service;
}
