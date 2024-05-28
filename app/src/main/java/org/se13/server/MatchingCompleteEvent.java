package org.se13.server;

import org.se13.game.event.TetrisEvent;

public record MatchingCompleteEvent(int playerId, int opponentId) implements TetrisEvent {
}
