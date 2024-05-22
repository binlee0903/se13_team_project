package org.se13.game.rule;

import org.se13.game.block.Block;

import java.util.*;

public class BlockQueue {
    protected Map<Block, Integer> blocks;
    protected final Random random;
    protected GameLevel level;
    protected BlockWeight weight;

    private Block[] sample = new Block[]{
        Block.IBlock,
        Block.JBlock,
        Block.LBlock,
        Block.OBlock,
        Block.SBlock,
        Block.TBlock,
        Block.ZBlock,
    };
    private Queue<Block> queue = new ArrayDeque<>(20);

    public BlockQueue(Random random) {
        this(random, GameLevel.NORMAL);
    }

    public BlockQueue(Random random, GameLevel level) {
        this(random, level, BlockWeight.DefaultWeight);
    }

    BlockQueue(Random random, GameLevel level, BlockWeight weight) {
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

    // 너무 랜덤 요소가 심해서 학습이 잘 안되가지고 수정
    public Block nextBlock() {
        if (queue.isEmpty()) {
            List<Block> blocks = Arrays.asList(sample);
            Collections.shuffle(blocks);
            queue.addAll(blocks);
        }

        return queue.poll();
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

        return blocks;
    }
}
