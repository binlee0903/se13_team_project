package org.se13.game.grid;

import org.se13.game.block.CellID;
import org.se13.game.item.CellClearedListener;

import java.util.ArrayList;
import java.util.Arrays;
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
        this.listeners = new ArrayList<>();
    }

    public void setCell(int rowIndex, int colIndex, CellID cellId) {
        gridCells[rowIndex][colIndex] = cellId;
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
     * clears full rows
     *
     * @return cleared row count
     */
    public int clearFullRows() {
        int cleared = 0;

        for (int i = rowSize - 1; i >= 0; i--) {
            if (isRowFull(i)) {
                clearRow(i);
                moveDownRows(i);
                i++;
                cleared++;
            }
        }

        return cleared;
    }

    /**
     * clears full rows
     *
     */
    public void clearWeightCol(int colIndex) {
        for (int i = colIndex; i < colIndex + 4; i++) {
            clearCol(i);
        }
    }

    public void registerItemListener(CellClearedListener listener) {
        this.listeners.add(listener);
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

    // column size of tetris board
    private final int colSize;
    // row size of tetris board
    private final int rowSize;

    /**
     * abstracted 10*22 tetris grid. originally, tetris's grid size
     * is 10*20. but I added 2 rows for block generation space.
     */
    private final CellID[][] gridCells;
    private final List<CellClearedListener> listeners;
}
