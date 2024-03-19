package org.se13.game.block;

public class CurrentBlock {

    private final Block block;
    private BlockPosition position;
    private int rotate;

    public CurrentBlock(Block block) {
        this.block = block;
        this.position = block.startOffset;
        this.rotate = 0;
    }

    public BlockPosition[] shape() {
        return block.shape(rotate);
    }

    public void rotateCW() {
        rotate = (rotate + 1) % block.cells.length;
    }

    public void rotateCCW() {
        if (rotate == 0) {
            rotate = block.cells.length - 1;
        } else {
            rotate--;
        }
    }

    public void move(int rows, int columns) {
        position.setRowIndex(rows);
        position.setColIndex(columns);
    }

    public void reset() {
        rotate = 0;
        position = block.startOffset;
    }

    public int getId() { return block.blockId; }
}
