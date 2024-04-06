package org.se13.view.setting;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.se13.SE13Application;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.view.base.BaseController;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class SettingScreenController extends BaseController {

    public Button moveLeftButton;
    public Button moveRightButton;
    public Button moveDownButton;
    public Button exitButton;
    public Button rotateButton;
    public Button pauseButton;
    public Button moveDropButton;
    @FXML
    private ChoiceBox<String> screenSizeChoiceBox;
    @FXML
    private ChoiceBox<String> screenColorBlindChoiceBox;
    private String selectedScreenSize;

    @FXML
    private void initialize() {
        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();
        Map<String, Object> configs = configRepository.getConfig(0);

        // Add options in ChoiceBox for the choice among scene size
        int screenWidth = (Integer) configs.get("screenWidth");
        int screenHeight = (Integer) configs.get("screenHeight");
        selectedScreenSize = screenWidth + "x" + screenHeight;
        screenSizeChoiceBox.setItems(FXCollections.observableArrayList("300x400", "600x800", "1920x1080"));
        screenSizeChoiceBox.setValue(selectedScreenSize);
        // Add options in ChoiceBox for the choice among color mode
        String colorMode = (String) configs.get("mode");
        screenColorBlindChoiceBox.setItems(FXCollections.observableArrayList("default", "Red-green", "Blue-yellow"));
        screenColorBlindChoiceBox.setValue(colorMode);
        // Add options in buttons for the choice in the keyboard
        String keyMoveLeft = String.valueOf(configs.get("keyLeft"));
        moveLeftButton.setText("Left: " + asciiCasting(keyMoveLeft));

        String keyMoveRight = String.valueOf(configs.get("keyRight"));
        moveRightButton.setText("Right: " + asciiCasting(keyMoveRight));

        String keyMoveDown = String.valueOf(configs.get("keyDown"));
        moveDownButton.setText("Down: " + asciiCasting(keyMoveDown));

        String keyExit = String.valueOf(configs.get("keyExit"));
        exitButton.setText("Exit: " + asciiCasting(keyExit));

        String keyDrop = String.valueOf(configs.get("keyDrop"));
        moveDropButton.setText("Drop: " + asciiCasting(keyDrop));

        String keyMoveRotate = String.valueOf(configs.get("keyRotateRight"));
        rotateButton.setText("Rotate: " + asciiCasting(keyMoveRotate));

        String keyPause = String.valueOf(configs.get("keyPause"));
        pauseButton.setText("Pause: " + asciiCasting(keyPause));
        // By selected scene size, the function will implement logic.

    }

    private String asciiCasting(String asciiCodeCasting) {
        int asciiValue = Integer.parseInt(asciiCodeCasting);
        char asciiChar = (char) asciiValue;
        return String.valueOf(asciiChar);
    }

    @FXML
    private void handleBackButtonAction() {
        // Turn into last scene
        SE13Application.navController.popBackStack();
        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();
        configRepository.insertDefaultConfig(0);
    }

    @FXML
    private void keySaveButtonAction(ActionEvent event) {
        Button buttonToConfigure = (Button) event.getSource(); // 이벤트가 발생한 버튼을 가져옵니다.
        // 사용자에게 키 입력을 요청하는 메시지 표시
        buttonToConfigure.setText("Press a key for...");
        // 키 이벤트 처리를 위한 리스너 정의
        EventHandler<KeyEvent> keyEventHandler = new EventHandler<>() {
            @Override
            public void handle(KeyEvent e) {
                KeyCode keyCode = e.getCode(); // 키보드의 키 코드를 가져옵니다.
                if (keyCode != null) {
                    // 키 코드의 이름을 사용하여 설정 메시지 표시
                    String keyName = keyCode.getName(); // 사용자에게 익숙한 키 이름 가져오기
                    keyName = keyName.toLowerCase();
                    buttonToConfigure.setText(keyName);

                    // 설정 완료 후 이벤트 리스너 제거
                    buttonToConfigure.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, this);

                    // 다른 이벤트 처리 방지
                    e.consume();
                }
            }
        };
        // 키 이벤트 핸들러 추가
        buttonToConfigure.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
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
        // button setting
        int selectedMoveLeft = Integer.parseInt(moveLeftButton.getText());

        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();

    }
}
