package org.se13.view.tetris;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.json.JSONObject;
import org.se13.SE13Application;
import org.se13.ai.Computer;
import org.se13.ai.ComputerEventRepository;
import org.se13.game.block.Block;
import org.se13.game.block.CellID;
import org.se13.game.config.Config;
import org.se13.game.event.TetrisEvent;
import org.se13.game.event.UpdateTetrisState;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.server.LocalTetrisServer;
import org.se13.utils.JsonUtils;
import org.se13.utils.Matrix;
import org.se13.utils.Subscriber;
import org.se13.view.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TrainingScreenController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(TetrisScreenController.class);

    private boolean isEnd = false;

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
        if (order == batch) {
            try {
                JsonUtils.saveJson(evolution(cached));
                integer.set(0);
                startTrainingInBackground();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final char DEFAULT_BLOCK_TEXT = '0';
    private final char FEVER_BLOCK_TEXT = 'F';
    private final char WEIGHT_ITEM_BLOCK_TEXT = 'W';
    private final char RESET_BLOCK_TEXT = 'A';
    private final char ALL_CLEAR_BLOCK_TEXT = 'A';
    private final char LINE_CLEAR_BLOCK_TEXT = 'L';
    private final int width = 150;
    private final int height = 315;
    private final int interval = 15;

    @FXML
    private BorderPane frame;
    @FXML
    private Canvas gameCanvas;

    private GraphicsContext tetrisGridView;

    @Override
    public void onCreate() {
        super.onCreate();
        Scene scene = gameCanvas.getScene();
        tetrisGridView = gameCanvas.getGraphicsContext2D();

        setInitState();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent -> {
            isEnd = true;
            SE13Application.navController.popBackStack();
        }));

        this.frame.setStyle("-fx-border-color: red;");

        startTrainingInBackground();
    }

    private void setInitState() {
        frame.setMaxWidth(width);
        frame.setMaxHeight(height);
        gameCanvas.setWidth(width);
        gameCanvas.setHeight(height);
    }

    private void observe(TetrisEventRepository repository, int index) {
        if (index == 0) {
            repository.subscribe(observeEvent(), this::observeEnd);
        }
    }

    private Subscriber<TetrisEvent> observeEvent() {
        return (event) -> {
            Platform.runLater(() -> {
                if (event instanceof UpdateTetrisState) {
                    CellID[][] cells = ((UpdateTetrisState) event).tetrisGrid();
                    setTetrisState(cells);
                }
            });
        };
    }

    private void setTetrisState(CellID[][] cells) {
        tetrisGridView.setFill(new Color(0, 0, 0, 1.0));
        tetrisGridView.fillRect(0, 0, width, height);
        tetrisGridView.clearRect(0, 0, width, height);

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                CellID cellID = cells[i][j];
                tetrisGridView.setFill(getCellColor(cellID));
                tetrisGridView.fillText(String.valueOf(getCellCharacter(cellID)), j * interval, i * interval);
            }
        }
    }

    private Color getCellColor(CellID cellID) {
        return switch (cellID) {
            case EMPTY -> null;
            case IBLOCK_ID -> Block.IBlock.blockColor;
            case JBLOCK_ID -> Block.JBlock.blockColor;
            case LBLOCK_ID -> Block.LBlock.blockColor;
            case OBLOCK_ID -> Block.OBlock.blockColor;
            case SBLOCK_ID -> Block.SBlock.blockColor;
            case TBLOCK_ID -> Block.TBlock.blockColor;
            case ZBLOCK_ID -> Block.ZBlock.blockColor;
            case ATTACKED_BLOCK_ID -> Block.AttackedBlock.blockColor;
            case CBLOCK_ID,
                 WEIGHT_ITEM_ID,
                 FEVER_ITEM_ID,
                 WEIGHT_BLOCK_ID,
                 RESET_ITEM_ID,
                 LINE_CLEAR_ITEM_ID,
                 ALL_CLEAR_ITEM_ID -> Color.WHITE;
        };
    }

    private char getCellCharacter(CellID cellID) {
        return switch (cellID) {
            case EMPTY -> ' ';
            case FEVER_ITEM_ID -> FEVER_BLOCK_TEXT;
            case WEIGHT_ITEM_ID, WEIGHT_BLOCK_ID -> WEIGHT_ITEM_BLOCK_TEXT;
            case RESET_ITEM_ID -> RESET_BLOCK_TEXT;
            case LINE_CLEAR_ITEM_ID -> LINE_CLEAR_BLOCK_TEXT;
            case ALL_CLEAR_ITEM_ID -> ALL_CLEAR_BLOCK_TEXT;
            case IBLOCK_ID,
                 JBLOCK_ID,
                 LBLOCK_ID,
                 OBLOCK_ID,
                 SBLOCK_ID,
                 TBLOCK_ID,
                 ZBLOCK_ID,
                 CBLOCK_ID,
                 ATTACKED_BLOCK_ID -> DEFAULT_BLOCK_TEXT;
        };
    }

    private void observeEnd(TetrisGameEndData end) {

    }

    private void startTrainingInBackground() {
        if (isEnd) return;
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
                TetrisEventRepository eventRepository = new TetrisEventRepositoryImpl();
                Computer computer = new Computer(computerId, null, new ComputerEventRepository(eventRepository), object, saver);
                LocalTetrisServer server = new LocalTetrisServer(GameLevel.NORMAL, GameMode.DEFAULT);
                observe(computer.eventRepository, computerId);
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

        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                if (random.nextFloat() < 0.1f) {
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
}
