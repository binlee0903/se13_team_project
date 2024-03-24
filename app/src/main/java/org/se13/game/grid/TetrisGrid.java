package org.se13.game.grid;

/**
 * abstracted tetris grid. It contains 'Cells' to display 'Blocks'
 *
 * @author binlee0903
 */
public class TetrisGrid {
    public TetrisGrid(int rowSize, int colSize) {
        this.rowSize = rowSize;
        this.colSize = colSize;
        this.gridCells = new int[rowSize][colSize];
    }

    public void setCell(int rowIndex, int colIndex, int blockId) {
        gridCells[rowIndex][colIndex] = blockId;
    }

    public int getCell(int rowIndex, int colIndex) {
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
        return isInsideGrid(rowIndex, colIndex) && getCell(rowIndex, colIndex) == 0;
    }

    /**
     * check given row is full
     *
     * @param rowIndex row index
     * @return return true when row was full
     */
    public boolean isRowFull(int rowIndex) {
        int i = 0;

        for (int colIndex = 0; colIndex < colSize; colIndex++) {
            if (isEmptyCell(rowIndex, colIndex) == false) {
                i++;
            }
        }

        return i == this.rowSize;
    }

    public boolean isRowEmpty(int rowIndex) {
        for (int colIndex = 0; colIndex < colSize; colIndex++) {
            if (getCell(rowIndex, colIndex) != 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * clears full rows
     *
     * @return cleared row count
     */
    public int clearFullRows() {
        int cleared = 0;

        for (int rowIndex = rowSize - 1; rowIndex >= 0; rowIndex--) {
            if (isRowFull(rowIndex)) {
                clearRow(rowIndex);
                moveDownRow(rowIndex, 1);
                cleared++;
            }
        }

        return cleared;
    }

    /**
     * clear given index's row
     *
     * @param rowIndex row index
     */
    private void clearRow(int rowIndex) {
        for (int colIndex = 0; colIndex < colSize; colIndex++) {
            setCell(rowIndex, colIndex, 0);
        }
    }

    /**
     * move down row
     *
     * @param rowIndex row index
     */
    private void moveDownRow(int rowIndex, int cleared) {
        for (int colIndex = 0; colIndex < colSize; colIndex++) {
            setCell(rowIndex + cleared, colIndex, getCell(rowIndex, colIndex));
            setCell(rowIndex, colIndex, 0);
        }
    }

    // column size of tetris board
    private final int colSize;
    // row size of tetris board
    private final int rowSize;

    /**
     * abstracted 10*22 tetris grid. originally, tetris's grid size
     * is 10*20. but I added 2 rows for block generation space.
     */
    private final int[][] gridCells;
}
