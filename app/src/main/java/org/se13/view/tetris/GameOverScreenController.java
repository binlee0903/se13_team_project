package org.se13.view.tetris;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.se13.SE13Application;
import org.se13.sqlite.ranking.RankingRepositoryImpl;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.se13.view.ranking.RankingScreenController;
import org.se13.view.start.StartScreenController;

public class GameOverScreenController extends BaseController {
    @Override
    public void onStart() {
        super.onStart();

        if (endData[0].userId() == -1) {
            score.setText(String.valueOf(endData[0].score()));
            rankingRepository = new RankingRepositoryImpl();
            rankingRepository.createNewTableRanking();
            winnerPrompt.setVisible(false);
            winnerUser.setVisible(false);
            homeButton.setVisible(false);
        } else {
            score.setVisible(false);
            scorePrompt.setVisible(false);
            rankingButton.setVisible(false);

            if (endData[0].score() < endData[1].score()) {
                winnerUser.setText("player2");
            } else {
                winnerUser.setText("player1");
            }
        }
    }

    public void handleRankingButtonAction() {
        SE13Application.navController.navigate(AppScreen.RANKING, (RankingScreenController controller) -> {
            controller.setArguments(endData[0].score(), endData[0].isItemMode(), endData[0].difficulty());
        });
    }

    public void handleHomeButtonAction() {
        SE13Application.navController.navigate(AppScreen.START);
    }

    public void setArguments(TetrisGameEndData endData) {
        if (this.endData == null) {
            this.endData = new TetrisGameEndData[2];
        }

        this.endData[0] = endData;
    }

    private RankingRepositoryImpl rankingRepository;

    @FXML
    private Text score;
    @FXML
    public Button rankingButton;
    @FXML
    public Text scorePrompt;
    @FXML
    public Text winnerPrompt;
    @FXML
    public Text winnerUser;
    @FXML
    public Button homeButton;

    private TetrisGameEndData[] endData;
}
