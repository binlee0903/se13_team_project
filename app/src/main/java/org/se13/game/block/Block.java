package org.se13.game.block;

public enum Block {
    IBlock(new int[][][]{
            {{1, 0}, {1, 1}, {1, 2}, {1, 3}},
            {{0, 2}, {1, 2}, {2, 2}, {3, 2}},
            {{2, 0}, {2, 1}, {2, 2}, {2, 3}},
            {{0, 1}, {1, 1}, {2, 1}, {3, 1}},
    }, new int[]{-1, 3}),

    JBlock(new int[][][]{
            {{0, 0}, {1, 0}, {1, 1}, {1, 2}},
            {{0, 1}, {0, 2}, {1, 1}, {2, 1}},
            {{1, 0}, {1, 1}, {1, 2}, {2, 2}},
            {{0, 1}, {1, 1}, {2, 0}, {2, 1}},
    }, new int[]{0, 3}),

    LBlock(new int[][][]{
            {{0, 2}, {1, 0}, {1, 1}, {1, 2}},
            {{0, 1}, {1, 1}, {2, 1}, {2, 2}},
            {{1, 0}, {1, 1}, {1, 2}, {2, 0}},
            {{0, 0}, {0, 1}, {1, 1}, {2, 1}},
    }, new int[]{0, 3}),

    OBlock(new int[][][]{
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}}
    }, new int[]{0, 4}),

    SBlock(new int[][][]{
            {{0, 1}, {0, 2}, {1, 0}, {1, 1}},
            {{0, 1}, {1, 1}, {1, 2}, {2, 2}},
            {{1, 1}, {1, 2}, {2, 0}, {2, 1}},
            {{0, 0}, {1, 0}, {1, 1}, {2, 1}},
    }, new int[]{0, 3}),

    TBlock(new int[][][]{
            {{0, 1}, {1, 0}, {1, 1}, {1, 2}},
            {{0, 1}, {1, 1}, {1, 2}, {2, 1}},
            {{1, 0}, {1, 1}, {1, 2}, {2, 1}},
            {{0, 1}, {1, 0}, {1, 1}, {2, 1}},
    }, new int[]{0, 3}),

    ZBlock(new int[][][]{
            {{0, 0}, {0, 1}, {1, 1}, {1, 2}},
            {{0, 2}, {1, 1}, {1, 2}, {2, 1}},
            {{1, 0}, {1, 1}, {2, 1}, {2, 2}},
            {{0, 1}, {1, 0}, {1, 1}, {2, 0}},
    }, new int[]{0, 3});

    Block(int[][][] positions, int[] offset) {
        int row = positions.length;

        cells = new BlockPosition[row][];
        startOffset = new BlockPosition(offset[0], offset[1]);

        for (int r = 0; r < row; r++) {
            int col = positions[r].length;
            cells[r] = new BlockPosition[col];
            for (int c = 0; c < col; c++) {
                cells[r][c] = new BlockPosition(positions[r][c][0], positions[r][c][1]);
            }
        }
    }

    enum BlockRotationStatus {
        NONE,
    }

    final BlockPosition[][] cells;
    final BlockPosition startOffset;

    private BlockRotationStatus rotationStatus;
    private BlockPosition position;
}
