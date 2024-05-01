package org.se13.server;

@FunctionalInterface
public interface TetrisActionHandler {

    void request(TetrisActionPacket packet);
}
