package org.se13.game.block;

import javafx.scene.paint.Color;
import org.se13.sqlite.config.ConfigRepository;

public class BlockColor {
    public BlockColor(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public Color getBlockColor(CellID cellID) {
        String colorMode = configRepository.getBlockColorMode();
        return switch (colorMode) {
            case "Red-green" -> getRedGreenColor(cellID);
            case "Blue-yellow" -> getBlueYellowColor(cellID);
            default -> getDefaultColor(cellID);
        };
    }
    private Color getDefaultColor(CellID cellID) {
        return switch (cellID) {
            case IBLOCK_ID -> Color.rgb(0, 0, 255);
            case JBLOCK_ID -> Color.rgb(255, 0, 0);
            case LBLOCK_ID -> Color.rgb(0, 255, 0);
            case OBLOCK_ID -> Color.rgb(255, 255, 0);
            case SBLOCK_ID -> Color.rgb(255, 165, 0);
            case TBLOCK_ID -> Color.rgb(135, 206, 235);
            case ZBLOCK_ID -> Color.rgb(128, 0, 128);
            case WEIGHT_BLOCK_ID -> Color.rgb(255, 255, 255);
            default -> Color.rgb(255, 255, 255);
        };
    }
    private Color getRedGreenColor(CellID cellID) {
        return switch (cellID) {
            case IBLOCK_ID -> Color.rgb(0, 0, 255);
            case JBLOCK_ID -> Color.rgb(255, 192, 203);
            case LBLOCK_ID -> Color.rgb(0, 128, 128);
            case OBLOCK_ID -> Color.rgb(255, 255, 0);
            case SBLOCK_ID -> Color.rgb(128, 0, 128);
            case TBLOCK_ID -> Color.rgb(173, 216, 230);
            case ZBLOCK_ID -> Color.rgb(255, 200, 100);
            case WEIGHT_BLOCK_ID -> Color.rgb(255, 255, 255);
            default -> Color.rgb(255, 255, 255);
        };
    }
    private Color getBlueYellowColor(CellID cellID) {
        return switch (cellID) {
            case IBLOCK_ID -> Color.rgb(255, 0, 0);
            case JBLOCK_ID -> Color.rgb(0, 255, 0);
            case LBLOCK_ID -> Color.rgb(128, 0, 128);
            case OBLOCK_ID -> Color.rgb(135, 206, 235);
            case SBLOCK_ID -> Color.rgb(255, 165, 0);
            case TBLOCK_ID -> Color.rgb(255, 255, 224);
            case ZBLOCK_ID -> Color.rgb(192, 192, 192);
            case WEIGHT_BLOCK_ID -> Color.rgb(255, 255, 255);
            default -> Color.rgb(255, 255, 255);
        };
    }

    private ConfigRepository configRepository;
}
