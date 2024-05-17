package org.se13.game.grid;

import org.se13.game.block.*;
import org.se13.game.event.AttackingTetrisBlocks;
import org.se13.game.item.CellClearedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * abstracted tetris grid. It contains 'Cells' to display 'Blocks'
 *
 * @author binlee0903
 */
public class TetrisGrid {
    public TetrisGrid(int rowSize, int colSize) {
        this.rowSize = rowSize;
        this.colSize = colSize;
        this.gridCells = getEmptyGrid(rowSize, colSize);
        this.attackedCells = getEmptyGrid(DEFAULT_ATTACKED_ROW_SIZE, colSize);
        this.attackedCellsRowCount = 0;
        this.listeners = new ArrayList<>();
    }

    public CellID[][] getGrid() {
        return gridCells;
    }

    public void setCell(int rowIndex, int colIndex, CellID cellId) {
        gridCells[rowIndex][colIndex] = cellId;
    }

    public CellID[][] getAttackingBlocks(CurrentBlock currentBlock, int fullRowCount) {
        BlockPosition[] currentBlockShapes = currentBlock.shape();
        BlockPosition[] realCurrentBlockPositions = new BlockPosition[currentBlockShapes.length];
        List<BlockPosition> clearedCurrentBlockPositions = new LinkedList<>();

        for (int i = 0; i < realCurrentBlockPositions.length; i++) {
            realCurrentBlockPositions[i] = new BlockPosition(currentBlockShapes[i].getRowIndex() + currentBlock.getPosition().getRowIndex(),
                    currentBlockShapes[i].getColIndex() + currentBlock.getPosition().getColIndex());
        }

        CellID[][] ret = new CellID[fullRowCount][colSize];
        int rowIndex = 0;

        for (int i = rowSize - 1; i >= 0; i--) {
            if (isRowFull(i)) {
                for (int j = 0; j < 4; j++) {
                    if (realCurrentBlockPositions[j].getRowIndex() == i) {
                        clearedCurrentBlockPositions.add(new BlockPosition(rowIndex, realCurrentBlockPositions[j].getColIndex()));
                    }
                }

                rowIndex++;
            }
        }

        for (int i = 0; i < fullRowCount; i++) {
            for (int j = 0; j < colSize; j++) {
                if (isClearedBlockPosition(clearedCurrentBlockPositions, i, j) == true) {
                    ret[i][j] = CellID.EMPTY;
                } else {
                    ret[i][j] = CellID.ATTACKED_BLOCK_ID;
                }
            }
        }

        return ret;
    }

    public CellID getCell(int rowIndex, int colIndex) {
        return gridCells[rowIndex][colIndex];
    }

    /**
     * check cell's location is inside or outside
     *
     * @param rowIndex row index
     * @param colIndex column index
     * @return if cell was in, return true.
     */
    public boolean isInsideGrid(int rowIndex, int colIndex) {
        return rowIndex >= 0 && rowIndex < rowSize && colIndex >= 0 && colIndex < colSize;
    }

    /**
     * check given location's cell is empty
     *
     * @param rowIndex row index
     * @param colIndex column index
     * @return return true when cell was empty(=0)
     */
    public boolean isEmptyCell(int rowIndex, int colIndex) {
        return isInsideGrid(rowIndex, colIndex) && getCell(rowIndex, colIndex) == CellID.EMPTY;
    }

    /**
     * check given row is full
     *
     * @param rowIndex row index
     * @return return true when row was full
     */
    public boolean isRowFull(int rowIndex) {
        int ret = 0;

        for (int i = 0; i < colSize; i++) {
            if (isEmptyCell(rowIndex, i) == false) {
                ret++;
            }
        }

        return ret == this.colSize;
    }

    public boolean isRowEmpty(int rowIndex) {
        for (int i = 0; i < colSize; i++) {
            if (getCell(rowIndex, i) != CellID.EMPTY) {
                return false;
            }
        }

        return true;
    }

    public boolean isAttackedBlockExists() {
        return attackedCellsRowCount > 0;
    }

    public int getNonEmptyRowsCount() {
        int count = 0;
        for (int i = 0; i < rowSize; i++) {
            if (!isRowEmpty(i)) {
                count++;
            }
        }
        return count;
    }

    public int animateFullRows() {
        int fulledRows = 0;

        for (int i = rowSize - 1; i >= 0; i--) {
            if (isRowFull(i)) {
                fillAnimationCellIntoRow(i);
                fulledRows++;
            }
        }

        return fulledRows;
    }

    /**
     * Clears all full rows in the Tetris grid.
     *
     * @return The number of rows cleared.
     */
    public int clearFullRows() {
        int clearedRows = 0;

        for (int i = rowSize - 1; i >= 0; i--) {
            if (isRowFull(i)) {
                clearRow(i);
                moveDownRows(i);
                i++;
                clearedRows++;
            }
        }

        return clearedRows;
    }

    /**
     * clears full rows
     */
    public void clearWeightCol(int colIndex) {
        for (int i = colIndex; i < colIndex + 4; i++) {
            clearCol(i);
        }
    }

    public void registerItemListener(CellClearedListener listener) {
        this.listeners.add(listener);
    }

