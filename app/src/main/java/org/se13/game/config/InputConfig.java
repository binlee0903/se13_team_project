package org.se13.game.config;

import org.se13.sqlite.config.ConfigRepository;
import org.se13.sqlite.config.ConfigRepositoryImpl;

import java.util.Map;

public class InputConfig {
    public InputConfig() {
        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();
        Map<String, Object> configs = configRepository.getConfig(0);
        DROP = (char) ((Integer)configs.get("keyDrop")).intValue();
        DOWN = (char) ((Integer)configs.get("keyDown")).intValue();
        LEFT = (char) ((Integer)configs.get("keyLeft")).intValue();
        RIGHT = (char) ((Integer)configs.get("keyRight")).intValue();
        CCW_SPIN = (char) ((Integer)configs.get("keyRotateLeft")).intValue();
        CW_SPIN = (char) ((Integer)configs.get("keyRotateRight")).intValue();
        PAUSE = (char) ((Integer)configs.get("keyPause")).intValue();
        EXIT = (char) ((Integer)configs.get("keyExit")).intValue();
    }

    public char DROP;
    public char DOWN;
    public char LEFT;
    public char RIGHT;
    public char CW_SPIN;
    public char CCW_SPIN;
    public char PAUSE;
    public char EXIT;
}
