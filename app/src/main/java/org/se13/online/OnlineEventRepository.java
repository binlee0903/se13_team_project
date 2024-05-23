package org.se13.online;

import org.se13.game.event.TetrisEvent;
import org.se13.utils.Subscriber;
import org.se13.view.tetris.TetrisEventRepository;
import org.se13.view.tetris.TetrisGameEndData;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class OnlineEventRepository implements TetrisEventRepository {
    public OnlineEventRepository(Socket socket, ExecutorService service) throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.service = service;
    }

    @Override
    public void gameOver(TetrisGameEndData endData) {
        OnlineEventPacket packet = new OnlineEventPacket(null, endData);
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
    public void response(TetrisEvent event) {
        OnlineEventPacket packet = new OnlineEventPacket(event, null);
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
    public void subscribe(Subscriber<TetrisEvent> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
        throw new UnsupportedOperationException("클라이언트가 서버의 OnlineEventRepository에 구독할 수 없습니다.");
    }

    private ExecutorService service;
    private ObjectOutputStream out;
}
