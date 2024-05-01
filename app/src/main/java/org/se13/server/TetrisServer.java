package org.se13.server;

public interface TetrisServer {

    TetrisActionHandler connect(TetrisClient client);

    void disconnect(TetrisClient client);

    void responseGameOver(int score, boolean isItemMode, String difficulty);
}
