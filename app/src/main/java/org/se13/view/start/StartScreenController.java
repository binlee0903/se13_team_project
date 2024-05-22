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


    private int batch = 100;
    private int hold = 20;
    private int mutate = 20;
    private int crossed = 40;
    private ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
    private AtomicInteger integer = new AtomicInteger(0);
    private Map<Integer, JSONObject> cached = new HashMap<>();
    private Computer.SaveComputer saver = (computerId, w1, w2, w3, w4, fitness) -> {
        cached.put(computerId, JsonUtils.createObject(w1, w2, w3, w4, fitness));
        int order = integer.incrementAndGet();
        if (order % 25 == 0) {
            log.info("{} Computers finished", order);
        }
        if (order == batch) {
            try {
                JsonUtils.saveJson(evolution(cached));
                log.info("Data is saved");
                integer.set(0);
                handleTrainingButtonAction();
                log.info("Restarted Traning");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
        startTrainingInBackground();
    }

    private void startTrainingInBackground() {
        new Thread(this::startTraining).start();
    }

    private void startTraining() {
        JSONObject data = JsonUtils.readJson();

        for (int i = 0; i < batch; i++) {
            final int computerId = i;
            JSONObject object = getData(computerId, data);
            if (i < hold) {
                log.info("Computer{} Loaded: {}", computerId, object);
            }

            service.execute(() -> {
                Computer computer = new Computer(computerId, null, new ComputerEventRepository(new TetrisEventRepositoryImpl()), object, saver);
                LocalTetrisServer server = new LocalTetrisServer(GameLevel.NORMAL, GameMode.DEFAULT);
                computer.connectToServer(server);
                computer.getActionRepository().connect();
            });
        }
    }

    /**
     * Batch 100개 기준
     * 상위 20개는 유지
     * 상위 20개끼리 교배해서 40개 추가
     * 상위 20개 데이터에서 부분적으로 돌연변이 추가
     * 나머지 20개는 버림 (데이터가 없으면 돌연변이가 됨)
     */
    private JSONObject evolution(Map<Integer, JSONObject> result) {
        Map<Integer, JSONObject> nextGeneration = new HashMap<>();
        List<Map.Entry<Integer, JSONObject>> list = new ArrayList<>(result.entrySet());
        String fitness = "fitness";
        list.sort(Comparator.comparingInt(v -> v.getValue().getInt(fitness)));

        AtomicInteger integer = new AtomicInteger(0);

        for (int i = 0; i < hold; i++) {
            Map.Entry<Integer, JSONObject> alive = list.get(list.size() - i - 1);
            nextGeneration.put(integer.getAndIncrement(), alive.getValue());
        }

        JSONObject parent = new JSONObject();

        List<JSONObject> cross = new ArrayList<>(100);
        nextGeneration.forEach((k1, v1) ->
            nextGeneration.forEach((k2, v2) -> {
                JSONObject crossResult = crossOver(v1, v2);
                cross.add(crossResult);
            }));

        Collections.shuffle(cross);

        for (int i = 0; i < crossed; i++) {
            nextGeneration.put(integer.getAndIncrement(), cross.get(i));
        }

        for (int i = 0; i < mutate; i++) {
            JSONObject mutate = nextGeneration.get(i);
            nextGeneration.put(integer.getAndIncrement(), mutation(mutate));
        }

        nextGeneration.forEach((key, value) -> parent.put(String.valueOf(key), value));

        return parent;
    }

    private JSONObject crossOver(JSONObject left, JSONObject right) {
        float[][] w1 = Matrix.crossOver(JsonUtils.getFloatArray(left, "w1"), JsonUtils.getFloatArray(right, "w1"));
        float[][] w2 = Matrix.crossOver(JsonUtils.getFloatArray(left, "w2"), JsonUtils.getFloatArray(right, "w2"));
        float[][] w3 = Matrix.crossOver(JsonUtils.getFloatArray(left, "w3"), JsonUtils.getFloatArray(right, "w3"));
        float[][] w4 = Matrix.crossOver(JsonUtils.getFloatArray(left, "w4"), JsonUtils.getFloatArray(right, "w4"));

        return JsonUtils.createObject(w1, w2, w3, w4, 0);
    }

    private JSONObject mutation(JSONObject object) {
        float[][] w1 = JsonUtils.getFloatArray(object, "w1");
        float[][] w2 = JsonUtils.getFloatArray(object, "w2");
        float[][] w3 = JsonUtils.getFloatArray(object, "w3");
        float[][] w4 = JsonUtils.getFloatArray(object, "w4");

        mutation(w1);
        mutation(w2);
        mutation(w3);
        mutation(w4);

        return JsonUtils.createObject(w1, w2, w3, w4, 0);
    }

    private void mutation(float[][] original) {
        Random random = new Random();
        float percent = random.nextFloat();

        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                if (percent < 0.1f) {
                    original[i][j] = random.nextFloat();
                }
            }
        }
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
