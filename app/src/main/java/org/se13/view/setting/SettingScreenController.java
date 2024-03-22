package org.se13.view.setting;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import org.se13.SE13Application;

public class SettingScreenController {

    @FXML
    private ChoiceBox<String> screenSizeChoiceBox;
    @FXML
    private ToggleButton colorBlindModeToggle;

    @FXML
    private void initialize() {
        // Add options in ChoiceBox for the choice among scene size
        // By selected scene size, the function will implement logic.

        // implement color blind mode
    }

    @FXML
    private void handleBackButtonAction() {
        // Turn into last scene
        SE13Application.navController.popBackStack();
    }

    @FXML
    private void handleSaveButtonAction() {
        // Saving personal settings
    }

    @FXML
    private void handleColorBlindModeToggleAction() {
        // implement personal color blind mode settings
    }
}
