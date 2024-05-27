package org.se13.server;

import org.se13.game.action.TetrisAction;
import java.io.Serializable;

public record TetrisActionPacket(int userId, TetrisAction action) implements Serializable {

}
