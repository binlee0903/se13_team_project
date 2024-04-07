package org.se13.game.tetris;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.se13.SE13Application;
import org.se13.game.timer.BlockCollideTimer;
import org.se13.game.timer.BlockFallingTimer;
import org.se13.game.block.Block;
import org.se13.game.block.BlockPosition;
import org.se13.game.block.CurrentBlock;
import org.se13.game.config.InputConfig;
import org.se13.game.grid.TetrisGrid;
import org.se13.game.input.InputManager;
import org.se13.game.rule.BlockQueue;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.sqlite.ranking.RankingRepositoryImpl;
import org.se13.view.nav.Screen;

import java.util.Random;

public class DefaultTetrisGame {
    enum GameStatus {
        GAMEOVER,
        RUNNING,
        PAUSED
    }

    enum BlockSpeed {
        DEFAULT,
        FASTER,
        RAGE,
        IMPOSSIBLE
    }

    private DefaultTetrisGame(Canvas tetrisGameCanvas, Canvas nextBlockCanvas, Label scoreLabel, boolean isTestMode) {
        this.blockQueue = new BlockQueue(new Random().nextLong());
        this.tetrisGameGrid = new TetrisGrid(ROW_SIZE, COL_SIZE);

        this.gameStatus = GameStatus.PAUSED;
        this.blockSpeed = BlockSpeed.DEFAULT;
        this.score = 0;
        this.scoreWeight = 10;

        this.isGameStarted = false;
        this.isTestMode = isTestMode;
        this.isBlockPlaced = false;
        this.isBlockCollided = false;

        this.currentBlock = nextBlock();
        this.nextBlock = nextBlock();

        this.inputConfig = new InputConfig();

        if (isTestMode == false) {
            this.gameGraphicsContext = tetrisGameCanvas.getGraphicsContext2D();
            this.nextBlockGraphicsContext = nextBlockCanvas.getGraphicsContext2D();
            this.scoreLabel = scoreLabel;
            this.CANVAS_WIDTH = tetrisGameCanvas.getWidth();
            this.CANVAS_HEIGHT = tetrisGameCanvas.getHeight();
            this.inputManager = InputManager.getInstance(scoreLabel.getScene());
        } else {
            this.isBlockPlaced = true;
            this.gameGraphicsContext = null;
            this.nextBlockGraphicsContext = null;
            this.CANVAS_WIDTH = 200;
            this.CANVAS_HEIGHT = 400;
        }

        this.animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                pulse(l);
            }
        };
    }

    public static DefaultTetrisGame getInstance(Canvas tetrisGameCanvas, Canvas nextBlockCanvas, Label scoreLabel, boolean isTestMode) {
        if (tetrisGame == null) {
            tetrisGame = new DefaultTetrisGame(tetrisGameCanvas, nextBlockCanvas, scoreLabel, isTestMode);
        }

        return tetrisGame;
    }

    public void pulse(long l) {
        currentTime = l;

        if (gameStatus == GameStatus.RUNNING) {
            if (inputManager.peekInput()) {
                processUserInput(inputManager.getInput());
            }

            if (isGameStarted == false) {
                blockMovingTimer = new BlockFallingTimer(l);
                collideCheckingTimer = new BlockCollideTimer(l);
                isGameStarted = true;
                drawNextBlock();
            }

            tick(l);
            prepare();
            render();
            update();
        } else if (gameStatus == GameStatus.PAUSED) {
            animationTimer.stop();
        } else {
            animationTimer.stop();
        }
    }

    public void startGame() {
        this.gameStatus = GameStatus.RUNNING;

        if (this.isTestMode == false) {
            animationTimer.start();
        }
    }

    public void stopGame() {
        this.gameStatus = GameStatus.GAMEOVER;

        if (this.isTestMode == false) {
            this.inputManager.reset();
            SE13Application.navController.navigate(Screen.GAMEOVER);
        }
    }

    public void togglePauseState() {
        if (this.gameStatus != GameStatus.PAUSED) {
            this.gameStatus = GameStatus.PAUSED;

            if (this.isTestMode == false) {
                blockMovingTimer.pauseTimer();
                collideCheckingTimer.pauseTimer();
            }
        } else {
            this.gameStatus = GameStatus.RUNNING;

            if (this.isTestMode == false) {
                blockMovingTimer.resumeTimer(currentTime);
                collideCheckingTimer.resumeTimer(currentTime);
            }

            if (isTestMode == false) {
                animationTimer.start();
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
        return "normal";
    }

    public boolean isItemMode() {
        return false;
    }

    public BlockSpeed getBlockSpeed() {
        return this.blockSpeed;
    }

    public CurrentBlock getCurrentBlock() { return this.currentBlock; }

    public TetrisGrid getTetrisGrid() { return this.tetrisGameGrid; }

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

    boolean isGamePaused() {
        return this.gameStatus == GameStatus.PAUSED;
    }

    void setCurrentBlock(CurrentBlock currentBlock) {
        this.currentBlock = currentBlock;
    }

    void drawBlockIntoGrid() {
        BlockPosition currentBlockPosition = currentBlock.getPosition();

        for (BlockPosition p : currentBlock.shape()) {
            tetrisGameGrid.setCell(p.getRowIndex() + currentBlockPosition.getRowIndex(), p.getColIndex() + currentBlockPosition.getColIndex(), currentBlock.getId());
        }
    }

    void deleteCurrentBlockFromGrid() {
        BlockPosition currentBlockPosition = currentBlock.getPosition();

        for (BlockPosition p : currentBlock.shape()) {
            tetrisGameGrid.setCell(p.getRowIndex() + currentBlockPosition.getRowIndex(), p.getColIndex() + currentBlockPosition.getColIndex(), 0);
        }
    }

    void processUserInput(char keyCode) {
        if (keyCode == this.inputConfig.DROP) {
            immediateBlockPlace();
        } else if (keyCode == this.inputConfig.DOWN) {
            moveBlockDown();
        } else if (keyCode == this.inputConfig.LEFT) {
            moveBlockLeft();
        } else if (keyCode == this.inputConfig.RIGHT) {
            moveBlockRight();
        } else if (keyCode == this.inputConfig.CW_SPIN) {
            rotateBlockCW();
        }
    }

    void updateBlockSpeed() {
        if (clearedLines > 10 && clearedLines <= 30 && blockSpeed == BlockSpeed.DEFAULT) {
            blockSpeed = BlockSpeed.FASTER;

            if (this.isTestMode == false) {
                blockMovingTimer.fasterBlockFallingTime();
            }

            scoreWeight += 10;
        } else if (clearedLines > 30 && clearedLines <= 60 && blockSpeed == BlockSpeed.FASTER) {
            blockSpeed = BlockSpeed.RAGE;
            blockMovingTimer.fasterBlockFallingTime();
            scoreWeight += 20;
        } else if (clearedLines > 60 && blockSpeed == BlockSpeed.RAGE) {
            blockSpeed = BlockSpeed.IMPOSSIBLE;
            blockMovingTimer.fasterBlockFallingTime();
            scoreWeight += 30;
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

        if (isBlockPlaced == true) {
            int clearedRows = tetrisGameGrid.clearFullRows();
            clearedLines += clearedRows;

            if (clearedRows > 0) {
                score += scoreWeight * clearedRows;
            }

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

    private CurrentBlock nextBlock() {
        return new CurrentBlock(blockQueue.nextBlock());
    }

    private void drawNextBlock() {
        nextBlockGraphicsContext.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        BlockPosition[] nextBlockPositions = nextBlock.shape();

        int colIndex = 0;
        int rowIndex = 0;

        for (int i = 0; i < 4; i++) {
            colIndex = nextBlockPositions[i].getColIndex();
            rowIndex = nextBlockPositions[i].getRowIndex() + 1; // 더 잘보이게 하기 위해 행 인덱스에 1을 더해줌

            nextBlockGraphicsContext.setFill(nextBlock.getColor().getBlockColor());
            nextBlockGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), colIndex * TEXT_INTERVAL, rowIndex * TEXT_INTERVAL);
        }
    }

    private void render() {
        gameGraphicsContext.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                switch (tetrisGameGrid.getCell(i, j)) {
                    case 1: // I Block
                        gameGraphicsContext.setFill(Block.IBlock.blockColor.getBlockColor());
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case 2: // J Block
                        gameGraphicsContext.setFill(Block.JBlock.blockColor.getBlockColor());
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case 3: // L Block
                        gameGraphicsContext.setFill(Block.LBlock.blockColor.getBlockColor());
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case 4: // O Block
                        gameGraphicsContext.setFill(Block.OBlock.blockColor.getBlockColor());
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case 5: // S Block
                        gameGraphicsContext.setFill(Block.SBlock.blockColor.getBlockColor());
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case 6: // T Block
                        gameGraphicsContext.setFill(Block.TBlock.blockColor.getBlockColor());
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    case 7:
                        gameGraphicsContext.setFill(Block.ZBlock.blockColor.getBlockColor());
                        gameGraphicsContext.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                        break;
                    default:
                        gameGraphicsContext.fillText(String.valueOf(' '), j * TEXT_INTERVAL, i * TEXT_INTERVAL);
                }
            }
        }
    }

    private void prepare() {
        gameGraphicsContext.setFill(new Color(0, 0, 0, 1.0));
        gameGraphicsContext.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    private static DefaultTetrisGame tetrisGame;

    private final int ROW_SIZE = 22;
    private final int COL_SIZE = 10;
    private final int TEXT_INTERVAL = 10;
    private final double CANVAS_WIDTH;
    private final double CANVAS_HEIGHT;
    private final char DEFAULT_BLOCK_TEXT = 'O';
    private long currentTime;
    private AnimationTimer animationTimer;
    private InputManager inputManager;
    private InputConfig inputConfig;
    private TetrisGrid tetrisGameGrid;
    private final BlockQueue blockQueue;
    private CurrentBlock currentBlock;
    private CurrentBlock nextBlock;
    private final GraphicsContext gameGraphicsContext;
    private final GraphicsContext nextBlockGraphicsContext;
    private GameStatus gameStatus;
    private BlockSpeed blockSpeed;
    private Label scoreLabel;
    private BlockFallingTimer blockMovingTimer;
    private BlockCollideTimer collideCheckingTimer;
    private int score;
    private int scoreWeight;
    private int clearedLines = 0;
    private boolean isGameStarted;
    private boolean isTestMode;
    private boolean isBlockPlaced;
    private boolean isBlockCollided;
}
