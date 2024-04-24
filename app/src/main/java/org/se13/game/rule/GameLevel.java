package org.se13.game.rule;

public enum GameLevel {
    EASY(1), NORMAL(2), HARD(3);

    GameLevel(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    private final int weight;
}
