package org.se13;

import org.se13.game.event.ReadyForMatching;
import org.se13.game.event.TetrisEvent;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.online.OnlineActionRepository;
import org.se13.online.OnlineEventRepository;
import org.se13.online.TetrisServerSocket;
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
    private ServerSocket serverSocket;
    private int connectionTrys = 1;

    public TetrisServerApplication() throws IOException {
        this.serverSocket = new ServerSocket(5555); // 기본 포트 번호 사용
    }

    public static void main(String[] args) throws IOException {
        new TetrisServerApplication().start();
    }

    private void start() {
        // Socket 2개를 LocalBattleServer로 연결해주는 매칭 쓰레드 실행
        matchingThread = new Thread(() -> {
            try {
                matching();
            } catch (InterruptedException | IOException e) {
                log.error(e.getMessage(), e);
            }
        });
        matchingThread.start();

        // 소켓 접속 처리
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                log.info("New client connected: " + clientSocket.getInetAddress());
                waiting.add(clientSocket);
            } catch (IOException e) {
                log.error("Error accepting client connection: ", e);
            }
        }
    }

    private void matching() throws InterruptedException, IOException {
        while (true) {
            log.info("Waiting for players to connect...");

            TetrisServerSocket player1Socket = new TetrisServerSocket(waiting.take());
            int player1Id = connectionTrys++;
            player1Socket.setUserId(player1Id);
            log.info("Player {} connected: ", player1Id);

            TetrisServerSocket player2Socket = new TetrisServerSocket(waiting.take());
            int player2Id = connectionTrys++;
            player2Socket.setUserId(player2Id);
            log.info("Player {} connected: ", player2Id);

            // Game setup
            GameLevel level = GameLevel.NORMAL;
            GameMode mode = GameMode.DEFAULT;

            LocalBattleTetrisServer server = new LocalBattleTetrisServer(level, mode) {
                @Override
                protected void broadcast(TetrisEvent event, int userId) {
                    sessions.forEach((playerId, session) -> {
                        session.response(event);
                    });
                }
            };

            OnlineActionRepository handler1 = createActionRepository(player1Id, player1Socket, server);
            OnlineActionRepository handler2 = createActionRepository(player2Id, player2Socket, server);

            log.info("Game started with two players.");

            service.execute(handler1::read);
            service.execute(handler2::read);

            player1Socket.write(new ReadyForMatching(player1Id, player2Id));
            player2Socket.write(new ReadyForMatching(player2Id, player1Id));
        }
    }

    private OnlineActionRepository createActionRepository(int playerId, TetrisServerSocket socket, TetrisServer server) throws IOException {
        TetrisEventRepository eventRepository = new OnlineEventRepository(socket, service);
        TetrisClient client = new TetrisClient(playerId, eventRepository);
        TetrisActionHandler actionHandler = server.connect(client);
        return new OnlineActionRepository(socket, actionHandler);
    }
}
