package org.se13.game.item;

import org.se13.game.block.Block;
import org.se13.game.block.BlockPosition;
import org.se13.game.block.CellID;

import java.util.Random;

public class FeverItem implements TetrisItem {
    private final int position;

    public FeverItem(Random random, Block block) {
        BlockPosition[] shape = block.shape(0);
        position = random.nextInt(0, shape.length);
    }

    @Override
    public CellID getId() {
        return CellID.FEVER_ITEM_ID;
    }

    public int getPosition() {
        return position;
    }
}
