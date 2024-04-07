package org.se13.sqlite.config;

import java.util.HashMap;
import java.util.Map;

public class FakeConfigRepository implements ConfigRepository {


    @Override
    public void insertDefaultConfig(int id) {

    }

    @Override
    public void updateConfig(int id, String mode, int gridWidth, int gridHeight, int keyLeft, int keyRight, int keyDown, int keyRotateLeft, int keyRotateRight, int keyPause, int keyDrop, int keyExit) {

    }

    @Override
    public Map<String, Object> getConfig(int id) {
        Map<String, Object> config = new HashMap<>();
        config.put("screenWidth", 300);
        config.put("screenHeight", 400);
        return config;
    }

    @Override
    public void clearConfig(int id) {

    }
}
