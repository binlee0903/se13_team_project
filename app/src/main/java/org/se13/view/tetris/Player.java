package org.se13.view.tetris;

import org.se13.online.ClientActionRepository;
import org.se13.online.ClientEventRepository;
import org.se13.server.*;
import org.se13.sqlite.config.PlayerKeycode;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Player {
    public Player(int userId, PlayerKeycode playerKeycode) {
        this.userId = userId;
        this.playerKeycode = playerKeycode;
        this.eventRepository = new TetrisEventRepositoryImpl();
        this.client = new TetrisClient(userId, eventRepository);
    }

    public Player(int userId, PlayerKeycode playerKeycode, Socket socket) throws IOException {
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
        this.userId = userId;
        this.socket = socket;
        this.service = Executors.newVirtualThreadPerTaskExecutor();
        this.playerKeycode = playerKeycode;
        this.eventRepository = new ClientEventRepository(oos, ois);
        this.client = new TetrisClient(userId, eventRepository);
    }

    public void connectToServer(TetrisServer server) {
        this.server = server;
        this.actionHandler = server.connect(this.client);
        this.actionRepository = new TetrisActionRepositoryImpl(this.userId, this.actionHandler);
    }

    public void connectToOnlineServer(TetrisServer server) throws IOException {
        this.server = server;
        this.actionHandler = server.connect(this.client);
        this.actionRepository = new ClientActionRepository(this.userId, this.socket, this.service, this.oos, this.ois);
    }

    public void connectOpponent(TetrisServer server) {
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
    private Socket socket;
    private ExecutorService service;
    private TetrisActionHandler actionHandler;
    private PlayerKeycode playerKeycode;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
}
