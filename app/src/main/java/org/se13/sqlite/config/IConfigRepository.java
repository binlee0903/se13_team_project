package org.se13.sqlite.config;

import java.util.Map;

public interface IConfigRepository {
    void insertDefaultConfig(int id);
    void updateConfig(int id, String mode, int gridWidth, int gridHeight, int keyLeft, int keyRight, int keyDown, int keyRotate, int keyPause);
    Map<String, Object> getConfig(int id);
    void clearConfig(int id);
}
