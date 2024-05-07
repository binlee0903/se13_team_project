package org.se13.game.block;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.se13.sqlite.config.ConfigRepository;
import org.se13.sqlite.config.ConfigRepositoryImpl;


class BlockColorTest {

    @Test
    void colorTest() {
        Color color1 = Color.RED;
        Color color2 = Color.BLUE;
        Color color3 = Color.GREEN;
        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();

        BlockColor blockColor = new BlockColor(configRepository);

        Assertions.assertEquals(color1, blockColor.getBlockColor(CellID.IBLOCK_ID));
        Assertions.assertEquals(color2, blockColor.getBlockColor(CellID.IBLOCK_ID));
        Assertions.assertEquals(color3, blockColor.getBlockColor(CellID.IBLOCK_ID));
    }
}