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

import java.io.*;
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

    private int connectionTrys;

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
                log.error(e.getMessage());
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

            Socket player1Socket = waiting.take();
            sendIsFirst(player1Socket, true);
            log.info("Player 1 connected: " + player1Socket.getInetAddress());

            Socket player2Socket = waiting.take();
            sendIsFirst(player2Socket, false);
            log.info("Player 2 connected: " + player2Socket.getInetAddress());

            // Game setup
            GameLevel level = GameLevel.NORMAL;
            GameMode mode = GameMode.ITEM;

            LocalBattleTetrisServer server = new LocalBattleTetrisServer(level, mode);

            OnlineActionRepository handler1 = createActionRepository(player1Socket, server);
            OnlineActionRepository handler2 = createActionRepository(player2Socket, server);

            log.info("Game started with two players.");

            service.execute(handler1::read);
            service.execute(handler2::read);
        }
    }

    private void sendIsFirst(Socket socket, boolean isFirst) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        try {
            oos.writeObject(new IsFirst(isFirst));
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OnlineActionRepository createActionRepository(Socket connection, TetrisServer server) throws IOException {
        int playerId = connectionTrys++;
        TetrisEventRepository eventRepository = new OnlineEventRepository(playerId % 2, connection, service);
        TetrisClient client = new TetrisClient(playerId % 2, eventRepository);
        TetrisActionHandler actionHandler = server.connect(client);
        return new OnlineActionRepository(playerId % 2, connection, actionHandler);
    }
}
