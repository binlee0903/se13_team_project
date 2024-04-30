package org.se13.view.difficulty;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import org.se13.SE13Application;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.server.LocalTetrisServer;
import org.se13.server.TetrisClient;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.se13.view.tetris.*;

public class LevelSelectScreenController extends BaseController {
    @FXML
    private void initialize() {
        modeChoiceBox.setItems(FXCollections.observableArrayList("default","item"));
        modeChoiceBox.setValue("default");
    }

    @FXML
    private void handleEasyButtonAction() {
        startLocalTetrisGame(GameLevel.EASY, setGameMode(modeChoiceBox.getValue()));
    }

    @FXML
    private void handleNormalButtonAction() {
        startLocalTetrisGame(GameLevel.NORMAL, setGameMode(modeChoiceBox.getValue()));
    }

    @FXML
    private void handleHardButtonAction() {
        startLocalTetrisGame(GameLevel.HARD, setGameMode(modeChoiceBox.getValue()));
    }

    private void startLocalTetrisGame(GameLevel level, GameMode mode) {
        TetrisStateRepository stateRepository = new TetrisStateRepositoryImpl();
        TetrisClient client = new TetrisClient(stateRepository);
        LocalTetrisServer server = new LocalTetrisServer(level, mode, client);
        TetrisActionRepository actionRepository = new TetrisActionRepositoryImpl(server);

        SE13Application.navController.navigate(AppScreen.TETRIS, (controller) -> {
            ((TetrisScreenController) controller).setArguments(actionRepository, stateRepository);
        });
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
