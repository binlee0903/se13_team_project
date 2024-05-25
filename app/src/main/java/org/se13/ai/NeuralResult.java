package org.se13.ai;

public record NeuralResult(int fitness, Neural neural) {

    public NeuralResult(Neural neural) {
        this(-1, neural);
    }

}
