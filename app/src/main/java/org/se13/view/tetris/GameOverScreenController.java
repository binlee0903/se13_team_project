package org.se13.view.tetris;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.se13.SE13Application;
import org.se13.game.tetris.DefaultTetrisGame;
import org.se13.sqlite.ranking.RankingRepositoryImpl;
import org.se13.view.base.BaseController;
import org.se13.view.nav.Screen;

public class GameOverScreenController extends BaseController {
    @Override
    public void onStart() {
        super.onStart();

        defaultTetrisGame = DefaultTetrisGame.getInstance(null, null, null);
        score.setText(String.valueOf(defaultTetrisGame.getScore()));
        rankingRepository = new RankingRepositoryImpl();
        rankingRepository.createNewTableRanking();
    }

    public void onEnter() {
        rankingRepository.insertRanking(name.getText(), defaultTetrisGame.getScore(), defaultTetrisGame.isItemMode(), defaultTetrisGame.getDifficulty());
        defaultTetrisGame.resetGame();
        SE13Application.navController.navigate(Screen.RANKING);
    }

    private DefaultTetrisGame defaultTetrisGame;
    private RankingRepositoryImpl rankingRepository;

    @FXML
    private Text score;

    @FXML
    private TextField name;
}
