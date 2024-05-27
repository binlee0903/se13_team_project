package org.se13.server;

import org.se13.view.tetris.TetrisGameEndData;

import java.util.List;

public interface TetrisServer {

    TetrisActionHandler connect(TetrisClient client);

    void disconnect(TetrisClient client);

    void responseGameOver(int score, boolean isItemMode, String difficulty);
}
