package org.se13.game.config;

import org.se13.sqlite.config.ConfigRepositoryImpl;

import java.util.Map;

public class InputConfig {
    public InputConfig() {
        ConfigRepositoryImpl configRepository = ConfigRepositoryImpl.getInstance();
        Map<String, Object> configs = configRepository.getConfig(0);
        DROP = (String)configs.get("keyDrop");
        DOWN = (String)configs.get("keyDown");
        LEFT = (String)configs.get("keyLeft");
        RIGHT = (String)configs.get("keyRight");
        CW_SPIN = (String)configs.get("keyRotateRight");
        PAUSE = (String)configs.get("keyPause");
        EXIT = (String)configs.get("keyExit");
    }

    public static String DROP;
    public static String DOWN;
    public static String LEFT;
    public static String RIGHT;
    public static String CW_SPIN;
    public static String PAUSE;
    public static String EXIT;
}
