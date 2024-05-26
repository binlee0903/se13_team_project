package org.se13.online;

import org.se13.game.action.TetrisAction;
import org.se13.game.event.*;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.tetris.TetrisGame;
import org.se13.server.*;
import org.se13.view.tetris.TetrisGameEndData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.*;

// 클라이언트 전용 TetrisServer
public class OnlineBattleTetrisServer implements TetrisServer {
    public OnlineBattleTetrisServer(String serverAddress, int port) throws IOException {
        this.socket = new Socket(serverAddress, port);
        this.service = Executors.newVirtualThreadPerTaskExecutor();
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.serverAddress = serverAddress;
        this.port = port;
        this.level = GameLevel.NORMAL;
        this.mode = GameMode.DEFAULT;
        this.tetrisTimer = new Timer();
        this.sessions = new HashMap<>();
        this.handlers = new HashMap<>();
    }

    public void write(int userId, TetrisAction action) {
        TetrisActionPacket packet = new TetrisActionPacket(userId, action);
        service.execute(() -> {
            try {
                out.writeObject(packet);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void read() {
        while (true) {
            try {
                OnlineEventPacket packet = (OnlineEventPacket) in.readObject();
                if (packet.event() != null) {
                    // event 처리
                    handlers.get(0).handle(0, packet.event());
                } else if (packet.endData() != null) {
                    // endData 처리
                    responseGameOver(packet.endData());
                } else {
                    throw new RuntimeException("event와 endData 중 하나는 null이 아니어야 합니다.");
                }
            } catch (IOException | ClassNotFoundException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void responseGameOver(int score, boolean isItemMode, String difficulty) {
        endDatas = new LinkedList<>();
        sessions.forEach((playerId, session) -> {
            endDatas.add(session.stopBattleGame());
            session.stopGame();
        });
        tetrisTimer.cancel();
    }

    @Override
    public List<TetrisGameEndData> getEndData() {
        return List.of();
    }

    public void responseGameOver(TetrisGameEndData endData) {
        this.endData = endData;
        sessions.forEach((playerId, session) -> {
            session.stopGame();
        });
        tetrisTimer.cancel();
    }

    @Override
    public TetrisActionHandler connect(TetrisClient client) {
        sessions.put(client.getUserId(), new TetrisSession(client, new TetrisGame(level, mode, this)));
        handlers.put(client.getUserId(), createHandlers());

        return packet -> {
            switch (packet.action()) {
                case START -> handleStartGame(packet.userId());
                case IMMEDIATE_BLOCK_PLACE,
                     ROTATE_BLOCK_CW,
                     MOVE_BLOCK_LEFT,
                     MOVE_BLOCK_DOWN,
                     MOVE_BLOCK_RIGHT -> write(packet.userId(), packet.action());
            }
        };
    }

    private void handleInputAction(int userId, TetrisAction action) {
        TetrisSession session = sessions.get(userId);
        if (session == null) {
            broadcast(new ServerErrorEvent("세션이 종료되었습니다." + userId), userId);
            return;
        }

        session.requestInput(action);
    }

    @Override
    public void disconnect(TetrisClient client) {
        sessions.remove(client.getUserId());
    }

    public void handleStartGame(int userId) {
        {
            TetrisSession session = sessions.get(userId);
            if (session == null) {
                broadcast(new ServerErrorEvent("세션이 종료되었습니다."), userId);
                return;
            }

            if (session.isPlayerReady()) {
                broadcast(new ServerErrorEvent("이미 레디중입니다."), userId);
                return;
            }

            session.setReady(true);
        }

        AtomicBoolean isAllPlayerReady = new AtomicBoolean(true);

        sessions.forEach((_userId, session) -> {
            if (!session.isPlayerReady()) {
                isAllPlayerReady.set(false);
            }
        });

        if (isAllPlayerReady.get()) {
            sessions.forEach((playerId, session) -> {
                session.startGame(handlers.get(playerId));
            });

            schedule();
        }
    }

    private TetrisEventHandler createHandlers() {
        return (userId, event) -> {
            switch (event) {
                case UpdateTetrisState state -> broadcast(state, userId);
                case AttackedTetrisBlocks state -> handleAttacked(userId, state);
                case AttackingTetrisBlocks blocks -> handleAttacking(userId, blocks);
                case InsertAttackBlocksEvent insertEvent -> handleInsertEvent(userId, insertEvent);
                default -> {
                }
            }
        };
    }

    private void handleInsertEvent(int userID, InsertAttackBlocksEvent insertEvent) {
        sessions.forEach((playerId, session) -> {
            if (playerId == userID) {
                session.insertAttackedBlocks(insertEvent);
            }
        });
    }

    private void handleAttacked(int userID, AttackedTetrisBlocks state) {
        sessions.forEach((playerId, session) -> {
            if (playerId == userID) {
                session.attacked(state);
            }
        });
    }

    private void handleAttacking(int userID, AttackingTetrisBlocks blocks) {
        sessions.forEach((playerId, session) -> {
            if (playerId != userID) {
                session.attackedByPlayer(blocks);
            }
        });
    }

    private void schedule() {
        tetrisTimer = new Timer();
        tetrisTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long nanoTime = System.nanoTime();
                sessions.forEach((_userId, session) -> {
                    session.pulse(nanoTime);
                });
            }
        }, 0, 16);
    }

    private void broadcast(TetrisEvent event, int userId) {
        TetrisSession session = sessions.get(userId);
        if (session == null) return;

        session.response(event);
    }

    private final int maxUser = 2;

    private GameLevel level;
    private GameMode mode;

    private static final Logger log = LoggerFactory.getLogger(OnlineActionRepository.class);
    private Socket socket;
    private ExecutorService service;
    private String serverAddress;
    private int port;
    private TetrisGameEndData endData;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private Timer tetrisTimer;
    private Map<Integer, TetrisSession> sessions;
    private Map<Integer, TetrisEventHandler> handlers;

    private List<TetrisGameEndData> endDatas;
}
