package org.se13.game.tetris;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.se13.game.block.Block;
import org.se13.game.block.BlockPosition;
import org.se13.game.block.CurrentBlock;
import org.se13.game.config.InputConfig;
import org.se13.game.grid.TetrisGrid;
import org.se13.game.input.InputManager;
import org.se13.game.rule.BlockQueue;
import org.se13.sqlite.config.ConfigRepository;
import org.se13.sqlite.config.ConfigRepositoryImpl;

import java.util.Map;
import java.util.Random;

public class DefaultTetrisGame implements ITetrisGame {
    enum GameStatus {
        GAMEOVER,
        RUNNING,
        PAUSED
    }

    public DefaultTetrisGame(Canvas canvas, Label scoreLabel) {
        this.blockQueue = new BlockQueue(new Random().nextInt());
        this.tetrisGrid = new TetrisGrid(ROW_SIZE, COL_SIZE);
        this.gc = canvas.getGraphicsContext2D();
        this.inputManager = InputManager.getInstance(scoreLabel.getScene());
        this.gameStatus = GameStatus.PAUSED;
        this.scoreLabel = scoreLabel;
        this.score = 0;
        this.CANVAS_WIDTH = canvas.getWidth();
        this.CANVAS_HEIGHT = canvas.getHeight();
        this.isBlockPlaced = false;
        this.currentBlock = nextBlock();
        this.configRepository = new ConfigRepositoryImpl();
        this.configRepository.createNewTableConfig();
        this.configRepository.insertDefaultConfig(0);
        this.inputConfig = new InputConfig(this.configRepository);

        this.animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (gameStatus == GameStatus.RUNNING) {
                    if (inputManager.peekInput()) {
                        processUserInput(inputManager.getInput());
                    }

                    prepare();
                    render();
                    update();
                } else if (gameStatus == GameStatus.PAUSED) {
                    animationTimer.stop();
                } else {
                    animationTimer.stop();
                }
            }
        };
    }

    @Override
    public void startGame() {
        this.gameStatus = GameStatus.RUNNING;

        animationTimer.start();
    }

    @Override
    public void stopGame() {
        this.gameStatus = GameStatus.GAMEOVER;
    }

    @Override
    public void pauseGame() {
        this.gameStatus = GameStatus.PAUSED;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    private CurrentBlock nextBlock() {
        return new CurrentBlock(blockQueue.nextBlock());
    }

    private boolean blockFits(int rowMovementAmount, int colMovementAmount, int newRotationState) {
        /*for (BlockPosition p : currentBlock.shape()) {
            if (!tetrisGrid.isEmptyCell(p.getRowPosition(), p.getColIndex())) {
                return false;
            }
        }*/

        return true;
    }

    private void rotateBlockCW() {
        int newRotateState = (currentBlock.getRotateState() + 1) % 4;

        if (blockFits(0, 0, newRotateState)) {
            deleteBlockFromGrid();
            currentBlock.rotateCW();
        }
    }

    private void rotateBlockCCW() {
        int newRotateState = currentBlock.getRotateState();

        if (newRotateState == 0) {
            newRotateState = 3;
        } else {
            newRotateState++;
        }

        if (blockFits(0, 0, newRotateState)) {
            deleteBlockFromGrid();
            currentBlock.rotateCCW();
        }
    }

    private void moveBlockLeft() {
        if (blockFits(0, -1, 0)) {
            deleteBlockFromGrid();
            currentBlock.move(-1, 0);
        }
    }

    private void moveBlockRight() {
        if (blockFits(0, 1, 0)) {
            deleteBlockFromGrid();
            currentBlock.move(1, 0);
        }
    }

    private void moveBlockDown() {
        if (blockFits(1, 0, 0)) {
            deleteBlockFromGrid();
            currentBlock.move(0, 1);
        }
    }

    private boolean isGameOver() {
        return !(tetrisGrid.isRowEmpty(1) && tetrisGrid.isRowEmpty(2));
    }

    private void drawBlockIntoGrid() {
        BlockPosition currentBlockPosition = currentBlock.getPosition();

        for (BlockPosition p : currentBlock.shape()) {
            tetrisGrid.setCell(p.getRowPosition() + currentBlockPosition.getRowPosition(), p.getColIndex() + currentBlockPosition.getColIndex(), currentBlock.getId());
        }

        /*if (isGameOver() == true) {
            this.gameStatus = GameStatus.GAMEOVER;
        }*/
    }

    private void deleteBlockFromGrid() {
        BlockPosition currentBlockPosition = currentBlock.getPosition();

        for (BlockPosition p : currentBlock.shape()) {
            tetrisGrid.setCell(p.getRowPosition() + currentBlockPosition.getRowPosition(), p.getColIndex() + currentBlockPosition.getColIndex(), 0);
        }
    }

    private void processUserInput(char keyCode) {
        if (keyCode == this.inputConfig.UP) {

        } else if (keyCode == this.inputConfig.DOWN) {
            moveBlockDown();
        } else if (keyCode == this.inputConfig.LEFT) {
            moveBlockLeft();
        } else if (keyCode == this.inputConfig.RIGHT) {
            moveBlockRight();
        } else if (keyCode == this.inputConfig.CW_SPIN) {
            rotateBlockCW();
        } else if (keyCode == this.inputConfig.CCW_SPIN) {
            rotateBlockCCW();
        } else if (keyCode == this.inputConfig.PAUSE) {

        } else if (keyCode == this.inputConfig.EXIT) {
            stopGame();
        }
    }

    private void render() {
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                switch (tetrisGrid.getCell(i, j)) {
                    case 1: // I Block
                        gc.setFill(Block.IBlock.blockColor.getBlockColor());
                        gc.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), i * TEXT_INTERVAL, j * TEXT_INTERVAL);
                        break;
                    case 2: // J Block
                        gc.setFill(Block.JBlock.blockColor.getBlockColor());
                        gc.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), i * TEXT_INTERVAL, j * TEXT_INTERVAL);
                        break;
                    case 3: // L Block
                        gc.setFill(Block.LBlock.blockColor.getBlockColor());
                        gc.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), i * TEXT_INTERVAL, j * TEXT_INTERVAL);
                        break;
                    case 4: // O Block
                        gc.setFill(Block.OBlock.blockColor.getBlockColor());
                        gc.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), i * TEXT_INTERVAL, j * TEXT_INTERVAL);
                        break;
                    case 5: // S Block
                        gc.setFill(Block.SBlock.blockColor.getBlockColor());
                        gc.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), i * TEXT_INTERVAL, j * TEXT_INTERVAL);
                        break;
                    case 6: // T Block
                        gc.setFill(Block.TBlock.blockColor.getBlockColor());
                        gc.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), i * TEXT_INTERVAL, j * TEXT_INTERVAL);
                        break;
                    case 7:
                        gc.setFill(Block.ZBlock.blockColor.getBlockColor());
                        gc.fillText(String.valueOf(DEFAULT_BLOCK_TEXT), i * TEXT_INTERVAL, j * TEXT_INTERVAL);
                        break;
                    default:
                        gc.setFill(Color.WHITE);
                        gc.fillText(String.valueOf(' '), i * TEXT_INTERVAL, j * TEXT_INTERVAL);
                }
            }
        }
    }

    private void update() {
        drawBlockIntoGrid();
        int clearedRows = tetrisGrid.clearFullRows();

        if (clearedRows > 0) {
            score += 10 * clearedRows;
        }

        scoreLabel.setText(String.valueOf(score));

        if (isBlockPlaced == true) {
            currentBlock = nextBlock();
            isBlockPlaced = false;
        }
    }

    private void prepare() {
        gc.setFill(new Color(0, 0, 0, 1.0));
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    private final int ROW_SIZE = 10;
    private final int COL_SIZE = 22;
    private final int TEXT_INTERVAL = 10;
    private final double CANVAS_WIDTH;
    private final double CANVAS_HEIGHT;
    private final char DEFAULT_BLOCK_TEXT = 'O';
    private AnimationTimer animationTimer;
    private ConfigRepositoryImpl configRepository;
    private InputManager inputManager;
    private InputConfig inputConfig;
    private TetrisGrid tetrisGrid;
    private final BlockQueue blockQueue;
    private CurrentBlock currentBlock;
    private GraphicsContext gc;
    private GameStatus gameStatus;
    private Label scoreLabel;
    private int score;
    private boolean isBlockPlaced;
    private long elapsedTime;
    private long lastTime;
}
