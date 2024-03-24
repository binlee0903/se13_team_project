package org.se13.view.tetris;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import org.se13.SE13Application;
import org.se13.game.tetris.DefaultTetrisGame;
import org.se13.game.tetris.ITetrisGame;
import org.se13.view.nav.Screen;

import java.net.URL;
import java.util.ResourceBundle;

public class TetrisScreenController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void startTetris() {
        this.tetrisGame = new DefaultTetrisGame(gameCanvas, score);
        this.tetrisGame.startGame();
    }

    @FXML
    private Label score;
    @FXML
    private Canvas gameCanvas;
    @FXML
    private Canvas nextBlockCanvas;
    private ITetrisGame tetrisGame;
}
