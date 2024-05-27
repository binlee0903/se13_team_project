package org.se13.view.difficulty;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextInputDialog;
import org.se13.SE13Application;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.online.IsFirst;
import org.se13.server.LocalBattleTetrisServer;
import org.se13.server.LocalTetrisServer;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.sqlite.config.PlayerKeycode;
import org.se13.sqlite.ranking.Ranking;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.se13.view.ranking.RankingProperty;
import org.se13.view.tetris.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class LevelSelectScreenController extends BaseController {

    @FXML
    public void initialize() {
        modeChoiceBox.setItems(FXCollections.observableArrayList("default", "item", "timeLimit"));
        modeChoiceBox.setValue("default");
        typeChoiceBox.setItems(FXCollections.observableArrayList("single", "battle", "online"));
        typeChoiceBox.setValue("single");
    }

    @FXML
    private void handleEasyButtonAction() throws IOException, ClassNotFoundException {
        startTetrisGame(GameLevel.EASY, setGameMode(modeChoiceBox.getValue()), typeChoiceBox.getValue());
    }

    @FXML
    private void handleNormalButtonAction() throws IOException, ClassNotFoundException {
        startTetrisGame(GameLevel.NORMAL, setGameMode(modeChoiceBox.getValue()), typeChoiceBox.getValue());
    }

    @FXML
    private void handleHardButtonAction() throws IOException, ClassNotFoundException {
        startTetrisGame(GameLevel.HARD, setGameMode(modeChoiceBox.getValue()), typeChoiceBox.getValue());
    }

    private void startTetrisGame(GameLevel level, GameMode gameMode, String type) throws IOException, ClassNotFoundException {
        switch (type) {
            case "single" -> startLocalTetrisGame(level, gameMode);
            case "battle" -> startLocalBattleTetrisGame(level, gameMode);
            case "online" -> startOnlineTetrisGame(level, gameMode);
        }
    }

    private void startLocalTetrisGame(GameLevel level, GameMode mode) {
        Player player = new Player(-1, new ConfigRepositoryImpl(0).getPlayerKeyCode());
        LocalTetrisServer server = new LocalTetrisServer(level, mode);
        player.connectToServer(server);
        SE13Application.navController.navigate(AppScreen.TETRIS, (controller) -> {
            ((TetrisScreenController) controller).setArguments(player);
        });
    }

    private void startLocalBattleTetrisGame(GameLevel level, GameMode mode) {
        LocalBattleTetrisServer server = new LocalBattleTetrisServer(level, mode);
        Player player1 = new Player(1, new ConfigRepositoryImpl(0).getPlayerKeyCode());
        Player player2 = new Player(2, new ConfigRepositoryImpl(1).getPlayerKeyCode());
        player1.connectToServer(server);
        player2.connectToServer(server);
        SE13Application.navController.navigate(AppScreen.BATTLE, (controller) -> {
            ((BattleScreenController) controller).setArguments(player1, player2);
        });
    }

    private void startOnlineTetrisGame(GameLevel level, GameMode mode) throws IOException, ClassNotFoundException {
        LocalBattleTetrisServer server = new LocalBattleTetrisServer(level, mode);
//        final String host = getIpAddr();
        final String host = "localhost";
        final int port = 5555;
        int myPlayerId = 0;
        int opponentPlayerId = 1;

        Socket socket = new Socket(host, port);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        try {
            IsFirst isFirst = (IsFirst) ois.readObject();
            if (!isFirst.isFirst()) {
                myPlayerId = 1;
                opponentPlayerId = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Player myPlayer = new Player(myPlayerId, new ConfigRepositoryImpl(0).getPlayerKeyCode(), socket, new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
        myPlayer.connectToOnlineServer(server);
        PlayerKeycode emptyKeycode = new PlayerKeycode("", "", "", "", "", "", "");
        Player opponentPlayer = new Player(opponentPlayerId, emptyKeycode, socket, new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
        opponentPlayer.connectOpponent(server);

        System.out.println("myPlayerId: " + myPlayerId + ", opponentPlayerId: " + opponentPlayerId);

        SE13Application.navController.navigate(AppScreen.BATTLE, (controller) -> {
            ((BattleScreenController) controller).setArguments(myPlayer, opponentPlayer);
        });
    }

    private String getIpAddr() {
        // TextInputDialog 생성
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("서버 주소 입력");
        dialog.setHeaderText("""
                Default IP Address: localhost
                Port: 5555
                """);
        dialog.setContentText("IP 주소를 입력해주세요:");

        // 사용자가 OK 버튼을 누를 때까지 대화 상자를 표시하고 입력 결과를 기다림
        Optional<String> result = dialog.showAndWait();

        return result.orElse("localhost");
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
