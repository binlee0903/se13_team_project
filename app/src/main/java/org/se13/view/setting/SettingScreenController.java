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
import org.se13.sqlite.ranking.RankingRepositoryImpl;
import org.se13.view.base.BaseController;

import java.util.HashMap;
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

    private Map<String, String> keySettings;

    @FXML
    private void initialize() {
        keySettings = new HashMap<>();
        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();
        Map<String, Object> configs = configRepository.getConfig(0);

        // Add options in ChoiceBox for the choice among scene size
        int screenWidth = (Integer) configs.get("screenWidth");
        int screenHeight = (Integer) configs.get("screenHeight");
        String selectedScreenSize = screenWidth + "x" + screenHeight;
        screenSizeChoiceBox.setItems(FXCollections.observableArrayList("300x400", "600x800", "1920x1080"));
        screenSizeChoiceBox.setValue(selectedScreenSize);
        // Add options in ChoiceBox for the choice among color mode
        String colorMode = (String) configs.get("mode");
        screenColorBlindChoiceBox.setItems(FXCollections.observableArrayList("default", "Red-green", "Blue-yellow"));
        screenColorBlindChoiceBox.setValue(colorMode);
        // Add options in buttons for the choice in the keyboard
        String keyMoveLeft = (String)configs.get("keyLeft");
        keySettings.put("keyLeft", keyMoveLeft);
        System.out.println(keyMoveLeft);
        moveLeftButton.setText("keyLeft: " + keyMoveLeft);

        String keyMoveRight = (String)configs.get("keyRight");
        keySettings.put("keyRight", keyMoveRight);
        moveRightButton.setText("keyRight: " + keyMoveRight);

        String keyMoveDown = (String)configs.get("keyDown");
        keySettings.put("keyDown", keyMoveDown);
        moveDownButton.setText("keyDown: " + keyMoveDown);

        String keyExit = (String)configs.get("keyExit");
        keySettings.put("keyExit", keyExit);
        exitButton.setText("keyExit: " + keyExit);

        String keyDrop = (String)configs.get("keyDrop");
        keySettings.put("keyDrop", keyDrop);
        moveDropButton.setText("keyDrop: " + keyDrop);

        String keyMoveRotate = (String)configs.get("keyRotate");
        keySettings.put("keyRotate", keyMoveRotate);
        rotateButton.setText("keyRotate: " + keyMoveRotate);

        String keyPause = (String)configs.get("keyPause");
        keySettings.put("keyPause", keyPause);
        pauseButton.setText("keyPause: " + keyPause);
        // By selected scene size, the function will implement logic.

    }

    public String asciiToString(String asciiCode) {
        try {
            // 문자열을 정수로 변환
            int code = Integer.parseInt(asciiCode);

            // 정수 값을 문자로 캐스팅
            char character = (char) code;

            // 문자를 문자열로 변환하여 반환
            return String.valueOf(character);
        } catch (NumberFormatException e) {
            // 입력 문자열이 유효한 정수가 아닌 경우 오류 처리
            System.err.println("Invalid ASCII code: " + asciiCode);
            return null;
        }
    }

    public int getStringFromButtonText(Button button) {
        // 버튼의 텍스트를 가져옵니다.
        String text = button.getText();

        // 텍스트가 비어있지 않은 경우 첫 번째 문자의 아스키 코드를 반환합니다.
        if (!text.isEmpty()) {
            return text.charAt(text.length()-1); // 첫 번째 문자의 아스키 코드 값을 반환
        }

        // 텍스트가 비어있는 경우, 오류 코드나 기본 값으로 처리할 수 있습니다.
        // 예를 들어, 여기서는 -1을 반환합니다.
        return -1;
    }


    @FXML
    private void handleBackButtonAction() {
        // Turn into last scene
        SE13Application.navController.popBackStack();
    }

    @FXML
    private void keySaveButtonAction(ActionEvent event) {
        Button buttonToConfigure = (Button) event.getSource(); // 이벤트가 발생한 버튼을 가져옵니다.
        String indicator = buttonToConfigure.getText().split(":")[0];

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
                    buttonToConfigure.setText(indicator + ": " + keyName);
                    keySettings.put(indicator, keyName);


                    // 설정 완료 후 이벤트 리스너 제거
                    buttonToConfigure.removeEventHandler(KeyEvent.KEY_PRESSED, this);

                    // 다른 이벤트 처리 방지
                    e.consume();
                }
            }
        };
        // 키 이벤트 핸들러 추가
        buttonToConfigure.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        // Saving personal settings
        // screen size setting
        String selectedSize = screenSizeChoiceBox.getValue();
        String[] dimensions = selectedSize.split("x");
        int selectedWidth = Integer.parseInt(dimensions[0]);
        int selectedHeight = Integer.parseInt(dimensions[1]);
        // box color setting
        String selectedColorMode = screenColorBlindChoiceBox.getValue();
        // button setting
        String indicator = moveLeftButton.getText().split(":")[0];
        String selectedMoveLeft = keySettings.get(indicator);

        indicator = moveRightButton.getText().split(":")[0];
        String selectedMoveRight = keySettings.get(indicator);

        indicator = moveDownButton.getText().split(":")[0];
        String selectedMoveDown = keySettings.get(indicator);

        indicator = rotateButton.getText().split(":")[0];
        String selectedRotate = keySettings.get(indicator);

        indicator = pauseButton.getText().split(":")[0];
        String selectedPause = keySettings.get(indicator);

        indicator = moveDropButton.getText().split(":")[0];
        String selectedDrop = keySettings.get(indicator);

        indicator = exitButton.getText().split(":")[0];
        String selectedExit = keySettings.get(indicator);

        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();
        configRepository.updateConfig(0, selectedColorMode, selectedWidth,
                selectedHeight, selectedMoveLeft, selectedMoveRight,
                selectedMoveDown, "", selectedRotate,
                selectedPause, selectedDrop, selectedExit);

        int[] size = { selectedWidth, selectedHeight };
        SE13Application.navController.setScreenSize(size);
    }

    public void handleSettingClearButtonAction() {
        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();
        configRepository.clearConfig(0);
        configRepository.insertDefaultConfig(0);
    }

    public void handleRankingClearButtonAction() {
        RankingRepositoryImpl rankingRepository = new RankingRepositoryImpl();
        rankingRepository.clearRanking();
        rankingRepository.createNewTableRanking();
    }
}
