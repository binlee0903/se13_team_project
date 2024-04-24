package org.se13.view.tetris;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.se13.game.config.InputConfig;
import org.se13.game.tetris.DefaultTetrisGame;
import org.se13.view.base.BaseController;
import org.se13.view.difficulty.LevelSelectScreenController;

public class TetrisScreenController extends BaseController {
    @Override
    public void onCreate() {
        this.tetrisGame = DefaultTetrisGame.getInstance(gameCanvas, nextBlockCanvas, score, LevelSelectScreenController.gameLevel, LevelSelectScreenController.gameMode, false);
        this.frame.setStyle("-fx-border-color: red;");

        Scene scene = gameCanvas.getScene();

        scene.setOnKeyPressed((keyEvent -> {
            String keyName = keyEvent.getCode().getName();

            if (keyName.compareToIgnoreCase(InputConfig.PAUSE) == 0) {
                this.tetrisGame.togglePauseState();
            } else if (keyName.compareToIgnoreCase(InputConfig.EXIT) == 0) {
                System.exit(0);
            }
        }));
    }

    @Override
    public void onStart() {
        this.tetrisGame.startGame();
    }

    @FXML
    private Canvas nextBlockCanvas;
    @FXML
    private Label score;
    @FXML
    private BorderPane frame;
    @FXML
    private Canvas gameCanvas;

    private DefaultTetrisGame tetrisGame;
}
