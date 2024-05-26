package org.se13.view.tetris;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.se13.ai.*;
import org.se13.game.block.Block;
import org.se13.game.block.CellID;
import org.se13.game.event.TetrisEvent;
import org.se13.game.event.UpdateTetrisState;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.server.LocalTetrisServer;
import org.se13.utils.JsonUtils;
import org.se13.utils.Subscriber;
import org.se13.view.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TrainingScreenController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(TetrisScreenController.class);
    public Label score;

    private boolean isEnd = false;

    private int batch = 100;
    private int hold = 10;
    private int crossed = 30;
    private int mutate = 50;
    private ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
    private AtomicInteger integer = new AtomicInteger(0);
    private List<NeuralResult> cached = new ArrayList<>(100);
    private Computer.SaveComputer saver = (computerId, result) -> {
        cached.add(result);
        int order = integer.incrementAndGet();

        if (order == batch) {
            try {
                List<NeuralResult> neuralList = evolution(cached);
                JsonUtils.saveJson(new SaveData(neuralList.stream().distinct().toList()));
                integer.set(0);
                cached.clear();
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
                    UpdateTetrisState state = ((UpdateTetrisState) event);
                    CellID[][] cells = state.tetrisGrid();
                    setTetrisState(cells);
                    setTetrisScore(state.score());
                }
            });
        };
    }

    private void setTetrisScore(int score) {
        this.score.setText(String.valueOf(score));
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

    private SaveData data;

    private void startTraining() {
        data = JsonUtils.readJson();

        for (int i = 0; i < batch; i++) {
            final int computerId = i;
            final NeuralResult result = getData(computerId, data);

            if (computerId < 10) {
                log.info("computer{} fitness: {}, predict: {}", computerId, result.fitness(), result.predict());
            }

            service.execute(() -> {
                TetrisEventRepository eventRepository = new TetrisEventRepositoryImpl();
                Computer computer = new Computer(computerId, null, new ComputerEventRepository(eventRepository), result.predict(), saver);
                LocalTetrisServer server = new LocalTetrisServer(GameLevel.NORMAL, GameMode.DEFAULT);
                observe(computer.eventRepository, computerId);
                computer.connectToServer(server);
                computer.getActionRepository().connect();
            });
        }
    }

    /**
     * Batch 200개 기준
     * 상위 20개는 유지
     * 상위 20개끼리 교배해서 60개 추가
     * 상위 20개 데이터에서 부분적으로 돌연변이 추가
     * 나머지 20개는 버림 (데이터가 없으면 새로 만들어짐)
     */
    private List<NeuralResult> evolution(List<NeuralResult> results) {
        Random random = new Random();
        results.sort((r1, r2) -> -Integer.compare(r1.fitness(), r2.fitness()));

        List<NeuralResult> next = new ArrayList<>(200);
        List<NeuralResult> best = new ArrayList<>(hold);

        for (int i = 0; i < hold; i++) {
            best.add(results.get(i));
        }

        // crossover
        List<NeuralResult> cross = new ArrayList<>(hold * hold);
        best.forEach((r1) ->
                best.forEach((n2) ->
                        cross.add(r1.cross(n2))));

        for (int i = 0; i < mutate; i++) {
            NeuralResult select = best.get(random.nextInt(best.size()));
            best.add(select.mutate(random));
        }

        next.addAll(best);
        Collections.shuffle(cross);

        for (int i = 0; i < crossed; i++) {
            next.add(cross.get(i));
        }

        return next;
    }

    private NeuralResult getData(int computerId, SaveData data) {
        try {
            return data.get(computerId);
        } catch (Exception e) {
            return new NeuralResult(new Predict());
        }
    }
}
