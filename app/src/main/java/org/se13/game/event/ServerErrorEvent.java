package org.se13.game.event;

public record ServerErrorEvent(String message) implements TetrisEvent {
}
