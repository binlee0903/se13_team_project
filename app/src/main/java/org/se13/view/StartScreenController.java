package org.se13.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartScreenController {

    @FXML
    private Button startButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button scoreButton;
    @FXML
    private Button quitButton;

    @FXML
    private void handleStartButtonAction() {
        // Playing Tetris logic
    }

    @FXML
    private void handleSettingsButtonAction() {
        // Turn into a setting screen
    }

    @FXML
    private void handleScoreButtonAction() {
        // Turn into a scoreboard screen
    }

    @FXML
    private void handleQuitButtonAction() {
        // Quit the app
        System.exit(0);
    }
}
