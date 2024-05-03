package org.se13.game.block;

import javafx.scene.paint.Color;
import org.se13.sqlite.config.ConfigRepositoryImpl;

import java.util.Map;
import java.util.Objects;

public enum Block {
    IBlock(new int[][][]{
            {{1, 0}, {1, 1}, {1, 2}, {1, 3}},
            {{0, 2}, {1, 2}, {2, 2}, {3, 2}},
            {{2, 3}, {2, 2}, {2, 1}, {2, 0}},
            {{3, 1}, {2, 1}, {1, 1}, {0, 1}},
    }, new int[]{0, 3}, CellID.IBLOCK_ID, new BlockColor(
            Color.rgb(0, 0, 255),
            Color.rgb(0, 0, 255),
            Color.rgb(255, 0, 0))),

    JBlock(new int[][][]{
            {{0, 0}, {1, 0}, {1, 1}, {1, 2}},
            {{0, 2}, {0, 1}, {1, 1}, {2, 1}},
            {{2, 2}, {1, 2}, {1, 1}, {1, 0}},
            {{2, 0}, {2, 1}, {1, 1}, {0, 1}},
    }, new int[]{0, 3}, CellID.JBLOCK_ID, new BlockColor(
            Color.rgb(255, 0, 0),
            Color.rgb(255, 192, 203),
            Color.rgb(0, 255, 0))),

    LBlock(new int[][][]{
            {{0, 2}, {1, 2}, {1, 1}, {1, 0}},
            {{2, 2}, {2, 1}, {1, 1}, {0, 1}},
            {{2, 0}, {1, 0}, {1, 1}, {1, 2}},
            {{0, 0}, {0, 1}, {1, 1}, {2, 1}},
    }, new int[]{0, 3}, CellID.LBLOCK_ID, new BlockColor(
            Color.rgb(0, 255, 0),
            Color.rgb(0, 128, 128),
            Color.rgb(128, 0, 128))),

    OBlock(new int[][][]{
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}},
            {{0, 1}, {1, 1}, {1, 0}, {0, 0}},
            {{1, 1}, {1, 0}, {0, 0}, {0, 1}},
            {{1, 0}, {0, 0}, {0, 1}, {1, 1}},
    }, new int[]{0, 3}, CellID.OBLOCK_ID, new BlockColor(
            Color.rgb(255, 255, 0),
            Color.rgb(255, 255, 0),
            Color.rgb(135, 206, 235))),

    SBlock(new int[][][]{
            {{0, 2}, {0, 1}, {1, 1}, {1, 0}},
            {{2, 2}, {1, 2}, {1, 1}, {0, 1}},
            {{2, 0}, {2, 1}, {1, 1}, {1, 2}},
            {{0, 0}, {1, 0}, {1, 1}, {2, 1}},
    }, new int[]{0, 3}, CellID.SBLOCK_ID, new BlockColor(
            Color.rgb(255, 165, 0),
            Color.rgb(128, 0, 128),
            Color.rgb(255, 165, 0))),

    TBlock(new int[][][]{
            {{0, 1}, {1, 0}, {1, 1}, {1, 2}},
            {{1, 2}, {0, 1}, {1, 1}, {2, 1}},
            {{2, 1}, {1, 2}, {1, 1}, {1, 0}},
            {{1, 0}, {2, 1}, {1, 1}, {0, 1}},
    }, new int[]{0, 3}, CellID.TBLOCK_ID, new BlockColor(
            Color.rgb(135, 206, 235),
            Color.rgb(173, 216, 230),
            Color.rgb(255, 255, 224))),

    ZBlock(new int[][][]{
            {{0, 0}, {0, 1}, {1, 1}, {1, 2}},
            {{0, 2}, {1, 2}, {1, 1}, {2, 1}},
            {{2, 2}, {2, 1}, {1, 1}, {1, 0}},
            {{2, 0}, {1, 0}, {1, 1}, {0, 1}},
    }, new int[]{0, 3}, CellID.ZBLOCK_ID, new BlockColor(
            Color.rgb(128, 0, 128),
            Color.rgb(255, 200, 100),
            Color.rgb(192, 192, 192))),

    WeightItemBlock(new int[][][]{
            {{0, 1}, {0, 2}, {1, 0}, {1, 1}, {1, 2}, {1, 3}},
    }, new int[]{0, 3}, CellID.WEIGHT_BLOCK_ID, new BlockColor(
            Color.rgb(255, 255, 255),
            Color.rgb(255, 255, 255),
            Color.rgb(255, 255, 255)));

    Block(int[][][] positions, int[] offset, CellID id, BlockColor blockColor) {
        int row = positions.length;

        cellId = id;
        cells = new BlockPosition[row][];
        startOffset = new BlockPosition(offset[0], offset[1]);

        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();
        Map<String, Object> configs = configRepository.getConfig();
        String colorMode = (String) configs.get("mode");
        if (Objects.equals(colorMode, "Red-green")) {
            this.blockColor = blockColor.getBlockColor(colorMode);
        } else if (Objects.equals(colorMode, "Blue-yellow")) {
            this.blockColor = blockColor.getBlockColor(colorMode);
        } else {
            this.blockColor = blockColor.getBlockColor(colorMode);
        }

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

    public BlockPosition[] shape(int rotate) {
        return cells[rotate % cells.length];
    }
}
