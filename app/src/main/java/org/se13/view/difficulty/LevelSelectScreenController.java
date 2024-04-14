package org.se13.view.difficulty;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import org.se13.SE13Application;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.view.base.BaseController;
import org.se13.view.nav.Screen;

public class LevelSelectScreenController extends BaseController {
    @FXML
    private void initialize() {
        modeChoiceBox.setItems(FXCollections.observableArrayList("default","item"));
        modeChoiceBox.setValue("default");
    }

    @FXML
    private void handleEasyButtonAction() {
        gameLevel = GameLevel.EASY;
        gameMode = setGameMode(modeChoiceBox.getValue());
        SE13Application.navController.navigate(Screen.TETRIS);
    }

    @FXML
    private void handleNormalButtonAction() {
        gameLevel = GameLevel.NORMAL;
        gameMode = setGameMode(modeChoiceBox.getValue());
        SE13Application.navController.navigate(Screen.TETRIS);
    }

    @FXML
    private void handleHardButtonAction() {
        gameLevel = GameLevel.HARD;
        gameMode = setGameMode(modeChoiceBox.getValue());
        SE13Application.navController.navigate(Screen.TETRIS);
    }

    private GameMode setGameMode(String gameMode) {
        return switch (gameMode) {
            case "default" -> GameMode.DEFAULT;
            case "item" -> GameMode.ITEM;
            default -> {
                assert (false);
                yield null;
            }
        };
    }

    public static GameMode gameMode;
    public static GameLevel gameLevel;

    @FXML
    Button easyButton;

    @FXML
    Button normalButton;

    @FXML
    Button hardButton;

    @FXML
    ChoiceBox<String> modeChoiceBox;
}
