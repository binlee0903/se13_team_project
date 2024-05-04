package org.se13.sqlite.config;

import java.util.Map;

public interface ConfigRepository {
    void insertDefaultConfig();
    void updateConfig(String mode, int gridWidth, int gridHeight, String keyLeft, String keyRight, String keyDown, String keyRotate, String keyPause, String keyDrop, String keyExit);
    Map<String, Object> getConfig();
    void clearConfig();
    int[] getScreenSize();
}
