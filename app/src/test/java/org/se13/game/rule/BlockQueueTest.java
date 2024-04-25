package org.se13.game.rule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.game.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("오차범위_5퍼센터_내에서_등장해야합니다.")
    void percentTest() {
        levelPerTest(new BlockQueue(new Random(System.currentTimeMillis()), GameLevel.EASY), 1.2f);
        levelPerTest(new BlockQueue(new Random(System.currentTimeMillis()), GameLevel.NORMAL), 1.0f);
        levelPerTest(new BlockQueue(new Random(System.currentTimeMillis()), GameLevel.HARD), 0.8f);
    }

    private void levelPerTest(BlockQueue queue, float percent) {
        Map<Block, Integer> count = new HashMap<>();
        int number = 100000;

        for (int i = 0; i < number; i++) {
            Block block = queue.nextBlock();
            count.put(block, count.getOrDefault(block, 0) + 1);
        }

        int iBlockCount = count.get(Block.IBlock);
        int otherBlockAverage = (number - iBlockCount) / 6;
        float iBlockPercent = ((float) iBlockCount) / number;
        float otherBlockPercent = ((float) otherBlockAverage) / number;

        assertTrue(otherBlockPercent * (percent - 0.05f) <= iBlockPercent);
        assertTrue(otherBlockPercent * (percent + 0.05f) >= iBlockPercent);
    }
}