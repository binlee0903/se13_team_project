package org.se13.game.tetris;

import org.se13.game.action.TetrisAction;
import org.se13.game.block.*;
import org.se13.game.event.*;
import org.se13.game.grid.TetrisGrid;
import org.se13.game.input.InputManager;
import org.se13.game.item.*;
import org.se13.game.rule.BlockQueue;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.timer.*;
import org.se13.server.TetrisServer;
import org.se13.utils.Observer;
import org.se13.utils.Subscriber;
import org.se13.view.tetris.TetrisGameEndData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TetrisGame {
    private static final Logger log = LoggerFactory.getLogger(TetrisGame.class);
    private TetrisServer server;
    private Observer<TetrisEvent> events;

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

    public TetrisGame(GameLevel gameLevel, GameMode gameMode, TetrisServer server) {
        this.random = new Random();
        this.server = server;
        this.events = new Observer<>();
        this.blockQueue = new BlockQueue(random, gameLevel);
        this.tetrisGameGrid = new TetrisGrid(ROW_SIZE, COL_SIZE);

        this.gameStatus = GameStatus.PAUSED;
        this.blockSpeed = BlockSpeed.DEFAULT;
        this.gameDifficulty = gameLevel;
        this.gameMode = gameMode;
        this.score = 0;
        this.scoreWeight = 10;
        this.clearedLines = 0;
        this.lineCounterForItem = 0;

        this.isBlockPlaced = false;
        this.isBlockCollided = false;
        this.isAnimationEnded = true;
        this.isAttacked = false;

        this.currentBlock = nextBlock();
        this.nextBlock = nextBlock();
        this.inputManager = new InputManager();

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
    }


    public void subscribe(Subscriber<TetrisEvent> subscriber) {
        events.subscribe(subscriber);
    }

    public void pulse(long l) {
        if (gameStatus == GameStatus.RUNNING) {
            if (inputManager.peekInput()) {
                processUserInput(inputManager.getInput());
            }

            if (feverModeTimer.isActive() && !isFeverMode()) {
                feverModeTimer.release();
            }

            tick(l);
            update();
        } else if (gameStatus == GameStatus.ANIMATION) {
            if (isAnimationEnded != true) {
                update();
            }
        }

        updateState(tetrisGameGrid, nextBlock, score, timeLimitModeTimer.getRemainingTime());
    }

    public void startGame() {
        long startTime = System.nanoTime();
        this.gameStatus = GameStatus.RUNNING;
        blockMovingTimer = new BlockFallingTimer(startTime);
        collideCheckingTimer = new BlockCollideTimer(startTime);
        lineClearAnimationTimer = new LineClearAnimationTimer(startTime);
        timeLimitModeTimer = new TimeLimitModeTimer(startTime);

        if (gameMode != GameMode.TIME_LIMIT) {
            timeLimitModeTimer.disableTimer();
        }
    }

    public void stopGame() {
        this.gameStatus = GameStatus.GAMEOVER;
        this.inputManager.reset();
        server.responseGameOver(getScore(), isItemMode(), getDifficulty());
    }

    public void stopBattleGame() {
        this.gameStatus = GameStatus.GAMEOVER;
        this.inputManager.reset();
    }

    public boolean togglePauseState() {
        if (this.gameStatus != GameStatus.PAUSED) {
            this.gameStatus = GameStatus.PAUSED;

            feverModeTimer.setPause();
            blockMovingTimer.pauseTimer();
            collideCheckingTimer.pauseTimer();
            lineClearAnimationTimer.pauseTimer();
            timeLimitModeTimer.pauseTimer();
            return false;
        } else {
            this.gameStatus = GameStatus.RUNNING;

            feverModeTimer.setResume();
            blockMovingTimer.resumeTimer();
            collideCheckingTimer.resumeTimer();
            lineClearAnimationTimer.resumeTimer();
            timeLimitModeTimer.resumeTimer();
            return true;
        }
    }

    public int getScore() {
        return this.score;
    }

    public void requestInput(TetrisAction input) {
        inputManager.add(input);
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

    public void rotateBlockCW() {
        deleteCurrentBlockFromGrid();
        currentBlock.rotateCW();

        score++;

        if (blockFits() == false) {
            score--;
            currentBlock.rotateCCW();
        }
    }

    public void moveBlockLeft() {
        deleteCurrentBlockFromGrid();
        currentBlock.move(0, -1);

        if (blockFits() == false) {
            currentBlock.move(0, 1);
        }
    }

    public void moveBlockRight() {
        deleteCurrentBlockFromGrid();
        currentBlock.move(0, 1);

        if (blockFits() == false) {
            currentBlock.move(0, -1);
        }
    }

    public void moveBlockDown() {
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

    public void immediateBlockPlace() {
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
        if (tetrisGameGrid.isFirstRowEmpty(this.currentBlock) == true) {
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

    void setAttacked() {
        this.isAttacked = true;
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

    void processUserInput(TetrisAction input) {
        switch (input) {
            case IMMEDIATE_BLOCK_PLACE -> immediateBlockPlace();
            case MOVE_BLOCK_DOWN -> moveBlockDown();
            case MOVE_BLOCK_LEFT -> moveBlockLeft();
            case MOVE_BLOCK_RIGHT -> moveBlockRight();
            case ROTATE_BLOCK_CW -> rotateBlockCW();
            case TOGGLE_PAUSE_STATE -> togglePauseState();
        }
    }

    public void updateBlockSpeed() {
        if (clearedLines > 10 && clearedLines <= 30 && blockSpeed == BlockSpeed.DEFAULT) {
            blockSpeed = BlockSpeed.FASTER;
            blockMovingTimer.fasterBlockFallingTime(this.gameDifficulty);
            scoreWeight += 10 * gameDifficulty.getWeight();
        } else if (clearedLines > 30 && clearedLines <= 80 && blockSpeed == BlockSpeed.FASTER) {
            blockSpeed = BlockSpeed.RAGE;
            blockMovingTimer.fasterBlockFallingTime(this.gameDifficulty);
            scoreWeight += 20 * gameDifficulty.getWeight();
        } else if (clearedLines > 80 && blockSpeed == BlockSpeed.RAGE) {
            blockSpeed = BlockSpeed.IMPOSSIBLE;
            blockMovingTimer.fasterBlockFallingTime(this.gameDifficulty);
            scoreWeight += 30 * gameDifficulty.getWeight();
        }
    }

    public void attacked(AttackingTetrisBlocks blocks) {
        // 기존 테트리스 블럭을 위로 올리고 blocks를 하단에 넣어주기
        tetrisGameGrid.addToAttackedBlocks(blocks);
        events.setValue(new AttackedTetrisBlocks(tetrisGameGrid.getAttackedBlocks()));
    }

    void tick(long l) {
        blockMovingTimer.setCurrentTime(l);
        collideCheckingTimer.setCurrentTime(l);
        timeLimitModeTimer.setCurrentTime(l);

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
        if (timeLimitModeTimer.isTimeOver() == true) {
            stopGame();
        }

        drawBlockIntoGrid();

        // WeightBlock이 떨어졌을 때, 해당 열을 지워줌
        if (isBlockPlaced == true && currentBlock.getId() == Block.WeightItemBlock.cellId) {
            int clearedColIndex = currentBlock.getPosition().getColIndex();
            tetrisGameGrid.clearWeightCol(clearedColIndex);
        }

        if (isBlockPlaced == true) {
            if (tetrisGameGrid.isAttackedBlockExists() == true) {
                tetrisGameGrid.insertAttackedBlocksToGrid();
                events.setValue(new InsertAttackBlocksEvent());
            }
            tetrisGameGrid.triggerLineClearItem();
            int fullRows = tetrisGameGrid.animateFullRows();

            if (fullRows > 0 || isAnimationEnded == false) {
                lineClearAnimationTimer.startLineClearAnimation(collideCheckingTimer, blockMovingTimer, feverModeTimer, false);

                if (lineClearAnimationTimer.isTimerOver() == true) {
                    blockMovingTimer.resumeTimer();
                    collideCheckingTimer.resumeTimer();
                    feverModeTimer.setResume();

                    attackingEvent(tetrisGameGrid.getAttackingBlocks(currentBlock, fullRows));

                    tetrisGameGrid.clearFullRows();
                    score += scoreWeight * fullRows;
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
                isBlockPlaced = false;
                checkGameIsOver();
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

    private void updateState(TetrisGrid newTetrisGird, CurrentBlock newNextBlock, int newScore, int newRemainingTime) {
        events.setValue(new UpdateTetrisState(newTetrisGird.getGrid(), newNextBlock, newScore, newRemainingTime));
    }

    private void attackingEvent(CellID[][] cells) {
        events.setValue(new AttackingTetrisBlocks(cells));
    }

    private void checkGameIsOver() {
        if (isGameOver() == true) {
            stopGame();
        }
    }

    private final int ROW_SIZE = 22;
    private final int COL_SIZE = 10;
    private final int FEVER_SCORE_WEIGHT = 10;
    private InputManager inputManager;
    private TetrisGrid tetrisGameGrid;
    private final BlockQueue blockQueue;
    private CurrentBlock currentBlock;
    private CurrentBlock nextBlock;
    private GameStatus gameStatus;
    private GameLevel gameDifficulty;
    private GameMode gameMode;
    private BlockSpeed blockSpeed;
    private BlockFallingTimer blockMovingTimer;
    private BlockCollideTimer collideCheckingTimer;
    private TimeLimitModeTimer timeLimitModeTimer;
    private FeverModeTimer feverModeTimer;
    private LineClearAnimationTimer lineClearAnimationTimer;
    private int score;
    private int scoreWeight;
    private int clearedLines = 0;
    private int lineCounterForItem;
    private boolean isBlockPlaced;
    private boolean isBlockCollided;
    private boolean isAnimationEnded;
    private boolean isAttacked;
    private Random random;
}
