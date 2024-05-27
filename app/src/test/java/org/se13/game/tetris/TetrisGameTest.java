package org.se13.game.tetris;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.SE13Application;
import org.se13.game.action.TetrisAction;
import org.se13.game.block.Block;
import org.se13.game.block.BlockPosition;
import org.se13.game.block.CellID;
import org.se13.game.block.CurrentBlock;
import org.se13.game.event.AttackingTetrisBlocks;
import org.se13.game.grid.TetrisGrid;
import org.se13.game.item.*;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.timer.*;
import org.se13.server.TetrisActionHandler;
import org.se13.server.TetrisClient;
import org.se13.server.TetrisServer;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TetrisGameTest {

    TetrisGame tetrisGame;

    @BeforeEach
    void before() {
        tetrisGame = new TetrisGame(GameLevel.NORMAL, GameMode.ITEM, new TetrisServer() {
            @Override
            public void responseGameOver(int score, boolean isItemMode, String difficulty) {

            }

            @Override
            public TetrisActionHandler connect(TetrisClient client) {
                return null;
            }

            @Override
            public void disconnect(TetrisClient client) {

            }
        });
    }

    @Test
    @DisplayName("테트리스 블록 이동 테스트")
    void blockMoveTest() {
        CurrentBlock block = new CurrentBlock(Block.IBlock);

        assertEquals(block.getPosition().getRowIndex(), 0);
        block.move(1, 0);
        assertEquals(block.getPosition().getRowIndex(), 1);
        assertEquals(block.getPosition().getColIndex(), 3);
        block.move(0, 1);
        assertEquals(block.getPosition().getColIndex(), 4);
    }

    @Test
    @DisplayName("테트리스 블록 회전 테스트")
    void blockSpinTest() {
        CurrentBlock block = new CurrentBlock(Block.IBlock);

        assertEquals(0, block.getRotateState());

        block.rotateCW();
        assertEquals(1, block.getRotateState());

        block.rotateCW();
        block.rotateCW();
        block.rotateCW();
        assertEquals(0, block.getRotateState());
    }

    @Test
    @DisplayName("블록위치 초기화 테스트")
    void blockResetTest() {
        CurrentBlock block = new CurrentBlock(Block.IBlock);
        block.rotateCW();
        block.rotateCW();
        block.move(5,5);
        block.reset();

        assertEquals(0, block.getPosition().getRowIndex());
        assertEquals(3, block.getPosition().getColIndex());
        assertEquals(0, block.getRotateState());
    }

    @Test
    @DisplayName("테트리스 그리드 기능 테스트")
    void tetrisGridTest() {
        TetrisGrid grid = new TetrisGrid(20, 10);
        assertFalse(grid.isInsideGrid(-1, 0));
        assertTrue(grid.isInsideGrid(0, 1));

        assertEquals(CellID.EMPTY, grid.getCell(8, 8));

        grid.setCell(8, 8, CellID.IBLOCK_ID);
        assertEquals(CellID.IBLOCK_ID, grid.getCell(8, 8));

        assertFalse(grid.isEmptyCell(8,8));

        for (int i = 0; i < 10; i++) {
            grid.setCell(4, i, CellID.IBLOCK_ID);
        }

        assertTrue(grid.isRowFull(4));
        assertFalse(grid.isRowEmpty(4));

        int count = grid.animateFullRows();

        assertEquals(1, count);
        assertEquals(1, grid.clearFullRows());

        grid.clearWeightCol(0);
        grid.clearWeightCol(4);
        grid.clearWeightCol(6);
        assertFalse(grid.isRowEmpty(19));
    }

    @Test
    @DisplayName("AttackingBlocks Test")
    void testGetAttackingBlocks() {
        TetrisGrid tetrisGrid = new TetrisGrid(20, 10);
        CurrentBlock currentBlock = new CurrentBlock(Block.OBlock);
        CellID[][] attackingBlocks = new CellID[10][10];

        for (int i = 0; i < attackingBlocks.length; i++) {
            for (int j = 0; j < attackingBlocks[i].length; j++) {
                attackingBlocks[i][j] = CellID.ATTACKED_BLOCK_ID;
            }

            if (i == 1) {
                break;
            }
        }

        AttackingTetrisBlocks attackingTetrisBlocks = new AttackingTetrisBlocks(attackingBlocks);
        tetrisGrid.addToAttackedBlocks(attackingTetrisBlocks);

        CellID[][] attackedBlocks = tetrisGrid.getAttackedBlocks();
        boolean isSuccess = false;

        for (int i = 10; i > 8; i--) {
            for (int j = 0; j < attackedBlocks[i].length; j++) {
                if (attackedBlocks[i][j] != CellID.ATTACKED_BLOCK_ID) {
                    isSuccess = false;
                    break;
                } else {
                    isSuccess = true;
                }
            }
        }

        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("BlockCollideTimer")
    void timerTest() {
        BlockCollideTimer blockCollideTimer = new BlockCollideTimer(100);
        boolean isCollided = false;
        blockCollideTimer.reset(0);
        blockCollideTimer.setCurrentTime(0);

        assertEquals(0, blockCollideTimer.getElapsedTime());

        long currentTime;

        while (true) {
            currentTime = System.nanoTime();

            if (isCollided == false){
                isCollided = true;
                blockCollideTimer = new BlockCollideTimer(currentTime);
                blockCollideTimer.setFirstBlockCollideTime(currentTime);
            } else {
                blockCollideTimer.setCurrentTime(currentTime);
            }

            if (blockCollideTimer.getElapsedTime() > 2000000000) {
                break;
            }
        }

        assertTrue(blockCollideTimer.isTimerStarted());
        assertTrue(blockCollideTimer.getElapsedTime() >= 2000000000);
        assertTrue(blockCollideTimer.isBlockPlaceTimeEnded());
    }

    @Test
    @DisplayName("LineClearAnimationTimer 테스트")
    void lineClearAnimationTimerTest() {
        LineClearAnimationTimer lineClearAnimationTimer = new LineClearAnimationTimer(System.nanoTime());

        lineClearAnimationTimer.startLineClearAnimation(null, null, null, true);

        while (lineClearAnimationTimer.isTimerOver() == true) {
            ;
        }

        lineClearAnimationTimer.resetFlags();

        assertTrue(true);
    }

    @Test
    @DisplayName("Timer stop, resume, reset 테스트")
    void timerStopTest() {
        Timer timer1 = new Timer(System.nanoTime());
        Timer timer2 = new Timer(System.nanoTime());
        boolean isStopped = false;
        long currentTime;
        long originalElapsedTime = 0;

        while (true) {
            currentTime = System.nanoTime();
            timer1.setCurrentTime(currentTime);
            timer2.setCurrentTime(currentTime);

            if (isStopped == false) {
                isStopped = true;
                timer1.pauseTimer();
                originalElapsedTime = timer1.getElapsedTime();
            }

            if (timer2.getElapsedTime() > 2000000000) {
                timer1.resumeTimer();
                break;
            }
        }

        assertEquals(originalElapsedTime, timer1.getElapsedTime());

        timer1.reset(0);
        timer1.setCurrentTime(0);

        assertEquals(0, timer1.getElapsedTime());
    }

    @Test
    @DisplayName("BlockFallingTimer 테스트")
    void fallingTimerTest() {
        BlockFallingTimer blockFallingTimer = new BlockFallingTimer(System.nanoTime());

        blockFallingTimer.fasterBlockFallingTime(GameLevel.NORMAL);

        while (true) {
            blockFallingTimer.setCurrentTime(System.nanoTime());

            if (blockFallingTimer.getElapsedTime() >= 2100000000L) {
                assertTrue(blockFallingTimer.isBlockFallingTimeHasGone());
                break;
            } else {
                assertFalse(blockFallingTimer.isBlockFallingTimeHasGone());
            }
        }

        blockFallingTimer.reset(System.nanoTime());
        blockFallingTimer.restoreBlockFallingTime();
        blockFallingTimer.fasterBlockFallingTime(GameLevel.EASY);

        while (true) {

            blockFallingTimer.setCurrentTime(System.nanoTime());

            if (blockFallingTimer.getElapsedTime() >= 2280000000L) {
                assertTrue(blockFallingTimer.isBlockFallingTimeHasGone());
                break;
            } else {
                assertFalse(blockFallingTimer.isBlockFallingTimeHasGone());
            }
        }

        blockFallingTimer.reset(System.nanoTime());
        blockFallingTimer.restoreBlockFallingTime();
        blockFallingTimer.fasterBlockFallingTime(GameLevel.HARD);

        while (true) {

            blockFallingTimer.setCurrentTime(System.nanoTime());

            if (blockFallingTimer.getElapsedTime() >= 1920000000L) {
                assertTrue(blockFallingTimer.isBlockFallingTimeHasGone());
                break;
            } else {
                assertFalse(blockFallingTimer.isBlockFallingTimeHasGone());
            }
        }
    }

    @Test
    @DisplayName("전반적인 테트리스 게임 테스트")
    void tetrisGameTest() {
        assertEquals(0, tetrisGame.getScore());
        assertEquals("Normal", tetrisGame.getDifficulty());
        assertTrue(tetrisGame.isItemMode());

        CurrentBlock currentBlock = tetrisGame.getCurrentBlock();

        assertNotNull(currentBlock);

        tetrisGame.setCurrentBlock(new CurrentBlock(Block.IBlock)); // (0, 3)

        tetrisGame.moveBlockDown(); // (1, 3)
        tetrisGame.moveBlockDown(); // (2, 3)
        tetrisGame.moveBlockDown(); // (3, 3)
        tetrisGame.moveBlockLeft(); // (3, 2)
        tetrisGame.moveBlockRight(); // (3, 3)

        tetrisGame.rotateBlockCW();

        tetrisGame.drawBlockIntoGrid();

        TetrisGrid tetrisGrid = tetrisGame.getTetrisGrid();
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(3, 5));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(4, 5));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(5, 5));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(6, 5));
        tetrisGame.deleteCurrentBlockFromGrid();

        tetrisGame.processUserInput(TetrisAction.MOVE_BLOCK_LEFT);
        tetrisGame.drawBlockIntoGrid();
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(3, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(4, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(5, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(6, 4));

        tetrisGame.deleteCurrentBlockFromGrid();
        assertEquals(CellID.EMPTY, tetrisGrid.getCell(3, 4));
        assertEquals(CellID.EMPTY, tetrisGrid.getCell(4, 4));
        assertEquals(CellID.EMPTY, tetrisGrid.getCell(5, 4));
        assertEquals(CellID.EMPTY, tetrisGrid.getCell(6, 4));

        tetrisGame.immediateBlockPlace();
        tetrisGame.drawBlockIntoGrid();
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(18, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(19, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(20, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(21, 4));

        tetrisGame.deleteCurrentBlockFromGrid();

        for (int i = 0; i < 9; i++) {
            tetrisGrid.setCell(0, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(1, i, CellID.IBLOCK_ID);
        }

        assertTrue(tetrisGame.isGameOver());

        for (int i = 0; i < 9; i++) {
            tetrisGrid.setCell(0, i, CellID.EMPTY);
            tetrisGrid.setCell(1, i, CellID.EMPTY);
        }

        tetrisGame.startGame();
        tetrisGame.togglePauseState();
        assertTrue(tetrisGame.isGamePaused());

        tetrisGame.togglePauseState();

        for (int i = 0; i < 10; i++) {
            tetrisGrid.setCell(10, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(11, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(12, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(13, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(14, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(15, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(16, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(17, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(18, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(19, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(20, i, CellID.IBLOCK_ID);
        }


        tetrisGame.setBlockPlaced(true);
        tetrisGame.pulse(System.nanoTime());
        tetrisGame.update();
        assertEquals(TetrisGame.BlockSpeed.DEFAULT, tetrisGame.getBlockSpeed());

        while (tetrisGame.isAnimationTimerEnded() == true) {
            tetrisGame.update();
        }

        tetrisGame.update();

        tetrisGame.setClearedLines(11);
        tetrisGame.updateBlockSpeed();

        assertEquals(TetrisGame.BlockSpeed.FASTER, tetrisGame.getBlockSpeed());

        tetrisGame.setClearedLines(31);
        tetrisGame.updateBlockSpeed();

        assertEquals(TetrisGame.BlockSpeed.RAGE, tetrisGame.getBlockSpeed());

        tetrisGame.setClearedLines(101);
        tetrisGame.updateBlockSpeed();

        assertEquals(TetrisGame.BlockSpeed.IMPOSSIBLE, tetrisGame.getBlockSpeed());
    }

    @Test
    @DisplayName("Item Test")
    void itemTest() {
        Random rand = new Random();

        AllClearItem allClearItem = new AllClearItem(rand, Block.IBlock);
        FallingTimeResetItem fallingTimeResetItem = new FallingTimeResetItem(rand, Block.IBlock);
        FeverItem feverItem = new FeverItem(rand, Block.IBlock);
        LineClearItem clearItem = new LineClearItem(rand, Block.IBlock);
        WeightItem weightItem = new WeightItem(rand, Block.IBlock);

        assertSame(allClearItem.getId(), CellID.ALL_CLEAR_ITEM_ID);
        assertSame(fallingTimeResetItem.getId(), CellID.RESET_ITEM_ID);
        assertSame(feverItem.getId(), CellID.FEVER_ITEM_ID);
        assertSame(clearItem.getId(), CellID.LINE_CLEAR_ITEM_ID);
        assertSame(weightItem.getId(), CellID.WEIGHT_ITEM_ID);
    }

    @Test
    @DisplayName("피버_모드_아이템_발동_테스트")
    void feverItemTest() throws InterruptedException {
        FeverModeTimer.DEFAULT_DURATION = 500000000L;

        tetrisGame.startGame();
        for (int i = 0; i < 10; i++) {
            tetrisGame
                .getTetrisGrid()
                .setCell(0, i, CellID.IBLOCK_ID);
        }

        tetrisGame
            .getTetrisGrid()
            .setCell(0, 0, CellID.FEVER_ITEM_ID);

        int currentWeight = tetrisGame.getScoreWeight();
        tetrisGame.getTetrisGrid().clearFullRows();
        int nextWeight = tetrisGame.getScoreWeight();

        assertTrue(currentWeight < nextWeight);

        Thread.sleep(500);

        tetrisGame.pulse(System.nanoTime());
        nextWeight = tetrisGame.getScoreWeight();
        assertEquals(currentWeight, nextWeight);
    }

    @Test
    @DisplayName("속도_리셋_아이템_발동_테스트")
    void resetSpeedItemTest() {
        tetrisGame.startGame();
        tetrisGame.pulse(System.nanoTime());

        for (int i = 0; i < 10; i++) {
            tetrisGame
                .getTetrisGrid()
                .setCell(0, i, CellID.IBLOCK_ID);
        }

        tetrisGame
            .getTetrisGrid()
            .setCell(0, 0, CellID.RESET_ITEM_ID);

        TetrisGame.BlockSpeed currentSpeed = tetrisGame.getBlockSpeed();
        tetrisGame.getTetrisGrid().clearFullRows();
        TetrisGame.BlockSpeed nextSpeed = tetrisGame.getBlockSpeed();

        assertEquals(currentSpeed, nextSpeed);
    }

    @Test
    @DisplayName("보드_리셋_아이템_발동_테스트")
    void resetBoardItemTest() {
        tetrisGame.startGame();
        tetrisGame.pulse(System.nanoTime());

        for (int i = 0; i < 10; i++) {
            tetrisGame
                .getTetrisGrid()
                .setCell(0, i, CellID.IBLOCK_ID);
        }

        tetrisGame
            .getTetrisGrid()
            .setCell(0, 0, CellID.ALL_CLEAR_ITEM_ID);

        tetrisGame
            .getTetrisGrid()
            .setCell(1, 1, CellID.IBLOCK_ID);

        assertEquals(CellID.IBLOCK_ID, tetrisGame.getTetrisGrid().getCell(1, 1));
        tetrisGame.getTetrisGrid().clearFullRows();
        assertEquals(CellID.EMPTY, tetrisGame.getTetrisGrid().getCell(1, 1));
    }

    @Test
    @DisplayName("아이템이_잘_생성되는지_테스트")
    void createItemBlockTest() {
        for (int i = 0; i < 100; i++) {
            CurrentBlock block = tetrisGame.nextItemBlock();
            assertInstanceOf(TetrisItem.class, block.getItem());
        }
    }
}
