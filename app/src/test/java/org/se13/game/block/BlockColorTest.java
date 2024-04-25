package org.se13.game.block;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class BlockColorTest {

    @Test
    void colorTest() {
        Color color1 = Color.RED;
        Color color2 = Color.BLUE;
        Color color3 = Color.GREEN;

        BlockColor blockColor = new BlockColor(color1, color2, color3);

        Assertions.assertEquals(color1, blockColor.getBlockColor(null));
        Assertions.assertEquals(color2, blockColor.getBlockColor("Red-green"));
        Assertions.assertEquals(color3, blockColor.getBlockColor("Blue-yellow"));
    }
}