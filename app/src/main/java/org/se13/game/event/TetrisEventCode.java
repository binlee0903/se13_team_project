package org.se13.game.event;

public interface TetrisEventCode {
    int EVENT_CODE_READY_FOR_MATCHING = 1;
    int EVENT_CODE_UPDATE_TETRIS_STATE = 2;

    static int getEventCode(TetrisEvent event) {
        return switch (event) {
            case ReadyForMatching e -> EVENT_CODE_READY_FOR_MATCHING;
            case UpdateTetrisState e -> EVENT_CODE_UPDATE_TETRIS_STATE;
            default -> -1;
        };
    }
}
