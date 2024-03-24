package org.se13.game.config;

import org.se13.game.tetris.DefaultTetrisGame;
import org.se13.sqlite.config.ConfigRepository;

import java.util.Map;

public class InputConfig {
    public InputConfig(ConfigRepository configRepository) {
        Map<String, Object> config = configRepository.getConfig(0);
        UP = 'w';
        DOWN = (char) ((Integer)config.get("keyDown")).intValue();
        LEFT = (char) ((Integer)config.get("keyLeft")).intValue();
        RIGHT = (char) ((Integer)config.get("keyRight")).intValue();
        CCW_SPIN = (char) ((Integer)config.get("keyRotateLeft")).intValue();
        CW_SPIN = (char) ((Integer)config.get("keyRotateRight")).intValue();
        PAUSE = (char) ((Integer)config.get("keyPause")).intValue();
        HOLD = 'h';
        EXIT = 'q';
    }

    public char UP;
    public char DOWN;
    public char LEFT;
    public char RIGHT;
    public char CW_SPIN;
    public char CCW_SPIN;
    public char PAUSE;
    public char HOLD;
    public char EXIT;
}
