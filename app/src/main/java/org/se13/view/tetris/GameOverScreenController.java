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

        if (endData.userID() == -1) {
            score.setText(String.valueOf(endData.score()));
            rankingRepository = new RankingRepositoryImpl();
            rankingRepository.createNewTableRanking();
            winnerPrompt.setVisible(false);
            winnerUser.setVisible(false);
            homeButton.setVisible(false);
        } else {
            score.setVisible(false);
            scorePrompt.setVisible(false);
            rankingButton.setVisible(false);
            score.setText(String.valueOf(endData.score()));

            if (endData.isGameOvered() == false) {
                if (endData.userID() == 2) {
                    winnerUser.setText("player2");
                } else {
                    winnerUser.setText("player1");
                }
            } else {
                if (endData.userID() == 1) {
                    winnerUser.setText("player2");
                } else {
                    winnerUser.setText("player1");
                }
            }
        }
    }

    public void handleRankingButtonAction() {
        SE13Application.navController.navigate(AppScreen.RANKING, (RankingScreenController controller) -> {
            controller.setArguments(endData.score(), endData.isItemMode(), endData.difficulty());
        });
    }

    public void handleHomeButtonAction() {
        SE13Application.navController.navigate(AppScreen.START);
    }

    public void setArguments(TetrisGameEndData endData) {
        this.endData = endData;
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

    private TetrisGameEndData endData;
}
