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
import org.se13.utils.Matrix;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.se13.view.tetris.TetrisEventRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
            int batch = 100;
            AtomicReference<ExecutorService> service = new AtomicReference<>(Executors.newVirtualThreadPerTaskExecutor());
            JSONObject data = JsonUtils.readJson();
            AtomicInteger integer = new AtomicInteger(0);
            Map<Integer, JSONObject> cached = new HashMap<>();

            Computer.SaveComputer saver = (computerId, w1, w2, w3, w4, fitness) -> {
                cached.put(computerId, JsonUtils.createObject(w1, w2, w3, w4, fitness));
                if (integer.incrementAndGet() == batch) {
                    try {
                        JsonUtils.saveJson(evolution(cached));
                        handleTrainingButtonAction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    log.info("Data is saved");
                    service.set(null);
                    throw new RuntimeException();
                }
            };

            for (int i = 0; i < batch; i++) {
                final int computerId = i;
                service.get().execute(() -> {
                    JSONObject object = getData(computerId, data);
                    log.info("Computer{} Loaded: {}", computerId, object);
                    Computer computer = new Computer(computerId, null, new ComputerEventRepository(new TetrisEventRepositoryImpl()), object, saver);
                    LocalTetrisServer server = new LocalTetrisServer(GameLevel.NORMAL, GameMode.DEFAULT);
                    computer.connectToServer(server);
                    computer.getActionRepository().connect();
                });
            }
        }).start();
    }

    /**
     * Batch 100개 기준
     * 상위 10위는 그대로
     * 상위 10개끼리 교배해서 60개 추가
     * 나머지 30개는 버림 (데이터가 없으면 돌연변이가 됨)
     */
    private JSONObject evolution(Map<Integer, JSONObject> result) {
        Map<Integer, JSONObject> nextGeneration = new HashMap<>();
        List<Map.Entry<Integer, JSONObject>> list = new ArrayList<>(result.entrySet());
        String fitness = "fitness";
        list.sort(Comparator.comparingInt(v -> v.getValue().getInt(fitness)));

        AtomicInteger integer = new AtomicInteger(0);

        for (int i = 0; i < 10; i++) {
            Map.Entry<Integer, JSONObject> alive = list.get(list.size() - i - 1);
            nextGeneration.put(integer.getAndIncrement(), alive.getValue());
        }

        JSONObject parent = new JSONObject();

        List<JSONObject> cross = new ArrayList<>(100);
        nextGeneration.forEach((k1, v1) -> nextGeneration.forEach((k2, v2) -> cross.add(crossOver(v1, v2))));

        for (int i = 0; i < 60; i++) {
            nextGeneration.put(integer.getAndIncrement(), cross.get(i));
        }

        nextGeneration.forEach((key, value) -> parent.put(String.valueOf(key), value));

        return parent;
    }

    private JSONObject crossOver(JSONObject left, JSONObject right) {
        double[][] w1 = Matrix.crossOver(JsonUtils.getDoubleArray(left, "w1"), JsonUtils.getDoubleArray(right, "w1"));
        double[][] w2 = Matrix.crossOver(JsonUtils.getDoubleArray(left, "w2"), JsonUtils.getDoubleArray(right, "w2"));
        double[][] w3 = Matrix.crossOver(JsonUtils.getDoubleArray(left, "w3"), JsonUtils.getDoubleArray(right, "w3"));
        double[][] w4 = Matrix.crossOver(JsonUtils.getDoubleArray(left, "w4"), JsonUtils.getDoubleArray(right, "w4"));

        return JsonUtils.createObject(w1, w2, w3, w4, 0);
    }

    private JSONObject getData(int computerId, JSONObject object) {
        try {
            return object.getJSONObject(String.valueOf(computerId));
        } catch (Exception e) {
            return null;
        }
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
