package org.se13.view.tetris;

public interface TetrisActionRepository {

    void connect();

    void immediateBlockPlace();

    void moveBlockDown();

    void moveBlockLeft();

    void moveBlockRight();

    void rotateBlockCW();

    void togglePauseState();

    void exitGame();
}
