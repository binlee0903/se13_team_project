package org.se13.game.rule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.game.block.Block;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BlockQueueTest {

    @Test
    @DisplayName("같은_시드일_경우에는_같은_값을_리턴합니다")
    void seedTest() {
        long seed = System.currentTimeMillis();
        BlockQueue queue1 = new BlockQueue(new Random(seed));
        BlockQueue queue2 = new BlockQueue(new Random(seed));

        assertEquals(queue1.nextBlock(), queue2.nextBlock());
    }

    @Test
    @DisplayName("가중치에_따라_블럭_확률이_조정됩니다")
    void randomTest() {
        int normal = BlockWeight.Normal;

        // case 1: IBlock 가중치만 10이고 다른 블록의 가중치를 0으로 설정할 때 IBlock만 등장해야 합니다.
        BlockWeight.Normal = 0;
        BlockWeight weight = new BlockWeight(10, 0, 0);
        BlockQueue queue = new BlockQueue(new Random(System.currentTimeMillis()), GameLevel.EASY, weight);
        List<Block> next = queue.nextBlocks();

        for (Block block : next) {
            assertEquals(Block.IBlock, block);
        }

        // case 2: IBlock 가중치가 0이고 다른 블록의 가중치가 기본 가중치일 때 IBlock은 등장하지 않습니다.
        BlockWeight.Normal = normal;
        queue.setLevel(GameLevel.NORMAL);
        next = queue.nextBlocks();

        for (Block block : next) {
            assertNotEquals(Block.IBlock, block);
        }
    }
}