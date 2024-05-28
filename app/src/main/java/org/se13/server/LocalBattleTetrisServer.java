package org.se13.server;

import org.se13.game.action.TetrisAction;
import org.se13.game.event.*;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.tetris.TetrisGame;
import org.se13.view.tetris.TetrisGameEndData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalBattleTetrisServer implements TetrisServer {
    private static final Logger log = LoggerFactory.getLogger(LocalBattleTetrisServer.class);
    private final int maxUser = 2;

    private GameLevel level;
    private GameMode mode;

    private Timer tetrisTimer;
    protected Map<Integer, TetrisSession> sessions;
    private Map<Integer, TetrisEventHandler> handlers;

    private List<TetrisGameEndData> endData;

    public LocalBattleTetrisServer(GameLevel level, GameMode mode) {
        this.level = level;
        this.mode = mode;
        this.tetrisTimer = new Timer();
        this.sessions = new HashMap<>();
        this.handlers = new HashMap<>();
    }

    @Override
    public void responseGameOver(int score, boolean isItemMode, String difficulty) {
        tetrisTimer.cancel();

        sessions.forEach((playerId, session) -> {
            session.stopBattleGame();
        });
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
                     MOVE_BLOCK_RIGHT -> handleInputAction(packet.userId(), packet.action());
            }
        };
    }

    private void handleInputAction(int userId, TetrisAction action) {
        TetrisSession session = sessions.get(userId);
        if (session == null) {
            broadcast(new ServerErrorEvent("Session closed: " + userId));
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
                broadcast(new ServerErrorEvent("Session closed"), userId);
                return;
            }

            if (session.isPlayerReady()) {
                broadcast(new ServerErrorEvent("You are already started"), userId);
                return;
            }

            session.setReady(true);
            log.info("Player{} Ready", userId);
        }

        AtomicBoolean isAllPlayerReady = new AtomicBoolean(true);

        sessions.forEach((_userId, session) -> {
            if (!session.isPlayerReady()) {
                isAllPlayerReady.set(false);
            }
        });

        if (isAllPlayerReady.get()) {
            log.info("게임을 시작합니다.");

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

    protected void broadcast(TetrisEvent event, int userId) {
        TetrisSession session = sessions.get(userId);
        if (session == null) return;

        session.response(event, userId);
    }

    private void broadcast(TetrisEvent event) {
        sessions.forEach((userId, _room) -> {
            broadcast(event, userId);
        });
    }
}
