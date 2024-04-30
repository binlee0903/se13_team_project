package org.se13.server;

import org.se13.game.rule.GameLevel;

public interface TetrisServer extends TetrisActionHandler {

    void responseGameOver(int score, boolean isItemMode, String difficulty);
}
