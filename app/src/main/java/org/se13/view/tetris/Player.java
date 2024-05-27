package org.se13.view.tetris;

import org.se13.server.*;
import org.se13.sqlite.config.PlayerKeycode;

public class Player {
    public Player(int userId, PlayerKeycode playerKeycode, TetrisEventRepository repository) {
        this.userId = userId;
        this.playerKeycode = playerKeycode;
        this.eventRepository = repository;
    }

    public Player(int userId) {
        this.userId = userId;
        this.playerKeycode = new PlayerKeycode("", "", "", "", "", "", "");
        this.eventRepository = new TetrisEventRepositoryImpl();
        this.client = new TetrisClient(userId, eventRepository);
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
