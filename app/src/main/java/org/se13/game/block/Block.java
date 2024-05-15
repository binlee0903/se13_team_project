package org.se13.game.block;

import javafx.scene.paint.Color;
import org.se13.sqlite.config.ConfigRepositoryImpl;

public enum Block {
    IBlock(new int[][][]{
            {{1, 0}, {1, 1}, {1, 2}, {1, 3}},
            {{0, 2}, {1, 2}, {2, 2}, {3, 2}},
            {{2, 3}, {2, 2}, {2, 1}, {2, 0}},
            {{3, 1}, {2, 1}, {1, 1}, {0, 1}},
    }, new int[]{0, 3}, CellID.IBLOCK_ID),

    JBlock(new int[][][]{
            {{0, 0}, {1, 0}, {1, 1}, {1, 2}},
            {{0, 2}, {0, 1}, {1, 1}, {2, 1}},
            {{2, 2}, {1, 2}, {1, 1}, {1, 0}},
            {{2, 0}, {2, 1}, {1, 1}, {0, 1}},
    }, new int[]{0, 3}, CellID.JBLOCK_ID),

    LBlock(new int[][][]{
            {{0, 2}, {1, 2}, {1, 1}, {1, 0}},
            {{2, 2}, {2, 1}, {1, 1}, {0, 1}},
            {{2, 0}, {1, 0}, {1, 1}, {1, 2}},
            {{0, 0}, {0, 1}, {1, 1}, {2, 1}},
    }, new int[]{0, 3}, CellID.LBLOCK_ID),

    OBlock(new int[][][]{
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}},
            {{0, 1}, {1, 1}, {1, 0}, {0, 0}},
            {{1, 1}, {1, 0}, {0, 0}, {0, 1}},
            {{1, 0}, {0, 0}, {0, 1}, {1, 1}},
    }, new int[]{0, 3}, CellID.OBLOCK_ID),

    SBlock(new int[][][]{
            {{0, 2}, {0, 1}, {1, 1}, {1, 0}},
            {{2, 2}, {1, 2}, {1, 1}, {0, 1}},
            {{2, 0}, {2, 1}, {1, 1}, {1, 2}},
            {{0, 0}, {1, 0}, {1, 1}, {2, 1}},
    }, new int[]{0, 3}, CellID.SBLOCK_ID),

    TBlock(new int[][][]{
            {{0, 1}, {1, 0}, {1, 1}, {1, 2}},
            {{1, 2}, {0, 1}, {1, 1}, {2, 1}},
            {{2, 1}, {1, 2}, {1, 1}, {1, 0}},
            {{1, 0}, {2, 1}, {1, 1}, {0, 1}},
    }, new int[]{0, 3}, CellID.TBLOCK_ID),

    ZBlock(new int[][][]{
            {{0, 0}, {0, 1}, {1, 1}, {1, 2}},
            {{0, 2}, {1, 2}, {1, 1}, {2, 1}},
            {{2, 2}, {2, 1}, {1, 1}, {1, 0}},
            {{2, 0}, {1, 0}, {1, 1}, {0, 1}},
    }, new int[]{0, 3}, CellID.ZBLOCK_ID),

    WeightItemBlock(new int[][][]{
            {{0, 1}, {0, 2}, {1, 0}, {1, 1}, {1, 2}, {1, 3}},
    }, new int[]{0, 3}, CellID.WEIGHT_BLOCK_ID),

    AttackedBlock(new int[][][]{
            {{0, 0}},
    }, new int[]{0, 1}, CellID.ATTACKED_BLOCK_ID);

    Block(int[][][] positions, int[] offset, CellID cellID) {
        int row = positions.length;

        cellId = cellID;
        cells = new BlockPosition[row][];
        startOffset = new BlockPosition(offset[0], offset[1]);
        blockColorManager = new BlockColor(new ConfigRepositoryImpl(0));
        this.blockColor = blockColorManager.getBlockColor(cellID);

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
    public Color blockColor;
    private BlockColor blockColorManager;

    public BlockPosition[] shape(int rotate) {
        return cells[rotate % cells.length];
    }
}
