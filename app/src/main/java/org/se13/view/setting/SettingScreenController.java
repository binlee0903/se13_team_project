package org.se13.view.setting;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import org.se13.SE13Application;
import org.se13.StackNavGraph;
import org.se13.view.nav.Screen;

import java.io.IOException;

public class SettingScreenController {

    @FXML
    private ChoiceBox<String> screenSizeChoiceBox;
    @FXML
    private ChoiceBox<String> screenColorBlindChoiceBox;
    private String selectedScreenSize;
    private StackNavGraph stackNavGraph;
    @FXML
    private void initialize() {
        // Add options in ChoiceBox for the choice among scene size
        screenSizeChoiceBox.setItems(FXCollections.observableArrayList("300x400", "600x800", "1920x1080"));
        screenSizeChoiceBox.setValue("300x400");
        // Add options in ChoiceBox for the choice among color mode
        screenColorBlindChoiceBox.setItems(FXCollections.observableArrayList("NO", "Red-green", "Blue-yellow"));
        screenColorBlindChoiceBox.setValue("NO");
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
        String selectedSize = screenSizeChoiceBox .getValue();
        String[] dimensions = selectedSize.split("x");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);

        screenSizeChoiceBox.setValue(selectedSize);

        // 설정 화면을 다시 로드하고 표시
        SE13Application.navController.navigate(Screen.SETTING);
    }

    @FXML
    private void handleColorBlindModeToggleAction() {
        // implement personal color blind mode settings
    }

    public String getSelectedScreenSize() {
        return selectedScreenSize;
    }

    public void setSelectedScreenSize(String selectedScreenSize) {
        this.selectedScreenSize = selectedScreenSize;
    }

    public void setStackNavGraph(StackNavGraph stackNavGraph) {
        this.stackNavGraph = stackNavGraph;
    }

    public StackNavGraph getStackNavGraph() {
        return stackNavGraph;
    }
}
