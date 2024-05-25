package org.se13.game.event;

public record LineClearedEvent(long cleared) implements TetrisEvent {
}
