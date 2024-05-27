package org.se13.game.event;

import java.io.Serializable;

public record ServerErrorEvent(String message) implements TetrisEvent, Serializable {
}
