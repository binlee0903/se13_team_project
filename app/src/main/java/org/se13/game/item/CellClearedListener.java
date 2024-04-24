package org.se13.game.item;

import org.se13.game.block.CellID;

public interface CellClearedListener {
    void clear(CellID cellID);
}
