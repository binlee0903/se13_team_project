package org.se13.server;

import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.tetris.TetrisGame;

import java.util.Map;

public class LocalBattleTetrisServer implements TetrisServer {

    private GameLevel level;
    private GameMode mode;

    private Map<Integer, TetrisRoom> sessions;

    public LocalBattleTetrisServer(GameLevel level, GameMode mode) {
        this.level = level;
        this.mode = mode;
    }

    @Override
    public void responseGameOver(int score, boolean isItemMode, String difficulty) {

    }

    @Override
    public TetrisActionHandler connect(TetrisClient client) {
        sessions.put(client.getUserId(), new TetrisRoom(client, new TetrisGame(level, mode, this)));

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

    }
}
