package org.se13.server;

import org.se13.game.action.TetrisAction;

public interface TetrisActionHandler {

    // 원래라면 네트워크 전송을 위해 패킷을 수신해야 하지만 현재론 로컬 게임만 있으므로 TetrisAction을 사용함.
    void handle(TetrisAction request);
}
