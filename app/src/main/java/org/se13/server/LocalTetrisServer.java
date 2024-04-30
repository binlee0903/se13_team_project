package org.se13.server;

import javafx.animation.AnimationTimer;
import org.se13.game.action.TetrisAction;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.tetris.DefaultTetrisGame;

public class LocalTetrisServer implements TetrisServer {
    // TODO: JavaFX 의존성이 없는 Timer로 교체해야 합니다.
    private AnimationTimer tetrisTimer;
    private TetrisClient client;
    private DefaultTetrisGame tetrisGame;

    public LocalTetrisServer(GameLevel gameLevel, GameMode gameMode, TetrisClient client) {
        this.client = client;
        this.tetrisGame = new DefaultTetrisGame(gameLevel, gameMode, this);
        this.tetrisGame.subscribe(client::response);
    }

    @Override
    public void responseGameOver(int score, boolean isItemMode, String difficulty) {
        this.client.gameOver(score, isItemMode, difficulty);
        tetrisTimer.stop();
    }

    @Override
    public void handle(TetrisAction request) {
        switch (request) {
            case CONNECT -> startGame();
            case EXIT_GAME -> tetrisGame.stopGame();
            case TOGGLE_PAUSE_STATE -> togglePauseState();
            case IMMEDIATE_BLOCK_PLACE,
                 MOVE_BLOCK_DOWN,
                 MOVE_BLOCK_LEFT,
                 MOVE_BLOCK_RIGHT,
                 ROTATE_BLOCK_CW -> tetrisGame.requestInput(request);
        }
    }

    private void startGame() {
        tetrisGame.startGame();
        tetrisTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                tetrisGame.pulse(l);
            }
        };
        tetrisTimer.start();
    }

    private void togglePauseState() {
        tetrisGame.togglePauseState();
        if (tetrisGame.togglePauseState()) {
            tetrisTimer.start();
        } else {
            tetrisTimer.stop();
        }
    }
}
