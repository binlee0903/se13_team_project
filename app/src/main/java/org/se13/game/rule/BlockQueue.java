package org.se13.game.rule;

import org.se13.game.block.Block;

import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;

public class BlockQueue {
    private final Block[] blocks = new Block[]{
            Block.IBlock, Block.JBlock, Block.LBlock,
            Block.OBlock, Block.SBlock, Block.TBlock, Block.ZBlock
    };

    private final Random random;
    private final Queue<Block> queue;

    public BlockQueue(int seed) {
        this.random = new Random(seed);
        this.queue = new ArrayDeque<>();
    }

    public Block nextBlock() {
        if (queue.isEmpty()) {
            List<Block> next = Arrays.asList(blocks);
            Collections.shuffle(next, random);
            queue.addAll(next);
        }

        return queue.poll();
    }
}
