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
import javafx.scene.text.Text;
import org.se13.SE13Application;
import org.se13.game.block.*;
import org.se13.game.config.Config;
import org.se13.game.event.*;
import org.se13.sqlite.config.PlayerKeycode;
import org.se13.utils.Subscriber;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleScreenController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(BattleScreenController.class);
    private int PLAYER1;
    private int PLAYER2;

    public enum GameSize {
        SMALL,
        MEDIUM,
        LARGE
    }

    @FXML
    public Label player1_score;
    @FXML
    public Canvas player1_nextBlockCanvas;
    @FXML
    public BorderPane player1_frame;
    @FXML
    public Canvas player1_gameCanvas;
    @FXML
    public Canvas player1_attackedBlocks;
    @FXML
    public BorderPane player1_attackedBlocksFrame;

    @FXML
    public Label player2_score;
    @FXML
    public Canvas player2_nextBlockCanvas;
    @FXML
    public BorderPane player2_frame;
    @FXML
    public Canvas player2_gameCanvas;
    @FXML
    public Canvas player2_attackedBlocks;
    @FXML
    public BorderPane player2_attackedBlocksFrame;

    @FXML
    public Text player1_timeLimitPanel;
    @FXML
    public Text player1_time;
    @FXML
    public Text player2_timeLimitPanel;
    @FXML
    public Text player2_time;

    private TetrisScreenViewModel player1_viewModel;
    private TetrisScreenViewModel player2_viewModel;
    private TetrisActionRepository actionRepository1;
    private TetrisActionRepository actionRepository2;
    private TetrisEventRepository stateRepository1;
    private TetrisEventRepository stateRepository2;

    private PlayerKeycode player1_keycode;
    private PlayerKeycode player2_keycode;

    private GraphicsContext player1_tetrisGridView;
    private GraphicsContext player1_nextBlockView;

    private GraphicsContext player2_tetrisGridView;
    private GraphicsContext player2_nextBlockView;

    private GraphicsContext player1_attackedBlocksView;
    private GraphicsContext player2_attackedBlocksView;

    private GameSize gameSize;
    private double width;
    private double height;
    private int tetrisGameScreenInterval;
    private int attackedScreenInterval;
    private double attackedScreenWidth;
    private double attackedScreenHeight;
    private double attackedScreenStartPoint;

    private final char DEFAULT_BLOCK_TEXT = '0';
    private final char FEVER_BLOCK_TEXT = 'F';
    private final char WEIGHT_ITEM_BLOCK_TEXT = 'W';
    private final char RESET_BLOCK_TEXT = 'A';
    private final char ALL_CLEAR_BLOCK_TEXT = 'A';
    private final char LINE_CLEAR_BLOCK_TEXT = 'L';

    private TetrisGameEndData tempGameEndData;

    @Override
    public void onCreate() {
        Scene scene = player1_gameCanvas.getScene();

        player1_viewModel = new TetrisScreenViewModel(actionRepository1, stateRepository1);
        player1_tetrisGridView = player1_gameCanvas.getGraphicsContext2D();
        player1_nextBlockView = player1_nextBlockCanvas.getGraphicsContext2D();

        player2_viewModel = new TetrisScreenViewModel(actionRepository2, stateRepository2);
        player2_tetrisGridView = player2_gameCanvas.getGraphicsContext2D();
        player2_nextBlockView = player2_nextBlockCanvas.getGraphicsContext2D();

        player1_attackedBlocksView = player1_attackedBlocks.getGraphicsContext2D();
        player2_attackedBlocksView = player2_attackedBlocks.getGraphicsContext2D();

        tempGameEndData = null;

        setInitState();

        player1_viewModel.observe(observePlayerEvent(PLAYER1), bindGameEnd());
        player2_viewModel.observe(observePlayerEvent(PLAYER2), bindGameEnd());

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            String keyCode = key.getCode().getName().toLowerCase();
            handleKeyEvent(keyCode, player1_viewModel, player1_keycode);
            handleKeyEvent(keyCode, player2_viewModel, player2_keycode);
        });

        this.player1_frame.setStyle("-fx-border-color: red;");
        this.player2_frame.setStyle("-fx-border-color: red;");

        player1_viewModel.connect();
        player2_viewModel.connect();
    }

    public void setArguments(Player player1, Player player2) {
        this.PLAYER1 = player1.getUserId();
        this.PLAYER2 = player2.getUserId();
        this.actionRepository1 = player1.getActionRepository();
        this.stateRepository1 = player1.getEventRepository();
        this.actionRepository2 = player2.getActionRepository();
        this.stateRepository2 = player2.getEventRepository();
        this.player1_keycode = player1.getPlayerKeycode();
        this.player2_keycode = player2.getPlayerKeycode();
    }

    private void setInitState() {
        if (Config.SCREEN_WIDTH == 450) setSmallScreen();
        else if (Config.SCREEN_WIDTH == 600) setMediumScreen();
        else if (Config.SCREEN_WIDTH == 1920) setLargeScreen();

        drawBorderToAttackedScreen(player1_attackedBlocksView);
        drawBorderToAttackedScreen(player2_attackedBlocksView);

        player1_frame.setMaxWidth(width);
        player1_frame.setMaxHeight(height);
        player1_gameCanvas.setWidth(width);
        player1_gameCanvas.setHeight(height);

        player2_frame.setMaxWidth(width);
        player2_frame.setMaxHeight(height);
        player2_gameCanvas.setWidth(width);
        player2_gameCanvas.setHeight(height);
    }

    private Subscriber<TetrisEvent> observePlayerEvent(int userID) {
        return (event) -> {
            Platform.runLater(() -> {
                switch (event) {
                    case UpdateTetrisState state -> handleUpdateState(state, userID);
                    case InsertAttackBlocksEvent events -> handleAttackedState(null, userID);
                    case AttackedTetrisBlocks state -> handleAttackedState(state, userID);
                    case ServerErrorEvent error -> handleServerError(error);
                    default -> {}
                }
            });
        };
    }

    private void handleAttackedState(AttackedTetrisBlocks state, int userID) {
        if (state == null) {
            drawAttackedBlock(null, userID);
        } else {
            drawAttackedBlock(state.blocks(), userID);
        }
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

    private void handleUpdateState(UpdateTetrisState state, int userID) {
        drawNextBlock(state.nextBlock(), userID);
        setTetrisState(state.tetrisGrid(), userID);

        switch (userID) {
            case 1:
                player1_score.setText(String.valueOf(state.score()));
                player1_time.setText(String.valueOf(state.remainingTime()));
                break;
            case 2:
                player2_score.setText(String.valueOf(state.score()));
                player2_time.setText(String.valueOf(state.remainingTime()));
                break;
        }
    }

    private Subscriber<TetrisGameEndData> bindGameEnd() {
        return (endData) -> {
            if (endData.isGameOvered() == true) {
                Platform.runLater(() -> {
                    SE13Application.navController.navigate(AppScreen.GAMEOVER, (GameOverScreenController controller) -> {
                        controller.setArguments(endData);
                    });
                });
            }

            if (tempGameEndData != null) {
                if (endData.score() < tempGameEndData.score()) {
                    Platform.runLater(() -> {
                        SE13Application.navController.navigate(AppScreen.GAMEOVER, (GameOverScreenController controller) -> {
                            controller.setArguments(tempGameEndData);
                        });
                    });
                } else {
                    Platform.runLater(() -> {
                        SE13Application.navController.navigate(AppScreen.GAMEOVER, (GameOverScreenController controller) -> {
                            controller.setArguments(endData);
                        });
                    });
                }
            }

            tempGameEndData = endData;
        };
    }

    private void setSmallScreen() {
        gameSize = GameSize.SMALL;
        width = 100;
        height = 210;
        tetrisGameScreenInterval = 10;
        attackedScreenInterval = 10;
        player1_tetrisGridView.setFont(new Font("Arial", 12));
        player1_nextBlockView.setFont(new Font("Arial",  12));
        player2_tetrisGridView.setFont(new Font("Arial", 12));
        player2_nextBlockView.setFont(new Font("Arial", 12));
        player1_attackedBlocksView.setFont(new Font("Arial", 10));
        player2_attackedBlocksView.setFont(new Font("Arial", 10));
        attackedScreenWidth = 80;
        attackedScreenHeight = 100;
        attackedScreenStartPoint = 25;
        player1_attackedBlocks.setWidth(attackedScreenWidth);
        player2_attackedBlocks.setWidth(attackedScreenWidth);
        player1_attackedBlocks.setHeight(attackedScreenHeight);
        player2_attackedBlocks.setHeight(attackedScreenHeight);
    }

    private void setMediumScreen() {
        gameSize = GameSize.MEDIUM;
        width = 150;
        height = 315;
        tetrisGameScreenInterval = 15;
        attackedScreenInterval = 8;
        player1_tetrisGridView.setFont(new Font("Arial", 20));
        player1_nextBlockView.setFont(new Font("Arial", 20));

        player2_tetrisGridView.setFont(new Font("Arial", 20));
        player2_nextBlockView.setFont(new Font("Arial", 20));

        player1_nextBlockCanvas.setWidth(100);
        player2_nextBlockCanvas.setWidth(100);

        player1_attackedBlocksView.setFont(new Font("Arial", 10));
        player2_attackedBlocksView.setFont(new Font("Arial", 10));
        attackedScreenWidth = 80;
        attackedScreenHeight = 85;
        attackedScreenStartPoint = 25;
        player1_attackedBlocks.setWidth(attackedScreenWidth);
        player2_attackedBlocks.setWidth(attackedScreenWidth);
        player1_attackedBlocks.setHeight(attackedScreenHeight);
        player2_attackedBlocks.setHeight(attackedScreenHeight);
    }

    private void setLargeScreen() {
        gameSize = GameSize.LARGE;
        width = 250;
        height = 530;
        tetrisGameScreenInterval = 25;
        attackedScreenInterval = 10;
        player1_tetrisGridView.setFont(new Font("Arial", 30));
        player1_nextBlockView.setFont(new Font("Arial", 30));

        player2_tetrisGridView.setFont(new Font("Arial", 30));
        player2_nextBlockView.setFont(new Font("Arial", 30));

        player1_nextBlockCanvas.setWidth(100);
        player2_nextBlockCanvas.setWidth(100);

        player1_attackedBlocksView.setFont(new Font("Arial", 15));
        player2_attackedBlocksView.setFont(new Font("Arial", 15));
        attackedScreenWidth = 100;
        attackedScreenHeight = 105;
        attackedScreenStartPoint = 25;
        player1_attackedBlocks.setWidth(attackedScreenWidth);
        player2_attackedBlocks.setWidth(attackedScreenWidth);
        player1_attackedBlocks.setHeight(attackedScreenHeight);
        player2_attackedBlocks.setHeight(attackedScreenHeight);
    }

    private void setTetrisState(CellID[][] cells, int userID) {
        GraphicsContext gc = (userID == PLAYER1) ? player1_tetrisGridView : player2_tetrisGridView;

        gc.clearRect(0, 0, width, height);

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                CellID cellID = cells[i][j];
                gc.setFill(getCellColor(cellID));
                gc.fillText(String.valueOf(getCellCharacter(cellID)), j * tetrisGameScreenInterval, i * tetrisGameScreenInterval);
            }
        }
    }

    private void drawAttackedBlock(CellID[][] blocks, int userID) {
        GraphicsContext gc = (userID == PLAYER1) ? player1_attackedBlocksView : player2_attackedBlocksView;

        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.moveTo(0, attackedScreenStartPoint);

        drawBorderToAttackedScreen(gc);

        gc.setFill(getCellColor(CellID.ATTACKED_BLOCK_ID));

        if (blocks != null) {
            for (int i = 0; i < blocks.length; i++) {
                for (int j = 0; j < blocks[i].length; j++) {
                    gc.fillText(String.valueOf(getCellCharacter(blocks[i][j])), j * attackedScreenInterval, i * attackedScreenInterval);
                }
            }
        }
    }

    private void drawNextBlock(CurrentBlock block, int userID) {
        GraphicsContext gc = (userID == PLAYER1) ? player1_nextBlockView : player2_nextBlockView;

        gc.clearRect(0, 0, width, height);

        BlockPosition[] nextBlockPositions = block.shape();
        Cell[] cells = block.cells();

        for (int i = 0; i < 4; i++) {
            int colIndex = nextBlockPositions[i].getColIndex();
            int rowIndex = nextBlockPositions[i].getRowIndex() + 1;

            gc.setFill(block.getColor());
            gc.fillText(String.valueOf(getCellCharacter(cells[i].cellID())), colIndex * tetrisGameScreenInterval, rowIndex * tetrisGameScreenInterval);
        }
    }

    private void drawBorderToAttackedScreen(GraphicsContext gc) {
        gc.setStroke(Color.RED);
        gc.setLineWidth(1);
        gc.strokeRect(0, 0, attackedScreenWidth, attackedScreenHeight);
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
            case ATTACKED_BLOCK_ID -> Block.AttackedBlock.blockColor;
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

    private void handleKeyEvent(String keyCode, TetrisScreenViewModel viewModel, PlayerKeycode playerKeycode) {
        if (keyCode.compareToIgnoreCase(playerKeycode.drop()) == 0) {
            viewModel.immediateBlockPlace();
        } else if (keyCode.compareToIgnoreCase(playerKeycode.down()) == 0) {
            viewModel.moveBlockDown();
        } else if (keyCode.compareToIgnoreCase(playerKeycode.left()) == 0) {
            viewModel.moveBlockLeft();
        } else if (keyCode.compareToIgnoreCase(playerKeycode.right()) == 0) {
            viewModel.moveBlockRight();
        } else if (keyCode.compareToIgnoreCase(playerKeycode.spin()) == 0) {
            viewModel.rotateBlockCW();
        } else if (keyCode.compareToIgnoreCase(Config.PAUSE) == 0) {
            viewModel.togglePauseState();
        } else if (keyCode.compareToIgnoreCase(Config.EXIT) == 0) {
            viewModel.exitGame();
            System.exit(0);
        }
    }
}
