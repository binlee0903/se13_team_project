package org.se13.view.setting;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import org.se13.SE13Application;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.view.base.BaseController;

public class SettingScreenController extends BaseController {

    public Button moveLeftButton;
    public Button moveRightButton;
    public Button moveDownButton;
    public Button rotateLeftButton;
    public Button rotateRightButton;
    public Button pauseButton;
    @FXML
    private ChoiceBox<String> screenSizeChoiceBox;
    @FXML
    private ChoiceBox<String> screenColorBlindChoiceBox;
    private String selectedScreenSize;
    @FXML
    private void initialize() {
        // Add options in ChoiceBox for the choice among scene size
        screenSizeChoiceBox.setItems(FXCollections.observableArrayList("300x400", "600x800", "1920x1080"));
        screenSizeChoiceBox.setValue("300x400");
        // Add options in ChoiceBox for the choice among color mode
        screenColorBlindChoiceBox.setItems(FXCollections.observableArrayList("Nothing", "Red-green", "Blue-yellow"));
        screenColorBlindChoiceBox.setValue("Nothing");
        // Add options in buttons for the choice in the keyboard
        moveLeftButton.setText("Left: A");
        moveRightButton.setText("Right: D");

        // By selected scene size, the function will implement logic.

    }

    @FXML
    private void handleBackButtonAction() {
        // Turn into last scene
        SE13Application.navController.popBackStack();
    }

    @FXML
    private void handleSaveButtonAction() {
        // Saving personal settings
        // screen size setting
        String selectedSize = screenSizeChoiceBox.getValue();
        String[] dimensions = selectedSize.split("x");
        int selectedWidth = Integer.parseInt(dimensions[0]);
        int selectedHeight = Integer.parseInt(dimensions[1]);
        // box color setting
        String selectedColorMode = screenColorBlindChoiceBox.getValue();

        ConfigRepositoryImpl configRepository = new ConfigRepositoryImpl();

    }

    public void keySaveButtonAction() {
        // 버튼을 한 번 누르면 id를 가져와서, 어떤 키를 설정할지 결정함
        // 설정하고 싶은 키를 한 번 누르면 그 키로 설정함
        // 버튼을 한 번 더 누르면 설정이 완료됨
    }
}
