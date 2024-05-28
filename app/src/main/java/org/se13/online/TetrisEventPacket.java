package org.se13.online;

import com.google.gson.Gson;
import org.se13.game.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public record TetrisEventPacket(int userId, TetrisEvent event) {

    public static class Builder {
        private static final Logger log = LoggerFactory.getLogger(TetrisEventPacket.Builder.class);

        private ByteBuffer buffer;
        private int userId;
        private int eventCode;

        public Builder(byte[] data) {
            buffer = ByteBuffer.wrap(data);
            userId = buffer.getInt();
            eventCode = buffer.getInt();
        }

        TetrisEventPacket build(Gson gson) {
            String response = StandardCharsets.UTF_8.decode(buffer).toString();
            TetrisEvent event = null;

            switch (eventCode) {
                case TetrisEventCode.EVENT_CODE_READY_FOR_MATCHING -> {
                    event = gson.fromJson(response, ReadyForMatching.class);
                }
                case TetrisEventCode.EVENT_CODE_UPDATE_TETRIS_STATE -> {
                    event = gson.fromJson(response, UpdateTetrisState.class);
                }
                default -> {
                    event = new ServerErrorEvent("알 수 없는 코드: " + eventCode + " 데이터: " + response);
                }
            }

            log.info("Player{} resposne {}", userId, event);

            return new TetrisEventPacket(userId, event);
        }
    }
}
