package org.se13.game.block;

import javafx.scene.paint.Color;

public class BlockColor {
    BlockColor(int red, int green, int blue) {
        this.defaultBlockColor = Color.rgb(red, green, blue);
        this.isColorBlindMode = false;
    }

    public Color getBlockColor() {
        if (this.isColorBlindMode == true) {
            return this.colorBlindModeBlockColor;
        } else {
            return this.defaultBlockColor;
        }
    }

    private Color defaultBlockColor;
    private Color colorBlindModeBlockColor;
    private boolean isColorBlindMode;
}
