package org.se13.game.event;

public interface TetrisEventCode {
    int EVENT_CODE_READY_FOR_MATCHING = 1;
    int EVENT_CODE_UPDATE_TETRIS_STATE = 2;
    int EVENT_CODE_ATTACKED_TETRIS_BLOCKS = 3;
    int EVENT_CODE_ATTACKING_TETRIS_BLOCKS = 4;
    int EVENT_CODE_INSERT_ATTACK_BLOCKS = 5;

    static int getEventCode(TetrisEvent event) {
        return switch (event) {
            case ReadyForMatching e -> EVENT_CODE_READY_FOR_MATCHING;
            case UpdateTetrisState e -> EVENT_CODE_UPDATE_TETRIS_STATE;
            case AttackedTetrisBlocks e -> EVENT_CODE_ATTACKED_TETRIS_BLOCKS;
            case AttackingTetrisBlocks e -> EVENT_CODE_ATTACKING_TETRIS_BLOCKS;
            case InsertAttackBlocksEvent e -> EVENT_CODE_INSERT_ATTACK_BLOCKS;
            default -> -1;
        };
    }
}
