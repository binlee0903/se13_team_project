package org.se13.view.setting;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.se13.SE13Application;
import org.se13.sqlite.config.ConfigRepository;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.sqlite.ranking.RankingRepositoryImpl;
import org.se13.view.base.BaseController;

import java.util.HashMap;
import java.util.Map;

public class SettingScreenController extends BaseController {
    public boolean isTestMode = false;
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
    @FXML
    public ChoiceBox<String> playerChoiceBox;

    private Map<String, String> keySettings;
    private ConfigRepository[] configRepository;

    private final int PLAYER1 = 0;
    private final int PLAYER2 = 1;

    public void testInit() {
        isTestMode = true;

        screenSizeChoiceBox = new ChoiceBox<>();
        screenColorBlindChoiceBox = new ChoiceBox<>();
        playerChoiceBox = new ChoiceBox<>();
        moveLeftButton = new Button("keyLeft:");
        moveRightButton = new Button("keyRight:");
        moveDownButton = new Button("keyDown:");
        exitButton = new Button("Exit");
        rotateButton = new Button("Rotate");
        pauseButton = new Button("Pause");
        moveDropButton = new Button("Drop");

        onCreate();
    }

    @Override
    public void onCreate() {
        this.configRepository = new ConfigRepositoryImpl[2];
        this.configRepository[0] = new ConfigRepositoryImpl(PLAYER1);
        this.configRepository[1] = new ConfigRepositoryImpl(PLAYER2);

        resetSettingButtons();

        playerChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> setSettingValueToScreen());
    }

    public void resetSettingButtons() {
        keySettings = new HashMap<>();

        playerChoiceBox.setItems(FXCollections.observableArrayList("player1", "player2"));
        playerChoiceBox.setValue("player1");

        setSettingValueToScreen();
    }

    @FXML
    public void handleBackButtonAction() {
        // Turn into last scene
        SE13Application.navController.popBackStack();
    }

    @FXML
    public void keySaveButtonAction(ActionEvent event) {
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
    public void handleSaveButtonAction() {
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

        int selectedPlayerID = resolvePlayer();

        configRepository[selectedPlayerID].updateConfig(selectedColorMode, selectedWidth,
                selectedHeight, selectedMoveLeft, selectedMoveRight,
                selectedMoveDown, selectedRotate,
                selectedPause, selectedDrop, selectedExit);

        int[] size = {selectedWidth, selectedHeight};
        SE13Application.navController.setScreenSize(size);
    }

    public void handleSettingClearButtonAction() {
        int selectedPlayerID = resolvePlayer();
        configRepository[selectedPlayerID].clearConfig();
        configRepository[selectedPlayerID].insertDefaultConfig();

        if (isTestMode == false) {
            resetSettingButtons();
            SE13Application.navController.setScreenSize(configRepository[selectedPlayerID].getScreenSize());
        }
    }

    public void handleRankingClearButtonAction() {
        RankingRepositoryImpl rankingRepository = new RankingRepositoryImpl();
        rankingRepository.clearRanking();
        rankingRepository.createNewTableRanking();
    }

    private void setSettingValueToScreen() {
        int selectedPlayerID = resolvePlayer();

        Map<String, Object> configs = configRepository[selectedPlayerID].getConfig();

        // Add options in ChoiceBox for the choice among scene size
        int screenWidth = (Integer) configs.get("screenWidth");
        int screenHeight = (Integer) configs.get("screenHeight");
        String selectedScreenSize = screenWidth + "x" + screenHeight;

        screenSizeChoiceBox.setItems(FXCollections.observableArrayList("450x600", "600x800", "1920x1080"));
        screenSizeChoiceBox.setValue(selectedScreenSize);
        // Add options in ChoiceBox for the choice among color mode
        String colorMode = (String) configs.get("mode");
        screenColorBlindChoiceBox.setItems(FXCollections.observableArrayList("default", "Red-green", "Blue-yellow"));
        screenColorBlindChoiceBox.setValue(colorMode);

        String keyMoveLeft = (String) configs.get("keyLeft");
        keySettings.put("keyLeft", keyMoveLeft);
        moveLeftButton.setText("keyLeft: " + keyMoveLeft);

        String keyMoveRight = (String) configs.get("keyRight");
        keySettings.put("keyRight", keyMoveRight);
        moveRightButton.setText("keyRight: " + keyMoveRight);

        String keyMoveDown = (String) configs.get("keyDown");
        keySettings.put("keyDown", keyMoveDown);
        moveDownButton.setText("keyDown: " + keyMoveDown);

        String keyExit = (String) configs.get("keyExit");
        keySettings.put("keyExit", keyExit);
        exitButton.setText("keyExit: " + keyExit);

        String keyDrop = (String) configs.get("keyDrop");
        keySettings.put("keyDrop", keyDrop);
        moveDropButton.setText("keyDrop: " + keyDrop);

        String keyMoveRotate = (String) configs.get("keyRotate");
        keySettings.put("keyRotate", keyMoveRotate);
        rotateButton.setText("keyRotate: " + keyMoveRotate);

        String keyPause = (String) configs.get("keyPause");
        keySettings.put("keyPause", keyPause);
        pauseButton.setText("keyPause: " + keyPause);
    }

    public Map<String, String> getKeySettings() {
        return this.keySettings;
    }

    private int resolvePlayer() {
        return switch (playerChoiceBox.getValue()) {
            case "player1" -> PLAYER1;
            case "player2" -> PLAYER2;
            default -> -1;
        };
    }
}
