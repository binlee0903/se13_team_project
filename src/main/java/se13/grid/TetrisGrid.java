package se13.grid;

/**
 * abstracted tetris grid. It contains 'Cells' to display 'Blocks'
 * @author binlee0903
 */
public class TetrisGrid {
    public TetrisGrid() {
        this.gridCells = new int[colSize][rowSize];
    }

    /**
     * check cell's location is inside or outside
     * @param rowIndex row index
     * @param colIndex column index
     * @return if cell was in, return true.
     */
    public boolean isInsideGrid(int rowIndex, int colIndex)
    {
        // TODO: check index is in, or not
        return false;
    }

    /**
     * check given location's cell is empty
     * @param rowIndex row index
     * @param colIndex column index
     * @return return true when cell was empty(=0)
     */
    public boolean isEmptyCell(int rowIndex, int colIndex)
    {
        // TODO: check given cell's location is empty space(=0)
        return false;
    }

    /**
     * check given row is full
     * @param rowIndex row index
     * @return return true when row was full
     */
    public boolean isRowFull(int rowIndex) {
        // TODO: check row is full
        return false;
    }

    /**
     * clears full rows
     * @return cleared row count
     */
    public int clearFullRows() {
        // TODO: write function
        return 0;
    }

    /**
     * clear given index's row
     * @param rowIndex row index
     */
    private void clearRow(int rowIndex) {
        // TODO: clear row
    }

    /**
     * move down row by 1 cell size
     * @param rowIndex row index
     */
    private void moveDownRow(int rowIndex) {
        // TODO: write function
    }

    /**
     * column size of tetris board
     */
    private final int colSize = 10;

    /**
     * row size of tetris board
     */
    private final int rowSize = 22;

    /**
     * abstracted 10*22 tetris grid. originally, tetris's grid size
     * is 10*20. but I added 2 rows for block generation space.
     */
    private int[][] gridCells;
}
