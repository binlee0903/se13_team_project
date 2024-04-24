package org.se13.game.rule;

import org.se13.game.block.Block;
import org.se13.game.item.*;

import java.util.*;

public class ItemQueue {
    private final BlockQueue blocks;
    private final List<TetrisItem> tetrisItems;
    private final Random rand;

    public ItemQueue(GameLevel level) {
        rand = new Random();
        this.blocks = new BlockQueue(rand, level);
        tetrisItems = new LinkedList<>();
        rand.setSeed(System.currentTimeMillis());
        tetrisItems.add(new FeverItem(rand, blocks.nextBlock()));
        tetrisItems.add(new WeightItem(rand, blocks.nextBlock()));
        tetrisItems.add(new FallingTimeResetItem(rand, blocks.nextBlock()));
        tetrisItems.add(new LineClearItem(rand, blocks.nextBlock()));
    }

    public TetrisItem nextItem() {
        return tetrisItems.get(rand.nextInt(tetrisItems.size()));
    }
}