    public CellID[][] getAttackedBlocks() {
        return attackedCells;
    }

    private CellID[][] getEmptyGrid(int rowSize, int colSize) {
        CellID[][] grid = new CellID[rowSize][colSize];
        for (int i = 0; i < rowSize; i++) {
            Arrays.fill(grid[i], CellID.EMPTY);
        }

        return grid;
    }

    private void fillAnimationCellIntoRow(int rowIndex) {
        for (int i = 0; i < colSize; i++) {
            if (CellID.CBLOCK_ID.compareTo(getCell(rowIndex, i)) > 0) {
                setCell(rowIndex, i, CellID.CBLOCK_ID);
            }
        }
    }

    private void resetAttackedBlocks() {
        attackedCells = getEmptyGrid(DEFAULT_ATTACKED_ROW_SIZE, colSize);
        attackedCellsRowCount = 0;
    }

    /**
     * clear given index's row
     *
     * @param rowIndex row index
     */
    private void clearRow(int rowIndex) {
        for (int i = 0; i < colSize; i++) {
            final CellID cell = getCell(rowIndex, i);
            listeners.forEach((listener) -> listener.clear(cell));
            setCell(rowIndex, i, CellID.EMPTY);
        }
    }

    /**
     * move down row
     *
     * @param rowIndex row index
     */
    private void moveDownRows(int rowIndex) {
        for (int i = rowIndex; i >= 1; i--) {
            for (int j = 0; j < colSize; j++) {
                setCell(i, j, getCell(i - 1, j));
                setCell(i - 1, j, CellID.EMPTY);
            }
        }
    }

    /**
     * clear given index's row
     *
     * @param colIndex row index
     */
    private void clearCol(int colIndex) {
        for (int i = 0; i < rowSize; i++) {
            final CellID cell = getCell(i, colIndex);
            listeners.forEach((listener) -> listener.clear(cell));
            setCell(i, colIndex, CellID.EMPTY);
        }
        setCell(rowSize - 1, colIndex, CellID.WEIGHT_BLOCK_ID);
    }

    private boolean isClearedBlockPosition(List<BlockPosition> blockPositions, int rowIndex, int colIndex) {
        for (BlockPosition blockPosition : blockPositions) {
            if (blockPosition.getRowIndex() == rowIndex && blockPosition.getColIndex() == colIndex) {
                return true;
            }
        }

        return false;
    }

    public void triggerLineClearItem() {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                if (gridCells[i][j] == CellID.LINE_CLEAR_ITEM_ID) {
                    for (int k = 0; k < colSize; k++) {
                        setCell(i, k, CellID.LINE_CLEAR_ITEM_ID);
                    }

                    break;
                }
            }
        }
    }

    public void insertAttackedBlocksToGrid() {
        for (int k = 0; k < attackedCellsRowCount; k++) {
            for (int i = 0; i < rowSize - 1; i++) {
                for (int j = 0; j < colSize; j++) {
                    setCell(i, j, gridCells[i + 1][j]);
                }
            }
        }

        int k = DEFAULT_ATTACKED_ROW_SIZE - 1;
        int count = 0;

        for (int i = rowSize - 1; count < attackedCellsRowCount; i--, k--, count++) {
            if (i < 0) {
                break;
            }

            for (int j = 0; j < colSize; j++) {
                gridCells[i][j] = attackedCells[k][j];
            }
        }

        resetAttackedBlocks();
    }

    /**
     * attackedByPlayer
     * move blocks up and insert attackedByPlayer blocks on the bottom
     *
     * @param blocks attackedByPlayer blocks
     */
    public void addToAttackedBlocks(AttackingTetrisBlocks blocks) {
        CellID[][] cells = blocks.cellIDs();

        if (cells.length < 2) {
            return;
        }

        int index = 0;
        int count = 0;

        for (int i = 0; i < cells.length; i++) {
            if (attackedCellsRowCount == DEFAULT_ATTACKED_ROW_SIZE - 1) {
                break;
            }

            if (attackedCellsRowCount == 0) {
                count += cells.length;
                attackedCellsRowCount += cells.length;
                break;
            }

            for (int j = DEFAULT_ATTACKED_ROW_SIZE - attackedCellsRowCount - 1; j < DEFAULT_ATTACKED_ROW_SIZE - 1; j++) {
                for (int k = 0; k < colSize; k++) {
                    attackedCells[j - 1][k] = attackedCells[j][k];
                }
            }

            attackedCellsRowCount++;
            count++;
        }

        for (int i = DEFAULT_ATTACKED_ROW_SIZE - 1; index < count; i--) {
            for (int j = 0; j < colSize; j++) {
                attackedCells[i][j] = cells[index][j];
            }

            index++;
        }
    }

    // column size of tetris board
    private final int colSize;
    // row size of tetris board
    private final int rowSize;

    private final int DEFAULT_ATTACKED_ROW_SIZE = 11;

    /**
     * abstracted 10*22 tetris grid. originally, tetris's grid size
     * is 10*20. but I added 2 rows for block generation space.
     */
    private final CellID[][] gridCells;
    private final List<CellClearedListener> listeners;
    private CellID[][] attackedCells;
    private int attackedCellsRowCount;
}
