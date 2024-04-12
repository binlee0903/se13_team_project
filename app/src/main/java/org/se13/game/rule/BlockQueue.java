package org.se13.game.rule;

import org.se13.game.block.Block;

import java.util.*;

public class BlockQueue {
    private Map<Block, Integer> blocks;
    private final Random random;
    private GameLevel level;
    private BlockWeight weight;

    public BlockQueue(Random random) {
        this(random, GameLevel.NORMAL);
    }

    public BlockQueue(Random random, GameLevel level) {
        this(random, level, BlockWeight.DefaultWeight);
    }

    public BlockQueue(Random random, GameLevel level, BlockWeight weight) {
        this.random = random;
        this.level = level;
        this.weight = weight;
        this.blocks = blocks();
    }

    public void setLevel(GameLevel level) {
        this.level = level;
        this.blocks = blocks();
    }

    // 테스트를 위해 package-private를 사용합니다.
    List<Block> nextBlocks() {
        return blocks.entrySet().stream()
            .flatMap((block) -> {
                Block[] b = new Block[block.getValue()];
                Arrays.fill(b, block.getKey());
                return Arrays.stream(b);
            }).toList();
    }

    public Block nextBlock() {
        List<Block> next = nextBlocks();
        return next.get(random.nextInt(next.size()));
    }

    private Map<Block, Integer> blocks() {
        Map<Block, Integer> blocks = new HashMap<>();
        blocks.put(Block.IBlock, weight.of(level));
        blocks.put(Block.JBlock, BlockWeight.Normal);
        blocks.put(Block.LBlock, BlockWeight.Normal);
        blocks.put(Block.OBlock, BlockWeight.Normal);
        blocks.put(Block.SBlock, BlockWeight.Normal);
        blocks.put(Block.TBlock, BlockWeight.Normal);
        blocks.put(Block.ZBlock, BlockWeight.Normal);
//        blocks.put(Block.WeightItemBlock, BlockWeight.Normal);

        return blocks;
    }
}
