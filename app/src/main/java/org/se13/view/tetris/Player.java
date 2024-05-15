package org.se13.view.tetris;

import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.server.*;
import org.se13.sqlite.config.PlayerKeycode;

public class Player {
    public Player(int userId, GameLevel gameLevel, GameMode gameMode, PlayerKeycode playerKeycode) {
        this.userId = userId;
        this.gameLevel = gameLevel;
        this.gameMode = gameMode;
        this.playerKeycode = playerKeycode;
        this.eventRepository = new TetrisEventRepositoryImpl();
        this.client = new TetrisClient(userId, eventRepository);
    }

    public void connectToServer(TetrisServer server) {
        this.server = server;

        this.actionHandler = server.connect(this.client);
        this.actionRepository = new TetrisActionRepositoryImpl(this.userId, this.actionHandler);
    }

    public int getUserId() {
        return userId;
    }

    public GameLevel getGameLevel() {
        return gameLevel;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public PlayerKeycode getPlayerKeycode() {
        return playerKeycode;
    }

    public TetrisEventRepositoryImpl getEventRepository() {
        return eventRepository;
    }

    public TetrisActionRepositoryImpl getActionRepository() {
        return actionRepository;
    }

    private int userId;
    private GameLevel gameLevel;
    private GameMode gameMode;
    private TetrisEventRepositoryImpl eventRepository;
    private TetrisActionRepositoryImpl actionRepository;
    private TetrisClient client;
    private TetrisServer server;
    private TetrisActionHandler actionHandler;
    private PlayerKeycode playerKeycode;
}
