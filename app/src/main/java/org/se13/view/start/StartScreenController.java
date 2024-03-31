package org.se13.view.start;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.se13.SE13Application;
import org.se13.view.base.BaseController;
import org.se13.view.nav.Screen;

public class StartScreenController extends BaseController {

    @FXML
    private Button startButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button scoreButton;
    @FXML
    private Button quitButton;

    @FXML
    private void handleTetrisButtonAction() {
        SE13Application.navController.navigate(Screen.TETRIS);
    }

    @FXML
    private void handleSettingsButtonAction() {
        // Turn into a setting screen
        SE13Application.navController.navigate(Screen.SETTING);
    }

    @FXML
    private void handleScoreButtonAction() {
        // Turn into a scoreboard screen
        SE13Application.navController.navigate(Screen.RANKING);
    }

    @FXML
    private void handleQuitButtonAction() {
        // Quit the app
        System.exit(0);
    }
}
