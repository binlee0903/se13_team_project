package org.se13.game.action;

import java.util.HashMap;
import java.util.Map;

public enum TetrisAction {
    START(1),
    IMMEDIATE_BLOCK_PLACE(2),
    MOVE_BLOCK_DOWN(3),
    MOVE_BLOCK_LEFT(4),
    MOVE_BLOCK_RIGHT(5),
    ROTATE_BLOCK_CW(6),
    TOGGLE_PAUSE_STATE(7),
    EXIT_GAME(8);

    private int code;

    TetrisAction(int code) {
        this.code = code;
    }

    static {
        Map<Integer, TetrisAction> codeMap = new HashMap<>();
        for (TetrisAction action : values()) {
            codeMap.put(action.code, action);
        }

        TetrisAction.codeMap = codeMap;
    }

    private static Map<Integer, TetrisAction> codeMap;

    public static TetrisAction fromCode(int code) {
        return codeMap.get(code);
    }
}
