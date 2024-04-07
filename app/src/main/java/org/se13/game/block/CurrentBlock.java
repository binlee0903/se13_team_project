package org.se13.game.block;

public class CurrentBlock {

    private final Block block;
    private BlockPosition position;
    private int rotate;

    public CurrentBlock(Block block) {
        this.block = block;
        this.position = new BlockPosition(block.startOffset);
        this.rotate = 0;
    }

    public BlockPosition[] shape() {
        return block.shape(rotate);
    }

    public BlockPosition getPosition() {
        return this.position;
    }

    public BlockColor getColor() { return block.blockColor; }

    public void rotateCW() {
        rotate = (rotate + 1) % 4;
    }

    public void rotateCCW() {
        if (rotate == 0) {
            rotate = 3;
        } else {
            rotate--;
        }
    }

    public void move(int rows, int columns) {
        int newRowIndex = position.getRowIndex() + rows;
        int newColIndex = position.getColIndex() + columns;

        position.setRowIndex(newRowIndex);
        position.setColIndex(newColIndex);
    }

    public void reset() {
        rotate = 0;
        position = block.startOffset;
    }

    public int getId() { return block.blockId; }
    public int getRotateState() {
        return rotate;
    }
}
