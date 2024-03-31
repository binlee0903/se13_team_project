package org.se13.view.tetris;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.se13.SE13Application;
import org.se13.view.base.BaseController;
import org.se13.view.nav.Screen;

public class TetrisScreenController extends BaseController {

    @FXML
    private Button startButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button scoreButton;
    @FXML
    private Button quitButton;

    @FXML
    private void handleScoreButtonAction() {
        SE13Application.navController.navigate(Screen.RANKING);
    }

    @FXML
    private void handleBackButtonAction() {
        SE13Application.navController.popBackStack();
    }
}
