package org.se13.game.item;

import org.se13.game.block.CellID;

public interface TetrisItem {
    CellID getId();
    int getPosition();
}
