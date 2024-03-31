package org.se13.view.tetris;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.se13.SE13Application;
import org.se13.game.tetris.DefaultTetrisGame;
import org.se13.view.base.BaseController;
import org.se13.view.nav.Screen;

public class TetrisScreenController extends BaseController {
    @Override
    public void onCreate() {
        this.tetrisGame = new DefaultTetrisGame(gameCanvas, nextBlockCanvas, score);
        this.frame.setStyle("-fx-border-color: red;");
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
