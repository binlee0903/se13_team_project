package org.se13.game.config;

import org.se13.sqlite.config.ConfigRepositoryImpl;

import java.util.Map;

public class Config {
    static {
        ConfigRepositoryImpl configRepository = new ConfigRepositoryImpl(0);
        Map<String, Object> configs = configRepository.getConfig();
        DROP = (String)configs.get("keyDrop");
        DOWN = (String)configs.get("keyDown");
        LEFT = (String)configs.get("keyLeft");
        RIGHT = (String)configs.get("keyRight");
        CW_SPIN = (String)configs.get("keyRotate");
        PAUSE = (String)configs.get("keyPause");
        EXIT = (String)configs.get("keyExit");
        SCREEN_WIDTH = (Integer) configs.get("screenWidth");
        SCREEN_HEIGHT = (Integer) configs.get("screenHeight");
    }

    public static String DROP;
    public static String DOWN;
    public static String LEFT;
    public static String RIGHT;
    public static String CW_SPIN;
    public static String PAUSE;
    public static String EXIT;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
}
