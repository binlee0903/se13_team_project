package org.se13.game.event;

public record ReadyForMatching(int playerId, int opponentId) implements TetrisEvent {
}
