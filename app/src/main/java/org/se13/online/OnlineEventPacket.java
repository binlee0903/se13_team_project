package org.se13.online;

import org.se13.game.event.TetrisEvent;
import org.se13.view.tetris.TetrisGameEndData;

public record OnlineEventPacket(TetrisEvent event, TetrisGameEndData endData) {
    public OnlineEventPacket {
        if (event == null && endData == null) {
            throw new IllegalArgumentException("event와 endData 중 하나는 null이 아니어야 합니다.");
        }
    }
}
