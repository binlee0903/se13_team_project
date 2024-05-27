package org.se13.online;

import org.se13.game.event.GameEndEvent;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;

import java.io.Serializable;

public record GameInfo(GameLevel level, GameMode mode, GameEndEvent gameEndEvent) implements Serializable {
    public GameInfo {
        if (level == null) {
            throw new IllegalArgumentException("level is null");
        }
        if (mode == null) {
            throw new IllegalArgumentException("mode is null");
        }
    }
}
