package org.se13.server;

import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.tetris.TetrisGame;
import org.se13.view.tetris.TetrisGameEndData;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocalTetrisServer implements TetrisServer {
    private GameLevel level;
    private GameMode mode;

    private Timer tetrisTimer;
    private TetrisClient player;
    private TetrisGame playerGame;

    public LocalTetrisServer(GameLevel gameLevel, GameMode gameMode) {
        this.level = gameLevel;
        this.mode = gameMode;
        tetrisTimer = new Timer();
    }

    @Override
    public void responseGameOver(int score, boolean isItemMode, String difficulty) {
        this.player.gameOver(score, isItemMode, playerGame.isGameOvered(), difficulty);
        tetrisTimer.cancel();
    }

    @Override
    public TetrisActionHandler connect(TetrisClient client) {
        player = client;
        playerGame = new TetrisGame(level, mode, this);
        playerGame.subscribe(player::response);

        return request -> {
            switch (request.action()) {
                case START -> startGame();
                case EXIT_GAME -> disconnect(player);
                case TOGGLE_PAUSE_STATE -> togglePauseState();
                case IMMEDIATE_BLOCK_PLACE,
                     MOVE_BLOCK_DOWN,
                     MOVE_BLOCK_LEFT,
                     MOVE_BLOCK_RIGHT,
                     ROTATE_BLOCK_CW -> playerGame.requestInput(request.action());
            }
        };
    }

    @Override
    public void disconnect(TetrisClient client) {
        playerGame.stopGame();
        player = null;
        playerGame = null;
    }

    private void startGame() {
        playerGame.startGame();
        schedule();
    }

    private void togglePauseState() {
        if (playerGame.togglePauseState()) {
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
                playerGame.pulse(System.nanoTime());
            }
        }, 0, 16);
    }

    void testPulse() {
        playerGame.pulse(System.nanoTime());
    }
}
