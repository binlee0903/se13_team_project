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
                case TetrisEventCode.EVENT_CODE_ATTACKED_TETRIS_BLOCKS -> {
                    event = gson.fromJson(response, AttackedTetrisBlocks.class);
                }
                case TetrisEventCode.EVENT_CODE_ATTACKING_TETRIS_BLOCKS -> {
                    event = gson.fromJson(response, AttackingTetrisBlocks.class);
                }
                case TetrisEventCode.EVENT_CODE_INSERT_ATTACK_BLOCKS -> {
                    event = gson.fromJson(response, InsertAttackBlocksEvent.class);
                }
                default -> {
                    event = new ServerErrorEvent("Unknown Code: " + eventCode + " Response: " + response);
                }
            }

            log.info("Player{} resposne {}", userId, event);

            return new TetrisEventPacket(userId, event);
        }
    }
}
