package org.se13.server;

import org.se13.game.action.TetrisAction;

public record TetrisActionPacket(int userId, TetrisAction action) {

}
