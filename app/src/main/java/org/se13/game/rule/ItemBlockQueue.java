package org.se13.game.rule;

import org.se13.game.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ItemBlockQueue extends BlockQueue {
    public ItemBlockQueue(Random random) {
        super(random);

        blocks = blocks();
    }

    private Map<Block, Integer> blocks() {
        Map<Block, Integer> blocks = new HashMap<>();
        blocks.put(Block.WeightItemBlock, BlockWeight.Normal);

        return blocks;
    }
}
