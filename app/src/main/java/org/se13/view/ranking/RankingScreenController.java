package org.se13.view.ranking;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.se13.SE13Application;

public class RankingScreenController {

    @FXML
    private Button startButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button scoreButton;
    @FXML
    private Button quitButton;

    @FXML
    private void handleQuitButtonAction() {
        SE13Application.navController.popBackStack();
    }
}
