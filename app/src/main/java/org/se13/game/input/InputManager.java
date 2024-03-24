package org.se13.game.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.LinkedList;
import java.util.Queue;

public class InputManager {
    private InputManager(Scene scene) {
        keyCodeQueue = new LinkedList<>();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            char c = key.getText().charAt(0);
            keyCodeQueue.add(c);
        });
    }

    public static InputManager getInstance(Scene scene) {
        if (inputManager == null) {
            inputManager = new InputManager(scene);
        }

        return inputManager;
    }

    public boolean peekInput() {
        return keyCodeQueue.peek() != null;
    }

    public char getInput() {
        return keyCodeQueue.poll();
    }

    private static InputManager inputManager;
    private Queue<Character> keyCodeQueue;
}
