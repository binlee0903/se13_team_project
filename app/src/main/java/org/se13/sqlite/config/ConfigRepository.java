package org.se13.sqlite.config;

import java.util.Map;

public interface ConfigRepository {
    void insertDefaultConfig(int id);
    void updateConfig(int id, String mode, int gridWidth, int gridHeight, int keyLeft, int keyRight, int keyDown, int keyRotateLeft, int keyRotateRight, int keyPause, int keyDrop, int keyExit);
    Map<String, Object> getConfig(int id);
    void clearConfig(int id);
}
