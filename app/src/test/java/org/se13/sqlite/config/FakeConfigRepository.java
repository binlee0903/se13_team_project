package org.se13.sqlite.config;

import java.util.HashMap;
import java.util.Map;

public class FakeConfigRepository implements ConfigRepository {


    @Override
    public void insertDefaultConfig(int id) {

    }

    @Override
    public void updateConfig(int id, String mode, int gridWidth, int gridHeight, String keyLeft, String keyRight, String keyDown, String keyRotateLeft, String keyRotateRight, String keyPause, String keyDrop, String keyExit) {

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

    @Override
    public int[] getScreenSize() {
        return new int[]{300, 400};
    }
}
