package org.se13.block;

/**
 * for tetris's abstracted block position, we have to separate block's position class
 * and block class
 */
public class BlockPosition {
    public BlockPosition(int rowIndex, int colIndex) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public int getRowPosition() {
        return this.rowIndex;
    }

    public int getColIndex() {
        return this.colIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    private int rowIndex;
    private int colIndex;
}
