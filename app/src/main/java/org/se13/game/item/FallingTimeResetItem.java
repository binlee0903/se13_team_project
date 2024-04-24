package org.se13.game.item;

import org.se13.game.block.Block;
import org.se13.game.block.BlockPosition;
import org.se13.game.block.CellID;

import java.util.Random;

public class FallingTimeResetItem implements TetrisItem {
    private final int position;

    public FallingTimeResetItem(Random random, Block block) {
        BlockPosition[] shape = block.shape(0);
        position = random.nextInt(0, shape.length);
    }

    @Override
    public CellID getId() {
        return CellID.RESET_ITEM_ID;
    }

    @Override
    public int getPosition() {
        return position;
    }
}
