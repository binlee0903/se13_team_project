package org.se13.view.start;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import org.se13.SE13Application;
import org.se13.game.event.ReadyForMatching;
import org.se13.online.ClientActionRepository;
import org.se13.online.ReadNetworkRepository;
import org.se13.online.TetrisClientSocket;
import org.se13.online.TetrisEventPacket;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.sqlite.config.PlayerKeycode;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.se13.view.tetris.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartScreenController extends BaseController {

    @FXML
    private Button startButton;
    @FXML
    public Button onlineButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button scoreButton;
    @FXML
    private Button quitButton;

    @FXML
    private void handleTetrisButtonAction() {
        SE13Application.navController.navigate(AppScreen.LEVEL_SELECT);
    }

    @FXML
    private void handleOnlineButtonAction() throws IOException {
        // Turn into a game screen
        startOnlineTetrisGame();
    }

    private void startOnlineTetrisGame() throws IOException {
        final String host = getIpAddr();
//        final String host = "localhost";
        final int port = 5555;

        Socket socket = new Socket(host, port);
        TetrisClientSocket client = new TetrisClientSocket(socket);
        ReadyForMatching matching = (ReadyForMatching) ((TetrisEventPacket) client.read()).event();

        int playerId = matching.playerId();
        int opponentId = matching.opponentId();
        client.setUserId(playerId);

        ReadNetworkRepository networkRepository = new ReadNetworkRepository(client, playerId);
        TetrisEventRepository playerEventRepository = networkRepository.playerEventRepository();
        TetrisEventRepository opponentEventRepository = networkRepository.opponentEventRepository();
        networkRepository.read();

        ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
        TetrisActionRepository playerActionRepository = new ClientActionRepository(service, client);

        PlayerKeycode keycode = new ConfigRepositoryImpl(0).getPlayerKeyCode();
        PlayerKeycode emptyKeycode = new PlayerKeycode("", "", "", "", "", "", "");

        Player player = new Player(playerId, keycode, playerActionRepository, playerEventRepository);
        Player opponent = new Player(opponentId, emptyKeycode, new EmptyTetrisActionRepository(), opponentEventRepository);

        System.out.println("myPlayerId: " + playerId + ", opponentPlayerId: " + opponentId);

        SE13Application.navController.navigate(AppScreen.BATTLE, (controller) -> {
            ((BattleScreenController) controller).setArguments(player, opponent);
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

    @FXML
    private void handleSettingsButtonAction() {
        // Turn into a setting screen
        SE13Application.navController.navigate(AppScreen.SETTING);
    }

    @FXML
    private void handleScoreButtonAction() {
        // Turn into a scoreboard screen
        SE13Application.navController.navigate(AppScreen.RANKING);
    }

    @FXML
    private void handleQuitButtonAction() {
        // Quit the app
        System.exit(0);
    }
}
