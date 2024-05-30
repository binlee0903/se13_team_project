package org.se13.game.input;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.game.action.TetrisAction;
import org.se13.game.config.Config;
import org.se13.sqlite.config.ConfigRepository;
import org.se13.sqlite.config.ConfigRepositoryImpl;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InputTest {
    @Test
    @DisplayName("InputManager 클래스 동작 테스트")
    void inputTest() {
        InputManager inputManager = new InputManager();
        inputManager.add(TetrisAction.START);
        assertTrue(inputManager.peekInput());
        assertEquals(inputManager.getInput(), TetrisAction.START);

        inputManager.reset();

        assertFalse(inputManager.peekInput());
        assertNull(inputManager.getInput());
    }

    @Test
    @DisplayName("Config 에 값이 잘 들어갔는지 테스트")
    void configTest() {
        ConfigRepository configRepository = new ConfigRepositoryImpl(0);
        Map<String, Object> configs = configRepository.getConfig();
        assertTrue(Config.DROP.equals(configs.get("keyDrop")));
        assertTrue(Config.DOWN.equals(configs.get("keyDown")));
        assertTrue(Config.LEFT.equals(configs.get("keyLeft")));
        assertTrue(Config.RIGHT.equals(configs.get("keyRight")));
        assertTrue(Config.CW_SPIN.equals(configs.get("keyRotate")));
        assertTrue(Config.PAUSE.equals(configs.get("keyPause")));
        assertTrue(Config.EXIT.equals(configs.get("keyExit")));
        assertTrue(Config.SCREEN_WIDTH == (Integer) configs.get("screenWidth"));
        assertTrue(Config.SCREEN_HEIGHT == (Integer) configs.get("screenHeight"));
    }
}
