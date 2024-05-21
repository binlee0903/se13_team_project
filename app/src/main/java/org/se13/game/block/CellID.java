package org.se13.game.block;

public enum CellID {
    EMPTY(0),
    IBLOCK_ID(1),
    JBLOCK_ID(2),
    LBLOCK_ID(3),
    OBLOCK_ID(4),
    SBLOCK_ID(5),
    TBLOCK_ID(6),
    ZBLOCK_ID(7),
    CBLOCK_ID(8),

    // 게임 아이템을 위한 아이디
    FEVER_ITEM_ID(9),
    WEIGHT_ITEM_ID(10),
    WEIGHT_BLOCK_ID(11),
    RESET_ITEM_ID(12),
    LINE_CLEAR_ITEM_ID(13),
    ALL_CLEAR_ITEM_ID(14),

    // 대전 모드를 위한 아이디
    ATTACKED_BLOCK_ID(15);

    public int id;

    CellID(int id) {
        this.id = id;
    }
}