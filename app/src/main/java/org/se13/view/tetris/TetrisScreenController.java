package org.se13.view.tetris;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.se13.game.config.InputConfig;
import org.se13.game.tetris.DefaultTetrisGame;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.view.base.BaseController;
import org.se13.view.difficulty.LevelSelectScreenController;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class TetrisScreenController extends BaseController {
    @Override
    public void onCreate() {
        this.tetrisGame = DefaultTetrisGame.getInstance(gameCanvas, nextBlockCanvas, score, LevelSelectScreenController.gameLevel, LevelSelectScreenController.gameMode, false);
        this.frame.setStyle("-fx-border-color: red;");

        Scene scene = gameCanvas.getScene();

        scene.setOnKeyPressed((keyEvent -> {
            if (keyEvent.getText().isEmpty() == false) {
                if (keyEvent.getText().charAt(0) == InputConfig.PAUSE) {
                    this.tetrisGame.togglePauseState();
                } else if (keyEvent.getText().charAt(0) == InputConfig.EXIT) {
                    System.exit(0);
                }
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

    private ConfigRepositoryImpl configRepository;
}
