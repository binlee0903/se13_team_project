package org.se13.server;

import org.se13.game.action.TetrisAction;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.tetris.DefaultTetrisGame;

import java.util.Timer;
import java.util.TimerTask;

public class LocalTetrisServer implements TetrisServer {
    private Timer tetrisTimer;
    private TetrisClient client;
    private DefaultTetrisGame tetrisGame;

    public LocalTetrisServer(GameLevel gameLevel, GameMode gameMode, TetrisClient client) {
        this.client = client;
        this.tetrisGame = new DefaultTetrisGame(gameLevel, gameMode, this);
        this.tetrisGame.subscribe(client::response);
        tetrisTimer = new Timer();
    }

    @Override
    public void responseGameOver(int score, boolean isItemMode, String difficulty) {
        this.client.gameOver(score, isItemMode, difficulty);
        tetrisTimer.cancel();
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
        schedule();
    }

    private void togglePauseState() {
        if (tetrisGame.togglePauseState()) {
            schedule();
        } else {
            tetrisTimer.cancel();
        }
    }

    private void schedule() {
        tetrisTimer = new Timer();
        tetrisTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                tetrisGame.pulse(System.nanoTime());
            }
        }, 0, 16);
    }
}
