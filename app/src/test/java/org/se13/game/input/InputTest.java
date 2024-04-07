package org.se13.game.input;

import javafx.scene.Group;
import javafx.scene.Scene;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InputTest {
    @Test
    @DisplayName("InputManager 클래스 동작 테스트")
    void inputTest() {
        InputManager inputManager = InputManager.getInstance(null);
        inputManager.reset();

        InputManager newInputManager = InputManager.getInstance(null);

        assertNotSame(inputManager, newInputManager);

        assertFalse(newInputManager.peekInput());

    }
}
