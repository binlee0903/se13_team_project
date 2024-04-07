package org.se13.game.block;

import javafx.scene.paint.Color;
import org.se13.sqlite.config.ConfigRepositoryImpl;

import java.util.Map;
import java.util.Objects;

public class BlockColor {
    public BlockColor(Color defaultColor, Color redGreenColorBlindColor, Color blueYellowColorBlindColor) {
        this.defaultColor = defaultColor;
        this.redGreenColorBlindColor = redGreenColorBlindColor;
        this.blueYellowColorBlindColor = blueYellowColorBlindColor;
    }

    public Color getBlockColor() {
        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();
        Map<String, Object> configs = configRepository.getConfig(0);
        String colorMode = (String) configs.get("mode");
        if (Objects.equals(colorMode, "Red-green")) {
            return redGreenColorBlindColor;
        } else if (Objects.equals(colorMode, "Blue-yellow")) {
            return blueYellowColorBlindColor;
        } else {
            return defaultColor;
        }
    }

    private final Color defaultColor;
    private final Color redGreenColorBlindColor; // 적록색맹용 색상
    private final Color blueYellowColorBlindColor; // 청황색맹용 색상
}
