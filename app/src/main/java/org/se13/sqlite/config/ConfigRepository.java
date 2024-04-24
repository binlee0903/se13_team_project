package org.se13.sqlite.config;

import java.util.Map;

public interface ConfigRepository {
    void insertDefaultConfig(int id);
    void updateConfig(int id, String mode, int gridWidth, int gridHeight, String keyLeft, String keyRight, String keyDown, String keyRotateLeft, String keyRotateRight, String keyPause, String keyDrop, String keyExit);
    Map<String, Object> getConfig(int id);
    void clearConfig(int id);

    int[] getScreenSize();
}
