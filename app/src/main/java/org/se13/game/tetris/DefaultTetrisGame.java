package org.se13.game.tetris;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.se13.SE13Application;
import org.se13.game.block.*;
import org.se13.game.config.Config;
import org.se13.game.grid.TetrisGrid;
import org.se13.game.input.InputManager;
import org.se13.game.item.*;
import org.se13.game.rule.BlockQueue;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.timer.BlockCollideTimer;
import org.se13.game.timer.BlockFallingTimer;
import org.se13.game.timer.FeverModeTimer;
import org.se13.game.timer.LineClearAnimationTimer;
import org.se13.view.nav.AppScreen;

import java.util.Random;

public class DefaultTetrisGame {
    enum GameStatus {
        GAMEOVER,
        ANIMATION,
        RUNNING,
        PAUSED
    }

    public enum BlockSpeed {
        DEFAULT,
        FASTER,
        RAGE,
        IMPOSSIBLE
    }

    public enum GameSize {
        SMALL,
        MEDIUM,
        LARGE
    }

    protected DefaultTetrisGame(Canvas tetrisGameCanvas, Canvas nextBlockCanvas, Label scoreLabel, GameLevel gameLevel, GameMode gameMode, GameSize gameSize, boolean isTestMode) {
        this.random = new Random();
        this.blockQueue = new BlockQueue(random, gameLevel);
        this.tetrisGameGrid = new TetrisGrid(ROW_SIZE, COL_SIZE);

        this.gameStatus = GameStatus.PAUSED;
        this.blockSpeed = BlockSpeed.DEFAULT;
        this.gameDifficulty = gameLevel;
        this.gameMode = gameMode;
        this.gameSize = gameSize;
        this.score = 0;
        this.scoreWeight = 10;
        this.clearedLines = 0;
        this.lineCounterForItem = 0;

        this.isGameStarted = false;
        this.isTestMode = isTestMode;
        this.isBlockPlaced = false;
        this.isBlockCollided = false;
        this.isAnimationEnded = true;

        if (isTestMode == false) {
            Scene scene = tetrisGameCanvas.getScene();
            BorderPane parent = (BorderPane) scene.lookup("#frame");

            this.gameGraphicsContext = tetrisGameCanvas.getGraphicsContext2D();
            this.nextBlockGraphicsContext = nextBlockCanvas.getGraphicsContext2D();

            switch (this.gameSize) {
                case GameSize.SMALL:
                    this.CANVAS_WIDTH = 100;
                    this.CANVAS_HEIGHT = 210;
                    TEXT_INTERVAL = 10;
                    break;
                case GameSize.MEDIUM:
                    this.CANVAS_WIDTH = 150;
                    this.CANVAS_HEIGHT = 315;
                    TEXT_INTERVAL = 15;
                    this.gameGraphicsContext.setFont(new Font("Arial", 20));
                    this.nextBlockGraphicsContext.setFont(new Font("Arial", 20));
                    nextBlockCanvas.setWidth(100);
                    break;
                case GameSize.LARGE:
                    this.CANVAS_WIDTH = 250;
                    this.CANVAS_HEIGHT = 530;
                    TEXT_INTERVAL = 25;
                    this.gameGraphicsContext.setFont(new Font("Arial", 30));
                    this.nextBlockGraphicsContext.setFont(new Font("Arial", 30));
                    nextBlockCanvas.setWidth(100);
                    break;
                default:
                    this.CANVAS_WIDTH = 600;
                    this.CANVAS_HEIGHT = 600;
            }

            parent.setMaxWidth(this.CANVAS_WIDTH);
            parent.setMaxHeight(this.CANVAS_HEIGHT);
            tetrisGameCanvas.setWidth(this.CANVAS_WIDTH);
            tetrisGameCanvas.setHeight(this.CANVAS_HEIGHT);

            this.scoreLabel = scoreLabel;
            this.inputManager = InputManager.getInstance(scoreLabel.getScene());
        } else {
            this.isBlockPlaced = true;
            this.gameGraphicsContext = null;
            this.nextBlockGraphicsContext = null;
            this.CANVAS_WIDTH = 200;
            this.CANVAS_HEIGHT = 400;
        }

        this.currentBlock = nextBlock();
        this.nextBlock = nextBlock();

        feverModeTimer = new FeverModeTimer(
                () -> scoreWeight += FEVER_SCORE_WEIGHT,
                () -> scoreWeight -= FEVER_SCORE_WEIGHT
        );

        this.tetrisGameGrid.registerItemListener((cellID) -> {
            switch (cellID) {
                case FEVER_ITEM_ID:
                    feverModeTimer.execute();
                    break;
                case WEIGHT_ITEM_ID:
                    nextBlock = new CurrentBlock(Block.WeightItemBlock);
                    break;
                case RESET_ITEM_ID:
                    blockSpeed = BlockSpeed.DEFAULT;
                    blockMovingTimer.restoreBlockFallingTime();
                    clearedLines = 0;
                    break;
                case ALL_CLEAR_ITEM_ID:
                    this.tetrisGameGrid = new TetrisGrid(ROW_SIZE, COL_SIZE);
                    break;
            }
        });

        this.tetrisTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                pulse(l);
            }
        };
    }

    public static DefaultTetrisGame getInstance(Canvas tetrisGameCanvas, Canvas nextBlockCanvas, Label scoreLabel, GameLevel gameDifficulty, GameMode gameMode, GameSize gameSize,boolean isTestMode) {
        if (tetrisGame == null) {
            tetrisGame = new DefaultTetrisGame(tetrisGameCanvas, nextBlockCanvas, scoreLabel, gameDifficulty, gameMode, gameSize, isTestMode);
        }

        return tetrisGame;
    }

    public void pulse(long l) {
        if (gameStatus == GameStatus.RUNNING) {
            if (isTestMode == false && inputManager.peekInput()) {
                processUserInput(inputManager.getInput());
            }

            if (isGameStarted == false) {
                blockMovingTimer = new BlockFallingTimer(l);
                collideCheckingTimer = new BlockCollideTimer(l);
                lineClearAnimationTimer = new LineClearAnimationTimer(l);
                isGameStarted = true;

                if (isTestMode == false) {
                    drawNextBlock();
                }
            }

            if (feverModeTimer.isActive() && !isFeverMode()) {
                feverModeTimer.release();
            }

            if (isTestMode == false) {
                tick(l);
                prepare();
                render();
            } else {
                tick(System.nanoTime());
            }

            update();
        } else if (gameStatus == GameStatus.ANIMATION) {
            if (isAnimationEnded != true) {
                if (isTestMode == false) {
                    prepare();
                    render();
                }

                update();
            }
        } else if (gameStatus == GameStatus.PAUSED) {
            tetrisTimer.stop();
        } else {
            tetrisTimer.stop();
        }
    }

    public void startGame() {
        this.gameStatus = GameStatus.RUNNING;

        if (this.isTestMode == false) {
            tetrisTimer.start();
        }
    }

    public void stopGame() {
        this.gameStatus = GameStatus.GAMEOVER;

        if (this.isTestMode == false) {
            this.inputManager.reset();
            SE13Application.navController.navigate(AppScreen.GAMEOVER);
        }
    }

    public void togglePauseState() {
        if (this.gameStatus != GameStatus.PAUSED) {
            this.gameStatus = GameStatus.PAUSED;

            if (this.isTestMode == false) {
                feverModeTimer.setPause();
                blockMovingTimer.pauseTimer();
                collideCheckingTimer.pauseTimer();
                lineClearAnimationTimer.pauseTimer();
            }
        } else {
            this.gameStatus = GameStatus.RUNNING;

            if (this.isTestMode == false) {
                feverModeTimer.setResume();
                blockMovingTimer.resumeTimer();
                collideCheckingTimer.resumeTimer();
                lineClearAnimationTimer.resumeTimer();

                tetrisTimer.start();
            }
        }
    }

    public void resetGame() {
        tetrisGame = null;
    }

    public int getScore() {
        return this.score;
    }

    public String getDifficulty() {
        switch (this.gameDifficulty) {
            case EASY:
                return "Easy";
            case NORMAL:
                return "Normal";
            case HARD:
                return "Hard";
            default:
                assert (false);
                return null;
        }
    }

    public int getScoreWeight() {
        return scoreWeight;
    }

    public boolean isItemMode() {
        return gameMode == GameMode.ITEM;
    }

    public boolean isAnimationTimerEnded() {
        return lineClearAnimationTimer.isTimerOver();
    }

    public BlockSpeed getBlockSpeed() {
        return this.blockSpeed;
    }

    public CurrentBlock getCurrentBlock() {
        return this.currentBlock;
    }

    public TetrisGrid getTetrisGrid() {
        return this.tetrisGameGrid;
    }

    boolean blockFits() {
        for (BlockPosition p : currentBlock.shape()) {
            int blockRowIndex = p.getRowIndex() + currentBlock.getPosition().getRowIndex();
            int blockColIndex = p.getColIndex() + currentBlock.getPosition().getColIndex();

            if (tetrisGameGrid.isEmptyCell(blockRowIndex, blockColIndex) == false) {
                return false;
            }

            if (tetrisGameGrid.isInsideGrid(blockRowIndex, blockColIndex) == false) {
                return false;
            }
        }

        return true;
    }

    void rotateBlockCW() {
        deleteCurrentBlockFromGrid();
        currentBlock.rotateCW();

        score++;

        if (blockFits() == false) {
            score--;
            currentBlock.rotateCCW();
        }
    }

    void moveBlockLeft() {
        deleteCurrentBlockFromGrid();
        currentBlock.move(0, -1);

        if (blockFits() == false) {
            currentBlock.move(0, 1);
        }
    }

    void moveBlockRight() {
        deleteCurrentBlockFromGrid();
        currentBlock.move(0, 1);

        if (blockFits() == false) {
            currentBlock.move(0, -1);
        }
    }

    void moveBlockDown() {
        deleteCurrentBlockFromGrid();
        currentBlock.move(1, 0);
        this.score += scoreWeight / 10;

        if (blockFits() == false) {
            isBlockCollided = true;
            this.score -= scoreWeight / 10;
            currentBlock.move(-1, 0);
        } else {
            isBlockCollided = false;
        }
    }

    void immediateBlockPlace() {
        deleteCurrentBlockFromGrid();

        for (int i = currentBlock.getPosition().getRowIndex(); i < ROW_SIZE; i++) {
            currentBlock.move(1, 0);
            if (blockFits() == false) {
                isBlockPlaced = true;
                currentBlock.move(-1, 0);
            }
        }
    }

    boolean isGameOver() {
        if (tetrisGameGrid.isRowEmpty(0) == true) {
            return false;
        } else {
            return true;
        }
    }

    boolean isFeverMode() {
        return feverModeTimer.isFeverMode();
    }

    boolean isGamePaused() {
        return this.gameStatus == GameStatus.PAUSED;
    }

    void setCurrentBlock(CurrentBlock currentBlock) {
        this.currentBlock = currentBlock;
    }

    void setBlockPlaced(boolean isBlockPlaced) {
        this.isBlockPlaced = isBlockPlaced;
    }

    void setClearedLines(int clearedLines) {
        this.clearedLines = clearedLines;
    }

    void drawBlockIntoGrid() {
        BlockPosition currentBlockPosition = currentBlock.getPosition();

        for (Cell cell : currentBlock.cells()) {
            tetrisGameGrid.setCell(
                    cell.position().getRowIndex() + currentBlockPosition.getRowIndex(),
                    cell.position().getColIndex() + currentBlockPosition.getColIndex(),
                    cell.cellID()
            );
        }
    }

    void deleteCurrentBlockFromGrid() {
        BlockPosition currentBlockPosition = currentBlock.getPosition();

        for (BlockPosition p : currentBlock.shape()) {
            tetrisGameGrid.setCell(p.getRowIndex() + currentBlockPosition.getRowIndex(), p.getColIndex() + currentBlockPosition.getColIndex(), CellID.EMPTY);
        }
    }

    void processUserInput(String keyCode) {
        if (keyCode.compareToIgnoreCase(Config.DROP) == 0) {
            immediateBlockPlace();
        } else if (keyCode.compareToIgnoreCase(Config.DOWN) == 0) {
            moveBlockDown();
        } else if (keyCode.compareToIgnoreCase(Config.LEFT) == 0) {
            moveBlockLeft();
        } else if (keyCode.compareToIgnoreCase(Config.RIGHT) == 0) {
            moveBlockRight();
        } else if (keyCode.compareToIgnoreCase(Config.CW_SPIN) == 0) {
            rotateBlockCW();
        }
    }

    public void updateBlockSpeed() {
        if (clearedLines > 10 && clearedLines <= 30 && blockSpeed == BlockSpeed.DEFAULT) {
            blockSpeed = BlockSpeed.FASTER;

            if (this.isTestMode == false) {
                blockMovingTimer.fasterBlockFallingTime(this.gameDifficulty);
            }

            scoreWeight += 10 * gameDifficulty.getWeight();
        } else if (clearedLines > 30 && clearedLines <= 80 && blockSpeed == BlockSpeed.FASTER) {
            blockSpeed = BlockSpeed.RAGE;

            if (this.isTestMode == false) {
                blockMovingTimer.fasterBlockFallingTime(this.gameDifficulty);
            }

            scoreWeight += 20 * gameDifficulty.getWeight();
        } else if (clearedLines > 80 && blockSpeed == BlockSpeed.RAGE) {
            blockSpeed = BlockSpeed.IMPOSSIBLE;

            if (this.isTestMode == false) {
                blockMovingTimer.fasterBlockFallingTime(this.gameDifficulty);
            }

            scoreWeight += 30 * gameDifficulty.getWeight();
        }
    }

    void tick(long l) {
        blockMovingTimer.setCurrentTime(l);
        collideCheckingTimer.setCurrentTime(l);

        if (blockMovingTimer.isBlockFallingTimeHasGone() == true) {
            moveBlockDown();
            blockMovingTimer.reset(l);
        }

        if (isBlockCollided == true) {
            if (collideCheckingTimer.isBlockPlaceTimeEnded() == true) {
                isBlockPlaced = true;
                isBlockCollided = false;
                collideCheckingTimer.reset(l);
            }

            if (collideCheckingTimer.isTimerStarted() == false) {
                collideCheckingTimer.setFirstBlockCollideTime(l);
            }
        } else {
            collideCheckingTimer.reset(l);
        }
    }

    void update() {
        drawBlockIntoGrid();

        if (this.isTestMode == false) {
            scoreLabel.setText(String.valueOf(score));
        }

        // WeightBlock이 떨어졌을 때, 해당 열을 지워줌
        if (isBlockPlaced == true && currentBlock.getId() == Block.WeightItemBlock.cellId) {
            int clearedColIndex = currentBlock.getPosition().getColIndex();
            tetrisGameGrid.clearWeightCol(clearedColIndex);
        }

        if (isBlockPlaced == true) {
            tetrisGameGrid.triggerLineClearItem();
            int fullRows = tetrisGameGrid.animateFullRows();

            if (fullRows > 0 || isAnimationEnded == false) {
                lineClearAnimationTimer.startLineClearAnimation(collideCheckingTimer, blockMovingTimer, feverModeTimer, false);

                if (lineClearAnimationTimer.isTimerOver() == true) {
                    blockMovingTimer.resumeTimer();
                    collideCheckingTimer.resumeTimer();
                    feverModeTimer.setResume();

                    score += scoreWeight * fullRows;
                    tetrisGameGrid.clearFullRows();
                    clearedLines += fullRows;
                    lineCounterForItem += fullRows;
                    lineClearAnimationTimer.resetFlags();
                    gameStatus = GameStatus.RUNNING;
                    isAnimationEnded = true;
                } else {
                    gameStatus = GameStatus.ANIMATION;
                    isAnimationEnded = false;
                }
            }

            if (isAnimationEnded == true) {
                updateBlockSpeed();

                currentBlock = nextBlock;
                nextBlock = nextBlock();

                if (this.isTestMode == false) {
                    drawNextBlock();
                }

                isBlockPlaced = false;

                if (isGameOver() == true) {
                    stopGame();
                }
            }
        }
    }

    private CurrentBlock nextBlock() {
        if (gameMode == GameMode.ITEM && lineCounterForItem >= 10) {
            CurrentBlock block = nextItemBlock();
            lineCounterForItem -= 10;
            return block;
        }
        return new CurrentBlock(blockQueue.nextBlock());
    }

    CurrentBlock nextItemBlock() {
        CellID[] list = new CellID[]{
            CellID.FEVER_ITEM_ID,
            CellID.WEIGHT_ITEM_ID,
            CellID.RESET_ITEM_ID,
            CellID.LINE_CLEAR_ITEM_ID,
            CellID.ALL_CLEAR_ITEM_ID,
        };

        Block block = blockQueue.nextBlock();

        return switch (list[random.nextInt(list.length)]) {
            case FEVER_ITEM_ID -> new CurrentBlock(block, new FeverItem(random, block));
            case WEIGHT_ITEM_ID -> new CurrentBlock(block, new WeightItem(random, block));
            case RESET_ITEM_ID -> new CurrentBlock(block, new FallingTimeResetItem(random, block));
            case LINE_CLEAR_ITEM_ID -> new CurrentBlock(block, new LineClearItem(random, block));
            case ALL_CLEAR_ITEM_ID -> new CurrentBlock(block, new AllClearItem(random, block));
            default -> throw new IllegalStateException();
        };
    }

    private void drawNextBlock() {
        nextBlockGraphicsContext.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        BlockPosition[] nextBlockPositions = nextBlock.shape();

        int colIndex = 0;
        int rowIndex = 0;
        Cell[] cells = nextBlock.cells();

        for (int i = 0; i < 4; i++) {
            colIndex = nextBlockPositions[i].getColIndex();
            rowIndex = nextBlockPositions[i].getRowIndex() + 1; // 더 잘보이게 하기 위해 행 인덱스에 1을 더해줌

            nextBlockGraphicsContext.setFill(nextBlock.getColor());

            switch (cells[i].cellID()) {
                case FEVER_ITEM_ID:
                    nextBlockGraphicsContext.setFill(Color.WHITE);
                    nextBlockGraphicsContext.fillText(String.valueOf(FEVER_BLOCK_TEXT), colIndex * TEXT_INTERVAL, rowIndex * TEXT_INTERVAL);
                    break;

                case WEIGHT_ITEM_ID:
                case CellID.WEIGHT_BLOCK_ID:
                    nextBlockGraphicsContext.setFill(Color.WHITE);
                    nextBlockGraphicsContext.fillText(String.valueOf(WEIGHT_ITEM_BLOCK_TEXT), colIndex * TEXT_INTERVAL, rowIndex * TEXT_INTERVAL);
                    break;

                case RESET_ITEM_ID:
                    nextBlockGraphicsContext.setFill(Color.WHITE);
                    nextBlockGraphicsContext.fillText(String.valueOf(RESET_BLOCK_TEXT), colIndex * TEXT_INTERVAL, rowIndex * TEXT_INTERVAL);
                    break;

                case ALL_CLEAR_ITEM_ID:
                    nextBlockGraphicsContext.setFill(Color.WHITE);
                    nextBlockGraphicsContext.fillText(String.valueOf(ALL_CLEAR_BLOCK_TEXT), colIndex * TEXT_INTERVAL, rowIndex * TEXT_INTERVAL);
                    break;

                case LINE_CLEAR_ITEM_ID:
                    nextBlockGraphicsContext.setFill(Color.WHITE);
                    nextBlockGraphicsContext.fillText(String.valueOf(LINE_CLEAR_BLOCK_TEXT), colIndex * TEXT_INTERVAL, rowIndex * TEXT_INTERVAL);
                    break;

                default:
                    nextBlockGraphicsContext.setFill(nextBlock.getColor());
                    nextBlockGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), colIndex * TEXT_INTERVAL, rowIndex * TEXT_INTERVAL);
            }
        }
    }

    private void render() {
        gameGraphicsContext.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                switch (tetrisGameGrid.getCell(i, j)) {
                    case IBLOCK_ID: // I Block
                        gameGraphicsContext.setFill(Block.IBlock.blockColor);
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case JBLOCK_ID: // J Block
                        gameGraphicsContext.setFill(Block.JBlock.blockColor);
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case LBLOCK_ID: // L Block
                        gameGraphicsContext.setFill(Block.LBlock.blockColor);
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case OBLOCK_ID: // O Block
                        gameGraphicsContext.setFill(Block.OBlock.blockColor);
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case SBLOCK_ID: // S Block
                        gameGraphicsContext.setFill(Block.SBlock.blockColor);
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case TBLOCK_ID: // T Block
                        gameGraphicsContext.setFill(Block.TBlock.blockColor);
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case ZBLOCK_ID:
                        gameGraphicsContext.setFill(Block.ZBlock.blockColor);
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case CBLOCK_ID:
                        gameGraphicsContext.setFill(Color.WHITE);
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case FEVER_ITEM_ID:
                        gameGraphicsContext.setFill(Color.rgb(255, 255, 255));
                        gameGraphicsContext.fillText(String.valueOf(FEVER_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case WEIGHT_ITEM_ID:
                        gameGraphicsContext.setFill(Color.rgb(255, 255, 255));
                        gameGraphicsContext.fillText(String.valueOf(WEIGHT_ITEM_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case WEIGHT_BLOCK_ID:
                        gameGraphicsContext.setFill(Color.rgb(255, 255, 255));
                        gameGraphicsContext.fillText(String.valueOf(WEIGHT_ITEM_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case RESET_ITEM_ID:
                        gameGraphicsContext.setFill(Color.rgb(255, 255, 255));
                        gameGraphicsContext.fillText(String.valueOf(RESET_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case LINE_CLEAR_ITEM_ID:
                        gameGraphicsContext.setFill(Color.rgb(255, 255, 255));
                        gameGraphicsContext.fillText(String.valueOf(LINE_CLEAR_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case ALL_CLEAR_ITEM_ID:
                        gameGraphicsContext.setFill(Color.rgb(255, 255, 255));
                        gameGraphicsContext.fillText(String.valueOf(ALL_CLEAR_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case EMPTY:
                        gameGraphicsContext.fillText(String.valueOf(' '), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                }
            }
        }
    }

    private void prepare() {
        gameGraphicsContext.setFill(new Color(0, 0, 0, 1.0));
        gameGraphicsContext.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    private static DefaultTetrisGame tetrisGame;

    private int TEXT_INTERVAL;
    private final int ROW_SIZE = 22;
    private final int COL_SIZE = 10;
    private final double CANVAS_WIDTH;
    private final double CANVAS_HEIGHT;
    private final char DEFAULT_BLOCK_TEXT = 'O';
    private final char FEVER_BLOCK_TEXT = 'F';
    private final char WEIGHT_ITEM_BLOCK_TEXT = 'W';
    private final char RESET_BLOCK_TEXT = 'R';
    private final char ALL_CLEAR_BLOCK_TEXT = 'A';
    private final char LINE_CLEAR_BLOCK_TEXT = 'L';
    private final int FEVER_SCORE_WEIGHT = 10;
    private AnimationTimer tetrisTimer;
    private InputManager inputManager;
    private TetrisGrid tetrisGameGrid;
    private final BlockQueue blockQueue;
    private CurrentBlock currentBlock;
    private CurrentBlock nextBlock;
    private final GraphicsContext gameGraphicsContext;
    private final GraphicsContext nextBlockGraphicsContext;
    private GameStatus gameStatus;
    private GameLevel gameDifficulty;
    private GameSize gameSize;
    private GameMode gameMode;
    private BlockSpeed blockSpeed;
    private Label scoreLabel;
    private BlockFallingTimer blockMovingTimer;
    private BlockCollideTimer collideCheckingTimer;
    private FeverModeTimer feverModeTimer;
    private LineClearAnimationTimer lineClearAnimationTimer;
    private int score;
    private int scoreWeight;
    private int clearedLines = 0;
    private int lineCounterForItem;
    private boolean isGameStarted;
    private boolean isTestMode;
    private boolean isBlockPlaced;
    private boolean isBlockCollided;
    private boolean isAnimationEnded;
    private Random random;
}
