package org.se13.view.difficulty;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import org.se13.SE13Application;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.server.LocalBattleTetrisServer;
import org.se13.server.LocalTetrisServer;
import org.se13.server.TetrisActionHandler;
import org.se13.server.TetrisClient;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.se13.view.tetris.*;

public class LevelSelectScreenController extends BaseController {

    @FXML
    private void initialize() {
        modeChoiceBox.setItems(FXCollections.observableArrayList("default","item","timeLimit"));
        modeChoiceBox.setValue("default");
        typeChoiceBox.setItems(FXCollections.observableArrayList("single", "battle"));
        typeChoiceBox.setValue("single");
    }

    @FXML
    private void handleEasyButtonAction() {
        startTetrisGame(GameLevel.EASY, setGameMode(modeChoiceBox.getValue()), typeChoiceBox.getValue());
    }

    @FXML
    private void handleNormalButtonAction() {
        startTetrisGame(GameLevel.NORMAL, setGameMode(modeChoiceBox.getValue()), typeChoiceBox.getValue());
    }

    @FXML
    private void handleHardButtonAction() {
        startTetrisGame(GameLevel.HARD, setGameMode(modeChoiceBox.getValue()), typeChoiceBox.getValue());
    }

    private void startTetrisGame(GameLevel level, GameMode gameMode, String type) {
        switch (type) {
            case "single" -> startLocalTetrisGame(level, gameMode);
            case "battle" -> startLocalBattleTetrisGame(level, gameMode);
        }
    }

    private void startLocalTetrisGame(GameLevel level, GameMode mode) {
        TetrisEventRepository eventRepository = new TetrisEventRepositoryImpl();
        TetrisClient client = new TetrisClient(-1, eventRepository);
        LocalTetrisServer server = new LocalTetrisServer(level, mode);
        TetrisActionHandler handler = server.connect(client);
        TetrisActionRepository actionRepository = new TetrisActionRepositoryImpl(client.getUserId(), handler);

        SE13Application.navController.navigate(AppScreen.TETRIS, (controller) -> {
            ((TetrisScreenController) controller).setArguments(actionRepository, eventRepository);
        });
    }

    private void startLocalBattleTetrisGame(GameLevel level, GameMode mode) {
        TetrisEventRepository eventRepository1 = new TetrisEventRepositoryImpl();
        TetrisClient client1 = new TetrisClient(1, eventRepository1);

        TetrisEventRepository eventRepository2 = new TetrisEventRepositoryImpl();
        TetrisClient client2 = new TetrisClient(2, eventRepository2);

        LocalBattleTetrisServer server = new LocalBattleTetrisServer(level, mode);
        TetrisActionHandler handler1 = server.connect(client1);
        TetrisActionHandler handler2 = server.connect(client2);

        TetrisActionRepository actionRepository1 = new TetrisActionRepositoryImpl(client1.getUserId(), handler1);
        TetrisActionRepository actionRepository2 = new TetrisActionRepositoryImpl(client2.getUserId(), handler2);

        SE13Application.navController.navigate(AppScreen.BATTLE, (controller) -> {
            ((BattleScreenController) controller).setArguments(actionRepository1, eventRepository1, actionRepository2, eventRepository2);
        });
    }

    private GameMode setGameMode(String gameMode) {
        return switch (gameMode) {
            case "default" -> GameMode.DEFAULT;
            case "item" -> GameMode.ITEM;
            case "timeLimit" -> GameMode.TIME_LIMIT;
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

    @FXML
    ChoiceBox<String> typeChoiceBox;
}
