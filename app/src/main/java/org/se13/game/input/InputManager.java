package org.se13.game.input;

import org.se13.game.action.TetrisAction;

import java.util.LinkedList;
import java.util.Queue;

public class InputManager {
    public InputManager() {
        keyCodeQueue = new LinkedList<>();
    }

    public void add(TetrisAction input) {
        keyCodeQueue.add(input);
    }

    public void reset() {
        keyCodeQueue.clear();
    }

    public boolean peekInput() {
        return keyCodeQueue.peek() != null;
    }

    public TetrisAction getInput() {
        return keyCodeQueue.poll();
    }

    private Queue<TetrisAction> keyCodeQueue;
}
