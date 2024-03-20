package org.se13.view;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;

public class SettingsScreenController {

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
