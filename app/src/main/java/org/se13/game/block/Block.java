package org.se13.game.block;

public enum Block {
    IBlock(new int[][][]{
            {{1, 0}, {1, 1}, {1, 2}, {1, 3}},
            {{0, 2}, {1, 2}, {2, 2}, {3, 2}},
            {{2, 0}, {2, 1}, {2, 2}, {2, 3}},
            {{0, 1}, {1, 1}, {2, 1}, {3, 1}},
    }, new int[]{0, 3}, CellID.IBLOCK_ID, new BlockColor(67, 255, 255)),

    JBlock(new int[][][]{
            {{0, 0}, {1, 0}, {1, 1}, {1, 2}},
            {{0, 1}, {0, 2}, {1, 1}, {2, 1}},
            {{1, 0}, {1, 1}, {1, 2}, {2, 2}},
            {{0, 1}, {1, 1}, {2, 0}, {2, 1}},
    }, new int[]{0, 3}, CellID.JBLOCK_ID, new BlockColor(0, 1, 252)),

    LBlock(new int[][][]{
            {{0, 2}, {1, 0}, {1, 1}, {1, 2}},
            {{0, 1}, {1, 1}, {2, 1}, {2, 2}},
            {{1, 0}, {1, 1}, {1, 2}, {2, 0}},
            {{0, 0}, {0, 1}, {1, 1}, {2, 1}},
    }, new int[]{0, 3}, CellID.LBLOCK_ID, new BlockColor(254, 165, 0)),

    OBlock(new int[][][]{
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}},
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}},
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}},
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}},
    }, new int[]{0, 3}, CellID.OBLOCK_ID, new BlockColor(255, 255, 0)),

    SBlock(new int[][][]{
            {{0, 1}, {0, 2}, {1, 0}, {1, 1}},
            {{0, 1}, {1, 1}, {1, 2}, {2, 2}},
            {{1, 1}, {1, 2}, {2, 0}, {2, 1}},
            {{0, 0}, {1, 0}, {1, 1}, {2, 1}},
    }, new int[]{0, 3}, CellID.SBLOCK_ID, new BlockColor(0, 255, 1)),

    TBlock(new int[][][]{
            {{0, 1}, {1, 0}, {1, 1}, {1, 2}},
            {{0, 1}, {1, 1}, {1, 2}, {2, 1}},
            {{1, 0}, {1, 1}, {1, 2}, {2, 1}},
            {{0, 1}, {1, 0}, {1, 1}, {2, 1}},
    }, new int[]{0, 3}, CellID.TBLOCK_ID, new BlockColor(170, 0, 255)),

    ZBlock(new int[][][]{
            {{0, 0}, {0, 1}, {1, 1}, {1, 2}},
            {{0, 2}, {1, 1}, {1, 2}, {2, 1}},
            {{1, 0}, {1, 1}, {2, 1}, {2, 2}},
            {{0, 1}, {1, 0}, {1, 1}, {2, 0}},
    }, new int[]{0, 3}, CellID.ZBLOCK_ID, new BlockColor(254,0, 0));

    Block(int[][][] positions, int[] offset, CellID id, BlockColor blockColor) {
        int row = positions.length;

        cellId = id;
        cells = new BlockPosition[row][];
        startOffset = new BlockPosition(offset[0], offset[1]);
        this.blockColor = blockColor;

        for (int r = 0; r < row; r++) {
            int col = positions[r].length;
            cells[r] = new BlockPosition[col];
            for (int c = 0; c < col; c++) {
                cells[r][c] = new BlockPosition(positions[r][c][0], positions[r][c][1]);
            }
        }
    }

    public final CellID cellId;
    public final BlockPosition[][] cells;
    public final BlockPosition startOffset;
    public final BlockColor blockColor;

    public BlockPosition[] shape(int rotate) {
        return cells[rotate % cells.length];
    }
}
