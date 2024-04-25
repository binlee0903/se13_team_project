package org.se13.game.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.game.block.Block;
import org.se13.game.block.Cell;
import org.se13.game.block.CellID;
import org.se13.game.block.CurrentBlock;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TetrisItemTest {

    @Test
    @DisplayName("피버 아이템이 있을 시 코드가 정상적으로 변형이 되는지 테스트")
    void mutateTest() {
        Random random = new Random();

        mutateTest(new CurrentBlock(Block.IBlock, new FeverItem(random, Block.IBlock)));
        mutateTest(new CurrentBlock(Block.IBlock, new AllClearItem(random, Block.IBlock)));
        mutateTest(new CurrentBlock(Block.IBlock, new FallingTimeResetItem(random, Block.IBlock)));
        mutateTest(new CurrentBlock(Block.IBlock, new LineClearItem(random, Block.IBlock)));
        mutateTest(new CurrentBlock(Block.IBlock, new WeightItem(random, Block.IBlock)));
    }

    private void mutateTest(CurrentBlock currentBlock) {
        Cell[] cells = currentBlock.cells();

        boolean isMutate = false;

        for (Cell cell : cells) {
            if (cell.cellID() != CellID.IBLOCK_ID) {
                isMutate = true;
            }
        }

        assertTrue(isMutate);
    }
}
