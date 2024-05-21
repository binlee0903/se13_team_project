package org.se13.online;

import org.se13.game.event.TetrisEvent;
import org.se13.utils.Subscriber;
import org.se13.view.tetris.TetrisEventRepository;
import org.se13.view.tetris.TetrisGameEndData;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class OnlineEventRepository implements TetrisEventRepository {
    private ExecutorService service;
    private PrintWriter out;

    public OnlineEventRepository(Socket socket, ExecutorService service) throws IOException {
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.service = service;
    }

    @Override
    public void gameOver(TetrisGameEndData endData) {
        // TODO: 직렬화 필요
        service.execute(() -> out.write(""));
    }

    @Override
    public void response(TetrisEvent event) {
        // TODO: 직렬화 필요
        service.execute(() -> out.write(""));
    }

    @Override
    public void subscribe(Subscriber<TetrisEvent> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
        throw new UnsupportedOperationException("클라이언트가 서버의 OnlineEventRepository에 구독할 수 없습니다.");
    }
}
