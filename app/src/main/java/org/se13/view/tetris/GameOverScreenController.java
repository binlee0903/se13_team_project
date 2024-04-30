package org.se13.view.tetris;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.se13.SE13Application;
import org.se13.sqlite.ranking.RankingRepositoryImpl;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.se13.view.ranking.RankingScreenController;

public class GameOverScreenController extends BaseController {
    @Override
    public void onStart() {
        super.onStart();
        score.setText(String.valueOf(endData.score()));
        rankingRepository = new RankingRepositoryImpl();
        rankingRepository.createNewTableRanking();
    }

    public void handleRankingButtonAction() {
        SE13Application.navController.navigate(AppScreen.RANKING, (RankingScreenController controller) -> {
            controller.setArguments(endData.score(), endData.isItemMode(), endData.difficulty());
        });
    }

    public void setArguments(TetrisGameEndData endData) {
        this.endData = endData;
    }

    private RankingRepositoryImpl rankingRepository;

    @FXML
    private Text score;
    @FXML
    public Button rankingButton;

    private TetrisGameEndData endData;
}
