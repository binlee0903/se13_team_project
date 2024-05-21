package org.se13.view.difficulty;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import org.json.JSONObject;
import org.se13.SE13Application;
import org.se13.ai.Computer;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.server.LocalBattleTetrisServer;
import org.se13.server.LocalTetrisServer;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.sqlite.config.PlayerKeycode;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.se13.view.tetris.BattleScreenController;
import org.se13.view.tetris.Player;
import org.se13.view.tetris.TetrisEventRepositoryImpl;
import org.se13.view.tetris.TetrisScreenController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LevelSelectScreenController extends BaseController {


    @FXML
    public void initialize() {
        modeChoiceBox.setItems(FXCollections.observableArrayList("default","item","timeLimit"));
        modeChoiceBox.setValue("default");
        typeChoiceBox.setItems(FXCollections.observableArrayList("single", "battle", "computer"));
        typeChoiceBox.setValue("computer");
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
            case "computer" -> startComputerBattleTetrisGame(level, gameMode);
        }
    }

    private void startLocalTetrisGame(GameLevel level, GameMode mode) {
        Player player = new Player(1, new ConfigRepositoryImpl(0).getPlayerKeyCode(), new TetrisEventRepositoryImpl());
        LocalTetrisServer server = new LocalTetrisServer(level, mode);
        player.connectToServer(server);
        SE13Application.navController.navigate(AppScreen.TETRIS, (controller) -> {
            ((TetrisScreenController) controller).setArguments(player);
        });
    }

    private void startLocalBattleTetrisGame(GameLevel level, GameMode mode) {
        LocalBattleTetrisServer server = new LocalBattleTetrisServer(level, mode);
        Player player1 = new Player(1, new ConfigRepositoryImpl(0).getPlayerKeyCode(), new TetrisEventRepositoryImpl());
        Player player2 = new Player(2, new ConfigRepositoryImpl(1).getPlayerKeyCode(), new TetrisEventRepositoryImpl());
        player1.connectToServer(server);
        player2.connectToServer(server);
        SE13Application.navController.navigate(AppScreen.BATTLE, (controller) -> {
            ((BattleScreenController) controller).setArguments(player1, player2, server);
        });
    }

    private void startComputerBattleTetrisGame(GameLevel level, GameMode mode) {
        LocalBattleTetrisServer server = new LocalBattleTetrisServer(level, mode);
        Player player = new Player(1, new ConfigRepositoryImpl(0).getPlayerKeyCode(), new TetrisEventRepositoryImpl());
        PlayerKeycode emptyCode = new PlayerKeycode("", "", "", "", "", "", "");
        JSONObject data = readJson();
        Player computer = new Computer(-1, emptyCode, new TetrisEventRepositoryImpl(), data.getJSONObject("-1"), saver);
        player.connectToServer(server);
        computer.connectToServer(server);
        SE13Application.navController.navigate(AppScreen.BATTLE, (controller) -> {
            ((BattleScreenController) controller).setArguments(player, computer, server);
        });
    }

    private JSONObject readJson() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return new JSONObject(content);
        } catch (Exception e) {
            throw new RuntimeException();
        }
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

    private final String path = "C:/Users/someh/Downloads/computer.json";

    private Computer.SaveComputer saver = (computerId, w1, w2, w3, w4, fitness) ->
        new Thread(() -> {
            try {
                JSONObject parent = new JSONObject();
                JSONObject object = new JSONObject();
                object.put("w1", w1);
                object.put("w2", w2);
                object.put("w3", w3);
                object.put("w4", w4);
                object.put("fitness", fitness);
                parent.put(String.valueOf(computerId), object);
                FileWriter fs = new FileWriter(path);
                BufferedWriter writer = new BufferedWriter(fs);
                parent.write(writer);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

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
