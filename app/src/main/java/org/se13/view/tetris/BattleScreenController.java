package org.se13.view.tetris;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.se13.SE13Application;
import org.se13.game.block.*;
import org.se13.game.config.Config;
import org.se13.game.event.ServerErrorEvent;
import org.se13.game.event.TetrisEvent;
import org.se13.game.event.UpdateTetrisState;
import org.se13.utils.Subscriber;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleScreenController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(BattleScreenController.class);

    public enum GameSize {
        SMALL,
        MEDIUM,
        LARGE
    }

    @FXML
    private Canvas nextBlockCanvas;
    @FXML
    private Label score;
    @FXML
    private BorderPane frame;
    @FXML
    private Canvas gameCanvas;

    private TetrisScreenViewModel viewModel;
    private TetrisActionRepository actionRepository1;
    private TetrisActionRepository actionRepository2;
    private TetrisEventRepository stateRepository1;
    private TetrisEventRepository stateRepository2;
    private GraphicsContext tetrisGridView;
    private GraphicsContext nextBlockView;

    private GameSize gameSize;
    private double width;
    private double height;
    private int interval;

    private final char DEFAULT_BLOCK_TEXT = '0';
    private final char FEVER_BLOCK_TEXT = 'F';
    private final char WEIGHT_ITEM_BLOCK_TEXT = 'W';
    private final char RESET_BLOCK_TEXT = 'A';
    private final char ALL_CLEAR_BLOCK_TEXT = 'A';
    private final char LINE_CLEAR_BLOCK_TEXT = 'L';

    @Override
    public void onCreate() {
        Scene scene = gameCanvas.getScene();
        viewModel = new TetrisScreenViewModel(actionRepository1, stateRepository1);
        tetrisGridView = gameCanvas.getGraphicsContext2D();
        nextBlockView = nextBlockCanvas.getGraphicsContext2D();

        setInitState();

        viewModel.observe(observeEvent(), bindGameEnd());

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            String keyCode = key.getCode().getName().toLowerCase();
            handleKeyEvent(keyCode);
        });

        this.frame.setStyle("-fx-border-color: red;");

        viewModel.connect();
        // TODO: 플레이어 별 ViewModel 관리 방법 생각하기
        actionRepository2.connect();
    }

    public void setArguments(TetrisActionRepository actionRepository1,
                             TetrisEventRepository stateRepository1,
                             TetrisActionRepository actionRepository2,
                             TetrisEventRepository stateRepository2) {

        this.actionRepository1 = actionRepository1;
        this.stateRepository1 = stateRepository1;
        this.actionRepository2 = actionRepository2;
        this.stateRepository2 = stateRepository2;
    }

    private void setInitState() {
        if (Config.SCREEN_WIDTH == 300) setSmallScreen();
        else if (Config.SCREEN_WIDTH == 600) setMediumScreen();
        else if (Config.SCREEN_WIDTH == 1920) setLargeScreen();

        frame.setMaxWidth(width);
        frame.setMaxHeight(height);
        gameCanvas.setWidth(width);
        gameCanvas.setHeight(height);
    }

    private Subscriber<TetrisEvent> observeEvent() {
        return (event) -> {
            Platform.runLater(() -> {
                switch (event) {
                    case UpdateTetrisState state -> handleUpdateState(state);
                    case ServerErrorEvent error -> handleServerError(error);
                    default -> {
                    }
                }
            });
        };
    }

    private void handleServerError(ServerErrorEvent error) {
        // 서버 에러 메시지를 보여주고 시작 화면으로 이동
        log.error("Server Error: {}", error.message());
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Server Error");
            alert.setHeaderText("A server error has occurred");
            alert.setContentText(error.message() + "\n" + "게임 시작 화면으로 이동합니다.");
            alert.showAndWait();
            // 오류창을 닫으면 시작 화면으로 이동
            SE13Application.navController.navigate(AppScreen.START);
        });
    }

    private void handleUpdateState(UpdateTetrisState state) {
        drawNextBlock(state.nextBlock());
        setTetrisState(state.tetrisGrid());
        score.setText(String.valueOf(state.score()));
    }

    private Subscriber<TetrisGameEndData> bindGameEnd() {
        return (endData) -> {
            Platform.runLater(() -> {
                SE13Application.navController.navigate(AppScreen.GAMEOVER, (GameOverScreenController controller) -> {
                    controller.setArguments(endData);
                });
            });
        };
    }

    private void setSmallScreen() {
        gameSize = GameSize.SMALL;
        width = 100;
        height = 210;
        interval = 10;
    }

    private void setMediumScreen() {
        gameSize = GameSize.MEDIUM;
        width = 150;
        height = 315;
        interval = 15;
        tetrisGridView.setFont(new Font("Arial", 20));
        nextBlockView.setFont(new Font("Arial", 20));
        nextBlockCanvas.setWidth(100);
    }

    private void setLargeScreen() {
        gameSize = GameSize.LARGE;
        width = 250;
        height = 530;
        interval = 25;
        tetrisGridView.setFont(new Font("Arial", 30));
        nextBlockView.setFont(new Font("Arial", 30));
        nextBlockCanvas.setWidth(100);
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

    private void drawNextBlock(CurrentBlock block) {
        nextBlockView.clearRect(0, 0, width, height);

        BlockPosition[] nextBlockPositions = block.shape();

        int colIndex, rowIndex;
        Cell[] cells = block.cells();

        for (int i = 0; i < 4; i++) {
            colIndex = nextBlockPositions[i].getColIndex();
            rowIndex = nextBlockPositions[i].getRowIndex() + 1;

            nextBlockView.setFill(block.getColor());
            nextBlockView.setFill(getCellColor(cells[i].cellID()));
            nextBlockView.fillText(String.valueOf(getCellCharacter(cells[i].cellID())), colIndex * interval, rowIndex * interval);
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
                 CBLOCK_ID -> DEFAULT_BLOCK_TEXT;
        };
    }

    private void handleKeyEvent(String keyCode) {
        if (keyCode.compareToIgnoreCase(Config.DROP) == 0) {
            viewModel.immediateBlockPlace();
        } else if (keyCode.compareToIgnoreCase(Config.DOWN) == 0) {
            viewModel.moveBlockDown();
        } else if (keyCode.compareToIgnoreCase(Config.LEFT) == 0) {
            viewModel.moveBlockLeft();
        } else if (keyCode.compareToIgnoreCase(Config.RIGHT) == 0) {
            viewModel.moveBlockRight();
        } else if (keyCode.compareToIgnoreCase(Config.CW_SPIN) == 0) {
            viewModel.rotateBlockCW();
        } else if (keyCode.compareToIgnoreCase(Config.PAUSE) == 0) {
            viewModel.togglePauseState();
        } else if (keyCode.compareToIgnoreCase(Config.EXIT) == 0) {
            viewModel.exitGame();
            System.exit(0);
        }
    }
}
