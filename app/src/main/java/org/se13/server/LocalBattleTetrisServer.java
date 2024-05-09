package org.se13.server;

import org.se13.game.event.AttackTetrisBlocks;
import org.se13.game.event.ServerErrorEvent;
import org.se13.game.event.TetrisEvent;
import org.se13.game.event.UpdateTetrisState;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.tetris.DefaultTetrisGame;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalBattleTetrisServer implements TetrisServer {

    private final int maxUser = 2;

    private GameLevel level;
    private GameMode mode;

    private Map<Integer, TetrisSession> sessions;
    private Map<Integer, TetrisEventHandler> handlers;

    public LocalBattleTetrisServer(GameLevel level, GameMode mode) {
        this.level = level;
        this.mode = mode;
        this.sessions = new HashMap<>();
    }

    @Override
    public void responseGameOver(int score, boolean isItemMode, String difficulty) {

    }

    @Override
    public TetrisActionHandler connect(TetrisClient client) {
        sessions.put(client.getUserId(), new TetrisSession(client, new DefaultTetrisGame(level, mode, this)));
        handlers.put(client.getUserId(), createHandlers());

        return packet -> {
            switch (packet.action()) {
                case START -> handleStartGame(packet.userId());
            }
        };
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
        }
    }

    private TetrisEventHandler createHandlers() {
        return (userId, event) -> {
            switch (event) {
                case UpdateTetrisState state -> broadcast(state);
                case AttackTetrisBlocks blocks -> handleAttacks(userId, blocks);
                default -> {
                }
            }
        };
    }

    private void handleAttacks(int userId, AttackTetrisBlocks blocks) {
        sessions.forEach((playerId, session) -> {
            if (playerId != userId) {
                session.attack(blocks);
            }
        });
    }

    private void broadcast(TetrisEvent event, int userId) {
        TetrisSession session = sessions.get(userId);
        if (session == null) return;

        session.response(event);
    }

    private void broadcast(TetrisEvent event) {
        sessions.forEach((userId, _room) -> {
            broadcast(event, userId);
        });
    }
}
