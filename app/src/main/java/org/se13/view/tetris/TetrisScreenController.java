package org.se13.view.tetris;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.se13.game.tetris.DefaultTetrisGame;
import org.se13.view.base.BaseController;

public class TetrisScreenController extends BaseController {
    @Override
    public void onCreate() {
        this.tetrisGame = DefaultTetrisGame.getInstance(gameCanvas, nextBlockCanvas, score, false);
        this.frame.setStyle("-fx-border-color: red;");

        Scene scene = gameCanvas.getScene();

        scene.setOnKeyPressed((keyEvent -> {
            if (keyEvent.getText().isEmpty() != true) {
                switch (keyEvent.getText()) {
                    case "p":
                        this.tetrisGame.togglePauseState();
                        break;

                    case "q":
                        System.exit(0);
                        break;

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
}
