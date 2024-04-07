package org.se13.game.block;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.game.item.FeverItem;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CurrentBlockTest {

    @Test
    @DisplayName("블록 리셋 테스트")
    void resetTest() {
        CurrentBlock currentBlock = new CurrentBlock(Block.IBlock);

        currentBlock.rotateCCW();
        currentBlock.move(0, 1);
        currentBlock.reset();

        assertEquals(Block.IBlock.startOffset, currentBlock.getPosition());
        assertEquals(Block.IBlock.shape(0), currentBlock.shape());
    }

    @Test
    @DisplayName("블록 회전 테스트")
    void rotateTest() {
        CurrentBlock currentBlock = new CurrentBlock(Block.IBlock);
        BlockPosition[] initial = currentBlock.shape();

        currentBlock.rotateCW();
        assertSame(currentBlock.shape(), Block.IBlock.shape(1));

        currentBlock.rotateCW();
        currentBlock.rotateCW();
        currentBlock.rotateCW();

        // 4번 회전했다면 원래 모습으로 돌아와야 합니다.
        assertSame(currentBlock.shape(), initial);

        currentBlock.rotateCCW();
        currentBlock.rotateCCW();
        currentBlock.rotateCCW();
        currentBlock.rotateCCW();

        assertSame(currentBlock.shape(), initial);
    }

    @Test
    @DisplayName("아무 아이템이 없을 시 모든 셀의 아이디는 같아야합니다.")
    void nonItemTest() {
        CurrentBlock currentBlock = new CurrentBlock(Block.IBlock);
        Cell[] cells = currentBlock.cells();

        for (int i = 0; i < cells.length - 1; i++) {
            assertSame(cells[i].cellID(), cells[i + 1].cellID());
        }
    }

    @Test
    @DisplayName("피버 아이템이 있을 시 코드가 정상적으로 변형이 되는지 테스트")
    void mutateTest() {
        Random random = new Random();
        CurrentBlock currentBlock = new CurrentBlock(Block.IBlock, new FeverItem(random, Block.IBlock));
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