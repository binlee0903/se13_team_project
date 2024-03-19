package org.se13.game.grid;

/**
 * abstracted tetris grid. It contains 'Cells' to display 'Blocks'
 * @author binlee0903
 */
public class TetrisGrid {

    // column size of tetris board
    private final int colSize;
    // row size of tetris board
    private final int rowSize;

    /**
     * abstracted 10*22 tetris grid. originally, tetris's grid size
     * is 10*20. but I added 2 rows for block generation space.
     */
    private final int[][] gridCells;

    public TetrisGrid(int colSize, int rowSize) {
        this.colSize = colSize;
        this.rowSize = rowSize;
        this.gridCells = new int[rowSize][colSize];
    }

    /**
     * check cell's location is inside or outside
     * @param rowIndex row index
     * @param colIndex column index
     * @return if cell was in, return true.
     */
    public boolean isInsideGrid(int rowIndex, int colIndex)
    {
        return rowIndex >= 0 && rowIndex <= rowSize && colIndex >= 0 && colIndex <= colSize;
    }

    /**
     * check given location's cell is empty
     * @param rowIndex row index
     * @param colIndex column index
     * @return return true when cell was empty(=0)
     */
    public boolean isEmptyCell(int rowIndex, int colIndex)
    {
        return gridCells[rowIndex][colIndex] == 0;
    }

    /**
     * check given row is full
     * @param rowIndex row index
     * @return return true when row was full
     */
    public boolean isRowFull(int rowIndex) {
        for (int colIndex = 0; colIndex < colSize; colIndex++) {
            if (isEmptyCell(rowIndex, colIndex)) {
                return false;
            }
        }

        return true;
    }

    /**
     * clears full rows
     * @return cleared row count
     */
    public int clearFullRows() {
        int count = 0;
        for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
            if (clearRow(rowIndex)) {
                count++;
            }
        }
        return count;
    }

    /**
     * clear given index's row
     * @param rowIndex row index
     * @return true cleared row
     */
    private boolean clearRow(int rowIndex) {
        if (isRowFull(rowIndex)) {
            for (int colIndex = 0; colIndex < colSize; colIndex++) {
                gridCells[rowIndex][colIndex] = 0;
            }

            return true;
        }

        return false;
    }

    /**
     * move down row by 1 cell size
     * @param rowIndex row index
     */
    private void moveDownRow(int rowIndex) {
        // TODO: write function
    }
}
