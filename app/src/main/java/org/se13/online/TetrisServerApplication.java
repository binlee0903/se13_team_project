package org.se13.online;

import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.server.LocalBattleTetrisServer;
import org.se13.server.TetrisActionHandler;
import org.se13.server.TetrisClient;
import org.se13.server.TetrisServer;
import org.se13.view.tetris.TetrisEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TetrisServerApplication {
    private static final Logger log = LoggerFactory.getLogger(TetrisServerApplication.class);
    private Thread matchingThread = null;
    private ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
    private ArrayList<LocalBattleTetrisServer> servers = new ArrayList<>();

    private BlockingQueue<Socket> waiting = new ArrayBlockingQueue<>(2);
    private ServerSocket serverSocket = new ServerSocket(5555);

    private int connectionTrys;

    public TetrisServerApplication() throws IOException {

    }

    public static void main(String[] args) throws IOException {
        new TetrisServerApplication().start();
    }

    private void start() throws IOException {
        // Socket 2개를 LocalBattleServer로 연결해주는 매칭 쓰레드 실행
        matchingThread = new Thread(() -> {
            try {
                matching();
            } catch (InterruptedException | IOException e) {
                log.error(e.getMessage());
            }
        });
        matchingThread.start();

        // 소켓 접속 처리
        while (true) {
            waiting.add(serverSocket.accept());
        }
    }

    private void matching() throws InterruptedException, IOException {
        while (true) {
            // 만약 대기실이 있었다면 방장(connect1)이 게임 설정하고 접속(userId) 처리를 추가할 수 있습니다.
            GameLevel level = GameLevel.NORMAL;
            GameMode mode = GameMode.ITEM;

            LocalBattleTetrisServer server = new LocalBattleTetrisServer(level, mode);

            OnlineActionRepository handler1 = createActionRepository(waiting.take(), server);
            OnlineActionRepository handler2 = createActionRepository(waiting.take(), server);

            service.execute(handler1::read);
            service.execute(handler2::read);
        }
    }

    private OnlineActionRepository createActionRepository(Socket connection, TetrisServer server) throws IOException {
        int playerId = connectionTrys++;
        TetrisEventRepository eventRepository = new OnlineEventRepository(connection, service);
        TetrisClient client = new TetrisClient(playerId, eventRepository);
        TetrisActionHandler actionHandler = server.connect(client);
        return new OnlineActionRepository(playerId, connection, actionHandler);
    }
}
