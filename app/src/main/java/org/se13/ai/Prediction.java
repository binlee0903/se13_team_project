package org.se13.ai;

import org.se13.game.action.TetrisAction;
import org.se13.game.block.CurrentBlock;
import org.se13.game.grid.TetrisGrid;

import java.util.List;

public interface Prediction {

    List<TetrisAction> predict(TetrisGrid board, CurrentBlock block, boolean isBattleMode);
}
