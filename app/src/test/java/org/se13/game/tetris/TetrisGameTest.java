package org.se13.game.tetris;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.SE13Application;
import org.se13.game.block.Block;
import org.se13.game.block.CellID;
import org.se13.game.block.CurrentBlock;
import org.se13.game.grid.TetrisGrid;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.game.timer.BlockCollideTimer;
import org.se13.game.timer.BlockFallingTimer;
import org.se13.game.timer.Timer;

import static org.junit.jupiter.api.Assertions.*;

public class TetrisGameTest {
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

        assertEquals(1, grid.clearFullRows());
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
                timer1.resumeTimer(currentTime);
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

        long currentTime;

        blockFallingTimer.fasterBlockFallingTime(GameLevel.EASY); // TODO: 난이도별 테스트 추가

        while (true) {
            currentTime = System.nanoTime();

            blockFallingTimer.setCurrentTime(currentTime);

            if (blockFallingTimer.getElapsedTime() >= 1100000000) {
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
        DefaultTetrisGame defaultTetrisGame = DefaultTetrisGame.getInstance(null, null, null, GameLevel.NORMAL, GameMode.DEFAULT, true);

        defaultTetrisGame.startGame();
        defaultTetrisGame.stopGame();
        assertFalse(defaultTetrisGame.isGameOver());
        defaultTetrisGame.resetGame();

        DefaultTetrisGame resetedTetrisGame = DefaultTetrisGame.getInstance(null, null, null, GameLevel.NORMAL, GameMode.DEFAULT, true);

        assertNotSame(defaultTetrisGame, resetedTetrisGame);
        assertEquals(0, resetedTetrisGame.getScore());
        assertEquals("Normal", resetedTetrisGame.getDifficulty());
        assertFalse(resetedTetrisGame.isItemMode());

        CurrentBlock currentBlock = resetedTetrisGame.getCurrentBlock();

        assertNotNull(currentBlock);

        resetedTetrisGame.setCurrentBlock(new CurrentBlock(Block.IBlock)); // (0, 3)

        resetedTetrisGame.moveBlockDown(); // (1, 3)
        resetedTetrisGame.moveBlockDown(); // (2, 3)
        resetedTetrisGame.moveBlockDown(); // (3, 3)
        resetedTetrisGame.moveBlockLeft(); // (3, 2)
        resetedTetrisGame.moveBlockRight(); // (3, 3)

        resetedTetrisGame.rotateBlockCW();

        resetedTetrisGame.drawBlockIntoGrid();

        TetrisGrid tetrisGrid = resetedTetrisGame.getTetrisGrid();
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(3, 5));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(4, 5));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(5, 5));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(6, 5));
        resetedTetrisGame.deleteCurrentBlockFromGrid();

        resetedTetrisGame.processUserInput('a');
        resetedTetrisGame.drawBlockIntoGrid();
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(3, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(4, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(5, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(6, 4));

        resetedTetrisGame.deleteCurrentBlockFromGrid();
        assertEquals(CellID.EMPTY, tetrisGrid.getCell(3, 4));
        assertEquals(CellID.EMPTY, tetrisGrid.getCell(4, 4));
        assertEquals(CellID.EMPTY, tetrisGrid.getCell(5, 4));
        assertEquals(CellID.EMPTY, tetrisGrid.getCell(6, 4));

        resetedTetrisGame.immediateBlockPlace();
        resetedTetrisGame.drawBlockIntoGrid();
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(18, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(19, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(20, 4));
        assertEquals(CellID.IBLOCK_ID, tetrisGrid.getCell(21, 4));

        resetedTetrisGame.deleteCurrentBlockFromGrid();

        for (int i = 0; i < 9; i++) {
            tetrisGrid.setCell(0, i, CellID.IBLOCK_ID);
            tetrisGrid.setCell(1, i, CellID.IBLOCK_ID);
        }

        assertTrue(resetedTetrisGame.isGameOver());

        for (int i = 0; i < 9; i++) {
            tetrisGrid.setCell(0, i, CellID.EMPTY);
            tetrisGrid.setCell(1, i, CellID.EMPTY);
        }

        resetedTetrisGame.startGame();
        resetedTetrisGame.togglePauseState();
        assertTrue(resetedTetrisGame.isGamePaused());

        resetedTetrisGame.togglePauseState();

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


        resetedTetrisGame.update();
        assertEquals(DefaultTetrisGame.BlockSpeed.FASTER, resetedTetrisGame.getBlockSpeed());
    }
}
