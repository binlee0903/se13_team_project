package org.se13.server;

import org.se13.game.event.TetrisEvent;

@FunctionalInterface
public interface TetrisEventHandler {

    void handle(int userId, TetrisEvent event);
}
