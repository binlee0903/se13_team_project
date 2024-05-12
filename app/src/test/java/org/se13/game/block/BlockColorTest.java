package org.se13.game.block;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.se13.sqlite.config.ConfigRepository;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.sqlite.config.ConfigRepositoryImplTest;


class BlockColorTest {

    @Test
    void colorTest() {
        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();
        BlockColor blockColor = new BlockColor(configRepository);

        Color color1 = Color.rgb(255,255,255);

        Assertions.assertEquals(color1, blockColor.getBlockColor(CellID.WEIGHT_BLOCK_ID));
    }
}