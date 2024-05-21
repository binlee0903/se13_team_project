package org.se13.view.start;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.json.JSONObject;
import org.se13.SE13Application;
import org.se13.ai.Computer;
import org.se13.ai.ComputerEventRepository;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.server.LocalTetrisServer;
import org.se13.utils.JsonUtils;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.se13.view.tetris.TetrisEventRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class StartScreenController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(StartScreenController.class);
    @FXML
    private Button startButton;
    @FXML
    public Button trainingButton;
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
    private void handleSettingsButtonAction() {
        // Turn into a setting screen
        SE13Application.navController.navigate(AppScreen.SETTING);
    }

    @FXML
    private void handleTrainingButtonAction() {
        new Thread(() -> {
            int batch = 10;
            ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
            Random random = new Random();
            JSONObject data = JsonUtils.readJson();
            JSONObject parent = new JSONObject();
            AtomicInteger integer = new AtomicInteger(0);
            Map<Integer, JSONObject> cached = new HashMap<>();

            Computer.SaveComputer saver = (computerId, w1, w2, w3, w4, fitness) -> {
                cached.put(computerId, JsonUtils.createObject(w1, w2, w3, w4, fitness));
                if (integer.incrementAndGet() == 10) {
                    cached.forEach((id, object) -> {
                        parent.put(String.valueOf(id), object);
                    });

                    try {
                        JsonUtils.saveJson(parent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    log.info("Data is saved");
                }
            };

            for (int i = 0; i < batch; i++) {
                final int computerId = -i;
                service.execute(() -> {
                    boolean isMutate = random.nextDouble() < 0.05;
                    JSONObject object = isMutate ? null : data.getJSONObject(String.valueOf(computerId));
                    log.info("Computer{} Loaded: {}", computerId, object);
                    Computer computer = new Computer(computerId, null, new ComputerEventRepository(new TetrisEventRepositoryImpl()), object, saver);
                    LocalTetrisServer server = new LocalTetrisServer(GameLevel.NORMAL, GameMode.DEFAULT);
                    computer.connectToServer(server);
                    computer.getActionRepository().connect();
                });
            }
        }).start();
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
