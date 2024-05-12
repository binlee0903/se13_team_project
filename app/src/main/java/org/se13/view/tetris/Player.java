package org.se13.view.tetris;

import org.se13.game.event.TetrisEvent;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.server.LocalTetrisServer;
import org.se13.server.TetrisClient;
import org.se13.utils.Subscriber;
import org.se13.server.TetrisActionHandler;

public class Player {

    public Player(int userId, GameLevel gameLevel, GameMode gameMode) {
        this.userId = userId;
        this.gameLevel = gameLevel;
        this.gameMode = gameMode;
        this.eventRepository = new TetrisEventRepositoryImpl();
        this.client = new TetrisClient(userId, eventRepository);
    }
    public void connectToServer(){
        this.server = new LocalTetrisServer(this.gameLevel, this.gameMode);
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
    private LocalTetrisServer server;
    private TetrisActionHandler actionHandler;
}
