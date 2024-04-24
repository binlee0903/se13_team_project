package org.se13.game.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.LinkedList;
import java.util.Queue;

public class InputManager {
    private InputManager(Scene scene) {
        keyCodeQueue = new LinkedList<>();

        if (scene != null) {
            scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
                keyCodeQueue.add(key.getCode().getName().toLowerCase());
            });
        }
    }

    public static InputManager getInstance(Scene scene) {
        if (inputManager == null) {
            inputManager = new InputManager(scene);
        }

        return inputManager;
    }

    public void reset() {
        inputManager = null;
    }

    public boolean peekInput() {
        return keyCodeQueue.peek() != null;
    }

    public String getInput() {
        return keyCodeQueue.poll();
    }

    private static InputManager inputManager;
    private Queue<String> keyCodeQueue;
}
