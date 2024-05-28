package org.se13.server;

import org.se13.game.event.TetrisEvent;
import org.se13.view.tetris.TetrisGameEndData;
import org.se13.view.tetris.TetrisEventRepository;

public class TetrisClient {

    private int userId;
    private TetrisEventRepository repository;

    public TetrisClient(int userId, TetrisEventRepository repository) {
        this.userId = userId;
        this.repository = repository;
    }

    public void response(TetrisEvent state) {
        repository.response(state);
    }

    public void response(TetrisEvent state, int userId) {
        repository.response(state, userId);
    }

    public void gameOver(int score, boolean isItemMode, boolean isGameOvered, String difficulty) {
        repository.gameOver(new TetrisGameEndData(userId, score, isItemMode, isGameOvered, difficulty));
    }

    public int getUserId() {
        return userId;
    }
}
