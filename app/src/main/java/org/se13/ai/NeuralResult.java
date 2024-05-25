package org.se13.ai;

import java.util.Objects;

public record NeuralResult(int fitness, Neural neural) {

    public NeuralResult(Neural neural) {
        this(-1, neural);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NeuralResult that = (NeuralResult) o;
        return Objects.equals(neural, that.neural);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neural);
    }
}
