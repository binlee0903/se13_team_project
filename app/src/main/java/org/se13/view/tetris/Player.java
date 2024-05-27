package org.se13.view.tetris;

import org.se13.server.TetrisActionHandler;
import org.se13.server.TetrisClient;
import org.se13.server.TetrisServer;
import org.se13.sqlite.config.PlayerKeycode;

public class Player {
    public Player(int userId, PlayerKeycode playerKeycode) {
        this.userId = userId;
        this.playerKeycode = playerKeycode;
        this.eventRepository = new TetrisEventRepositoryImpl();
        this.client = new TetrisClient(userId, eventRepository);
    }

    public Player(int userId, PlayerKeycode playerKeycode, TetrisActionRepository actionRepository, TetrisEventRepository eventRepository) {
        this.userId = userId;
        this.playerKeycode = playerKeycode;
        this.actionRepository = actionRepository;
        this.eventRepository = eventRepository;
    }

    public void connectToServer(TetrisServer server) {
        this.server = server;

        this.actionHandler = server.connect(this.client);
        this.actionRepository = new TetrisActionRepositoryImpl(this.userId, this.actionHandler);
    }

    public PlayerKeycode getPlayerKeycode() {
        return playerKeycode;
    }

    public TetrisEventRepository getEventRepository() {
        return eventRepository;
    }

    public TetrisActionRepository getActionRepository() {
        return actionRepository;
    }

    private int userId;
    private TetrisEventRepository eventRepository;
    private TetrisActionRepository actionRepository;
    private TetrisClient client;
    private TetrisServer server;
    private TetrisActionHandler actionHandler;
    private PlayerKeycode playerKeycode;
}
